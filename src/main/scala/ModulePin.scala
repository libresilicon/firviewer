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

class ModulePin (parent : Module, s: String, l : Double) extends Group
{
	var m_x = 0.0
	var m_y = 0.0
	var m_parent = parent
	var m_nameLabel = new Text(0, 0, s)
        var m_rectangle = new Rectangle {
		x = 0
		y = 0
		width = l
		height = l
		fill = Black
		stroke = Black
		strokeWidth = 1
	}
	this.getChildren().add(m_nameLabel)
	this.getChildren().add(m_rectangle)

	// relative positions
	def x() : Double = {
		return m_x
	}

	def y() : Double = {
		return m_y
	}

	// absolute positions
	def absoluteX() : Double = {
		if( m_parent == this ) return m_x
		return m_x + m_parent.absoluteX()
	}

	def absoluteY() : Double = {
		if( m_parent == this ) return m_y
		return m_y + m_parent.absoluteY()
	}

	// set relative positions
	def setPos(x : Double, y : Double) {
		var absx = m_parent.absoluteX() + x
		var absy = m_parent.absoluteY() + y

		// setting absolute position
		m_rectangle.x() = absx
		m_rectangle.y() = absy
		m_nameLabel.x() = absx
		m_nameLabel.y() = absy

		// updating relative position
		m_x = x
		m_y = y
	}
}
