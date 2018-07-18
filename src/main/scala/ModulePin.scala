package firviewer.shape

import firviewer.{shape => jfxss}

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

object PortSide extends Enumeration {
	type PortSide = Value
	val LEFT, RIGHT, TOP, BOTTOM, NONE = Value
}
import PortSide._

class ModulePin(s: String) extends Group
{
	var l = 10.0
	var m_nameLabel = new Text(0, l, s)
	this.getChildren().add(m_nameLabel)
	var rect = new Rectangle {
		//x = m_nameLabel.getBoundsInLocal().getWidth() + l
		x = 0
		y = 0
		width = l
		height = l
		fill = Black
		stroke = Black
		strokeWidth = 1
	}
	this.getChildren().add(rect)
}
