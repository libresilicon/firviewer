package firviewer

import firrtl.{CircuitState, Transform, UnknownForm}
import firrtl.ir._
import firrtl.passes.Pass

import firviewer.shape.Module

import scalafx.scene.Group

import scala.collection.mutable

object DiagramData extends Pass {
	override def name = "DiagramData"

	var modules : Seq[DefModule] = Seq.empty[DefModule]

	val m_graphModules : mutable.Map[String, Module] = mutable.HashMap.empty[String, Module]

	def dumpStmts(p : Module, m: DefModule)(s: Statement): Statement = {
		s match {
			case s: Connect => {
				//dumpConnect(m)(s)
				//println(s.name)
			}
			case s: DefInstance => {
				for ( mod <- modules ) {
					if( mod.name == s.module ) {
						var node = new Module(mod)
						node.setInstanceName(s.name)
						p.addSubmodule(node)
					}
				}
			}
			case s => {
				s mapStmt dumpStmts(p, m)
			}
		}
		s
	}

	def genModuleNode(m: DefModule) {
		m_graphModules(m.name) = new Module(m)
		m mapStmt dumpStmts(m_graphModules(m.name), m)
	}

	def getSceneContent(name: String) : Module = {
		return m_graphModules(name)
	}

	override def run(c: Circuit): Circuit = {
		modules = c.modules
		c.modules foreach genModuleNode
		c
	}
}
