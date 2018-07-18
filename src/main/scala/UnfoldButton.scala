package firviewer.shape

// for buttong drawing
import scalafx.scene.shape.Circle
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color._
import scalafx.scene.Group

// event stuff
import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.event._
import scalafx.event.Event._
import scalafx.event.ActionEvent

class ModuleUnfoldButton(parent: Module) extends Group(parent)
{
	var m_x = 0.0
	var m_y = 0.0
	var m_collapsed = true

	var m_button_base = new Rectangle {
		x = 0
		y = 0
		width = 10.0
		height = 10.0
		fill = Green
		stroke = Green
		strokeWidth = 1
	}
	this.getChildren().add(m_button_base)

	onMouseClicked =  (e: MouseEvent) => {
		println("Event "+parent.name())
		println(e.clickCount)
	}
}
