package firviewer

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import firrtl.{CircuitState, Transform, UnknownForm}
import firrtl.ir._
import firrtl.passes.Pass

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.stage.Stage
import scalafx.scene.layout._
import scalafx.Includes._

import firviewer.shape.Module

object Main extends JFXApp {
	if ( ( parameters.named contains "fir" ) && ( parameters.named contains "top" ) ){
		var firFile = parameters.named("fir")
		var topLevelName = parameters.named("top")
		val w = 1000
		val h = 1000
		var depth = 3
		if ( parameters.named contains "depth" ) depth = parameters.named("depth").toInt

		val input: String = scala.io.Source.fromFile(firFile).mkString
		val state = CircuitState(firrtl.Parser.parse(input), UnknownForm)
		val transforms = Seq(DiagramData)
		transforms.foldLeft(state) {
			(c: CircuitState, t: Transform) => {
				t.runTransform(c)
			}
		}
		val modules = for ( graph <- transforms ) yield graph.getSceneContent(depth, topLevelName)
		for ( mod <- modules ) mod.setPos(50,50)
		stage = new PrimaryStage {
			title.value = "FIR viewer"
			width = w
			height = h
			scene = new Scene {
				content = modules
			}
		}
	} else {
		println("Usage: runMain firviewer.Main --top=<top model> --fir=<fir file>")
	}
}
