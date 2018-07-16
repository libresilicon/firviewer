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

class Module (s: String) extends Group
{
	var m_pin_side = 10.0
	var m_parent = this
	var m_x = 0.0
	var m_y = 0.0
	var m_name = s
	var m_children = List.empty[Module]
	var m_input_pins = List.empty[ModulePin]
	var m_output_pins = List.empty[ModulePin]
	var m_nameLabel = new Text(0, 0, m_name)
        var m_rectangle = new Rectangle {
		x = 0
		y = 0
		width = 20
		height = 20
		fill = White
		stroke = Black
		strokeWidth = 1
	}
	this.getChildren().add(m_nameLabel)
	this.getChildren().add(m_rectangle)

	def name() : String = {
		return m_name
	}
	
	def setParent ( m : Module ) {
		m_parent = m
	}

	def addNetListConnection( from : String , to : String ) {
		println("Connectin "+from+" to "+to+" within "+m_name)
		for ( m <- m_children ) {
			m.addNetListConnection(from, to)
		}
		
	}

	def autoPlaceSubModules() {
		// try to place submodules
		val r = scala.util.Random
		var mx = r.nextDouble*m_rectangle.width()
		var my = r.nextDouble*m_rectangle.height()
		for ( i <- 1 to 100 ) {
			for ( m <- m_children ) {
				for ( mod <- m_children ) {
					if ( mod.isOverlapping(m) ) {
						mx = r.nextDouble*m_rectangle.width()
						my = r.nextDouble*m_rectangle.height()
					}
				}
				m.setPos( mx , my )
			}
		}
	}

	def addSubmodule(m: Module) {
		m.setParent ( this )
		m_children = m_children ++ List(m)
		autoScaleBoundingBox()
		autoPlaceSubModules()
		this.getChildren().add(m)
	}

	def autoScaleBoundingBox() {
		// refit box to wrap all children:
		var nw = 0.0
		var nh = 0.0
		for ( mod <- m_children ) {
			nw = nw + mod.getWidth()
			nh = nh + mod.getHeight()
		}
		m_rectangle.width() = nw
		m_rectangle.height() = nh

		var io_port_width = m_pin_side
		for( pin <- m_input_pins ) {
			io_port_width = io_port_width + m_pin_side*2
		}
		if(m_rectangle.width() < io_port_width) m_rectangle.width() = io_port_width
		if(m_rectangle.height() < io_port_width) m_rectangle.height() = io_port_width

		io_port_width = m_pin_side
		for( pin <- m_output_pins ) {
			io_port_width = io_port_width + m_pin_side*2
		}
		if(m_rectangle.width() < io_port_width) m_rectangle.width() = io_port_width
		if(m_rectangle.height() < io_port_width) m_rectangle.height() = io_port_width
	}

	def autoPlaceOutputPins() {
		var pinpos = m_pin_side
		for( pin <- m_output_pins ) {
			pin.setPos(m_rectangle.width(), pinpos )
			pinpos = pinpos + m_pin_side*2
		}
	}

	def autoPlaceInputPins() {
		var pinpos = m_pin_side
		for( pin <- m_input_pins ) {
			pin.setPos(-m_pin_side, pinpos )
			pinpos = pinpos + m_pin_side*2
		}
	}

	def addInputPin(s: String) {
		var pin = new ModulePin(this,s,m_pin_side)
		this.getChildren().add(pin)
		m_input_pins = m_input_pins ++ List(pin)
		autoScaleBoundingBox()
		autoPlaceInputPins()
	}

	def addOutputPin(s: String) {
		var pin = new ModulePin(this,s,m_pin_side)
		this.getChildren().add(pin)
		m_output_pins = m_output_pins ++ List(pin)
		autoScaleBoundingBox()
		autoPlaceOutputPins()
	}

	def getWidth() : Double = {
		return m_rectangle.width()
	}

	def getHeight() : Double = {
		return m_rectangle.height()
	}

	def isOverlapping (m : Module) : Boolean = {
		val x1 = this.absoluteX()
		val x2 = this.absoluteX() + m_rectangle.width()
		val y1 = this.absoluteY()
		val y2 = this.absoluteY() + m_rectangle.height()
		val x = m.absoluteX()
		val y = m.absoluteY()
		return (((x1<=x)&&(x<=x2))&&((y1<=y)&&(y<=y2)))
	}

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

		autoScaleBoundingBox()
		autoPlaceInputPins()
		autoPlaceOutputPins()
		autoPlaceSubModules()
	}
}

/*class Arrow(from:Circle,to:Circle) extends Line {
	startX <== from.centerX + from.translateY + from.layoutY
	startY <== from.centerY + from.translateY + from.layoutY
	endX <== to.centerX + to.translateX + to.layoutX
	endY <== to.centerY + to.translateY + to.layoutY
}*/
