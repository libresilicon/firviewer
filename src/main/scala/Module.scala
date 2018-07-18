package firviewer.shape

import scalafx.scene.shape.Circle
import scalafx.scene.shape.Line
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Shape

import scalafx.scene.control.Button

import scalafx.scene.paint.Color._
import scalafx.scene.text.Text
import scalafx.scene.layout._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.beans.property.DoubleProperty
import scalafx.delegate.{DimensionDelegate, PositionDelegate, SFXDelegate}
import scalafx.scene.paint._
import scalafx.scene.Group

import scala.util.Random
import scala.collection.mutable
import scala.language.implicitConversions

// effects
import scalafx.scene.effect.BoxBlur
import scalafx.scene.effect.Glow

// event stuff
import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.event._
import scalafx.event.Event._
import scalafx.event.ActionEvent

// cicruit stuff
import firrtl.{CircuitState, Transform, UnknownForm}
import firrtl.ir._
import firrtl.passes.Pass

import firviewer.shape.PortSide._

class Module (m: DefModule) extends Group
{
	var m_spacing = 50.0
	var m_pin_side = 10.0
	var m_name = m.name

	//effect = new Glow(10.0)

	// all the submodules
	var m_children = List.empty[Module]

	// pin ports
	var m_left_pins = List.empty[ModulePin]
	var m_right_pins = List.empty[ModulePin]
	var m_bottom_pins = List.empty[ModulePin]
	var m_top_pins = List.empty[ModulePin]

	var m_unfold_button = new ModuleUnfoldButton(this)
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
	//this.getChildren().add(m_unfold_button)

	this.parsePins(m)

	def parsePins(m: DefModule) {
		for (p <- m.ports) {
			var port = new ModulePort(p)
			this.getChildren().add(port)
		}
	}

	def dumpLoc(loc: Expression, acc: String): String = {
		loc match {
			case r: Reference => r.name + acc
			case s: SubField => dumpLoc(s.expr, acc) + "." + s.name
			case i: SubIndex => dumpLoc(i.expr, acc) + "[" + i.value + "]"
			case s => acc
		}
	}

	def setInstanceName(s: String) {
	}


	def name() : String = {
		return m_name
	}
	
	def addNetListConnection( from : String , to : String ) {
		println("Connectin "+from+" to "+to+" within "+m_name)
		for ( m <- m_children ) {
			m.addNetListConnection(from, to)
		}
		
	}

	def autoPlaceSubModules() {
		var mx = m_spacing
		var my = m_spacing
		for ( m <- m_children ) {
			//my = m.getBoundsInLocal().getHeight()
			m.relocate( mx , my )
			mx += m.getBoundsInLocal().getWidth()
			mx += m_spacing
		}
		autoScaleBoundingBox()
	}

	def addSubmodule(m: Module) {
		this.getChildren().add(m)
		m_children = m_children ++ List(m)
		autoPlaceSubModules()
	}

	def autoScaleBoundingBox() {
		m_rectangle.width() = this.getBoundsInLocal().getWidth() + m_spacing
		m_rectangle.height() = this.getBoundsInLocal().getHeight()
	}
}
