package firviewer.shape

import scalafx.scene.shape.Circle
import scalafx.scene.shape.Line
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Shape

import scalafx.scene.paint.Color._
import scalafx.scene.text.Text
import scalafx.scene.layout._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.beans.property.DoubleProperty
import scalafx.delegate.{DimensionDelegate, PositionDelegate, SFXDelegate}
import scalafx.scene.paint._
import scala.language.implicitConversions
import scalafx.scene.Group
import scala.util.Random

import scalafx.Includes._

import firrtl.{CircuitState, Transform, UnknownForm}
import firrtl.ir._
import firrtl.passes.Pass

class ModulePort ( p : firrtl.ir.Port ) extends Group
{
	p.tpe match {
		case ClockType => {
			var pin = new ModulePin(p.name)
			this.getChildren().add(pin)
		}
		case UIntType(width) => {
			var pin = new ModulePin(p.name)
			this.getChildren().add(pin)
		}
		case BundleType(fields) => {
			//var port = new ModulePort(this,p.name,fields)
			//extractBundle(fields)
		}
		case VectorType(BundleType(fields), size) => {
			//var port = new ModulePort(this,p.name,fields)
			//extractBundle(fields)
		}
		case AnalogType(IntWidth(w)) => {
			if(w==1) {
				var pin = new ModulePin(p.name)
				this.getChildren().add(pin)
			} /* else if (w>1) {
				for ( i <- BigInt(1) to w ) outputPortList(m.name) = outputPortList(m.name) ++ Seq(p.name+"["+(i-1)+"]")
			} */
		}
	}

	/*def extractBundle( fs : Seq[Field] ) {
		for ( p <- fs ) {
			p.tpe match {
				case ClockType => {
					addInputPin(p.name)
				}
				case UIntType(width) => {
					addOutputPin(p.name)
				}
				case BundleType(fields) => {
					extractBundle(fields)
				}
				case VectorType(tpe, size) => {
					addOutputPin(p.name)
				}
				case AnalogType(IntWidth(w)) => {
					if(w==1) {
						addAnalogPin(p.name)
					} /*else if (w>1) {
						for ( i <- BigInt(1) to w ) ret = ret ++ Seq(p.name+"["+(i-1)+"]")
					} */
				}
			}
		}
	}*/
}
