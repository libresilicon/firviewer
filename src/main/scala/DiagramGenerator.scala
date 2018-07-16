package firviewer

import firrtl.{CircuitState, Transform, UnknownForm}
import firrtl.ir._
import firrtl.passes.Pass

import firviewer.shape.Module

import scala.collection.mutable

object DiagramData extends Pass {
	override def name = "DiagramData"

	def dumpLoc(loc: Expression, acc: String): String = {
		loc match {
			case r: Reference => r.name + acc
			case s: SubField => dumpLoc(s.expr, acc) + "." + s.name
			case i: SubIndex => dumpLoc(i.expr, acc) + "[" + i.value + "]"
			case s => acc
		}
	}

	def dumpConnect(m: DefModule)(c: Connect): Unit = {
		if (connections contains m.name) {
			connections(m.name) = connections(m.name) :+ (dumpLoc(c.loc, ""), dumpLoc(c.expr, ""))
		} else {
			connections(m.name) = Seq((dumpLoc(c.loc, ""), dumpLoc(c.expr, "")))
		}
	}

	def dumpStmts(m: DefModule)(s: Statement): Statement = {
		s match {
			case s: Connect => dumpConnect(m)(s)
			case s: DefInstance => {
				if (hierarchy contains m.name) {
					hierarchy(m.name) = hierarchy(m.name) :+ (s.module, s.name)
				} else {
					hierarchy(m.name) = Seq((s.module, s.name))
				}

			}
			case s => {
				s mapStmt dumpStmts(m)
			}
		}
		s
	}

	def getField(f: Field, acc: String): String = {
		getType(f.tpe, acc + "." + f.name)
	}

	def getType(t: Type, acc: String): String = {
		t match {
			case b: BundleType => {
				(b.fields map (f => getField(f, acc))).mkString("|")
			}
			case c: UIntType => {
				acc
			}
			case v: VectorType => {
				(for (i <- 0 until v.size) yield getType(v.tpe, acc + "[" + i + "]")).mkString("|")
			}
			case b: Type => acc
		}
	}

	def getPortName(p: Port): String = {
		getType(p.tpe, p.name)
	}

	val hierarchy: mutable.Map[String, Seq[(String, String)]] = mutable.HashMap.empty[String, Seq[(String, String)]]
	val connections: mutable.Map[String, Seq[(String, String)]] = mutable.HashMap.empty[String, Seq[(String, String)]]
	val clkPortList: mutable.Map[String, Seq[String]] = mutable.HashMap.empty[String, Seq[String]]
	val inputPortList: mutable.Map[String, Seq[String]] = mutable.HashMap.empty[String, Seq[String]]
	val outputPortList: mutable.Map[String, Seq[String]] = mutable.HashMap.empty[String, Seq[String]]

	def extractBundle( fs : Seq[Field] ) : Seq[String] = {
		var ret = Seq.empty[String]
		for ( p <- fs ) {
			p.tpe match {
				case ClockType => {
					ret = ret ++ Seq(p.name)
				}
				case UIntType(width) => {
					ret = ret ++ Seq(p.name)
				}
				case BundleType(fields) => {
					ret = ret ++ (for ( pname <- extractBundle(fields) ) yield p.name+"."+pname)
				}
				case VectorType(tpe, size) => {
					ret = ret ++ Seq(p.name)
				}
				case AnalogType(IntWidth(w)) => {
					if(w==1) {
						ret = ret ++ Seq(p.name)
					} else if (w>1) {
						for ( i <- BigInt(1) to w ) ret = ret ++ Seq(p.name+"["+(i-1)+"]")
					}
				}
			}
		}
		return ret
	}

	def dumpModule(m: DefModule): Unit = {
		m mapStmt dumpStmts(m)
		val names = (m.ports map (p => getPortName(p))).mkString("|")
	
		inputPortList(m.name) = Seq()
		outputPortList(m.name) = Seq()
		clkPortList(m.name) = Seq()

		for (p <- m.ports) {
			p.tpe match {
				case ClockType => {
					clkPortList(m.name) = clkPortList(m.name) ++ Seq(p.name)
				}
				case UIntType(width) => {
					if(p.direction==Input) inputPortList(m.name) = inputPortList(m.name) ++ Seq(p.name)
					if(p.direction==Output) outputPortList(m.name) = outputPortList(m.name) ++ Seq(p.name)
				}
				case BundleType(fields) => {
					outputPortList(m.name) = outputPortList(m.name) ++ (for ( pname <- extractBundle(fields) ) yield p.name+"."+pname)
				}
				case VectorType(BundleType(fields), size) => {
					outputPortList(m.name) = outputPortList(m.name) ++ (for ( pname <- extractBundle(fields) ) yield p.name+"."+pname)
				}
				case AnalogType(IntWidth(w)) => {
					if(w==1) {
						outputPortList(m.name) = outputPortList(m.name) ++ Seq(p.name)
					} else if (w>1) {
						for ( i <- BigInt(1) to w ) outputPortList(m.name) = outputPortList(m.name) ++ Seq(p.name+"["+(i-1)+"]")
					}
				}
			}
		}
	}

	def sanitize(s: String): String = {
		s.replace(".", "_").replace("[", "_").replace("]", "")
	}

	def getSubModuleContent(depth: Int, instanceName: String, moduleName: String) : Module = {
		var node = new Module(instanceName)
		attachPins(node)
		if ( depth == 0 ) {
			println("Maximum depth reached")
		} else if ( hierarchy contains moduleName ) {
			for ( submodule <- hierarchy(moduleName) ) node.addSubmodule(getSubModuleContent(depth-1,submodule._1,submodule._2))
		} else if ( hierarchy contains instanceName ) {
			for ( submodule <- hierarchy(instanceName) ) node.addSubmodule(getSubModuleContent(depth-1,submodule._1,submodule._2))
		} else {
			println(instanceName+" nor "+moduleName+" is not within hierarchy")
		}
		return node
	}

	def attachPins(node : Module) {
		if ( inputPortList contains node.name() ) for ( pn <- inputPortList(node.name()) ) node.addInputPin(pn)
		if ( outputPortList contains node.name() ) for ( pn <- outputPortList(node.name()) ) node.addOutputPin(pn)
	}

	def getSceneContent(depth: Int, name: String) : Module = {
		var node = new Module(name)
		attachPins(node)
		for ( submodule <- hierarchy(name) ) node.addSubmodule(getSubModuleContent(depth,submodule._1,submodule._2))
		return node
	}

	override def run(c: Circuit): Circuit = {
		c.modules foreach dumpModule // generates the data
		c
	}
}
