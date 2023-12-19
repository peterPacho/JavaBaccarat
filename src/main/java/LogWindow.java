/*

	This class shows a window where text entries can be added.

 */

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LogWindow
{
	private ScrollPane scroll;
	private VBox vbox;
	private double width;

	LogWindow ()
	{
		scroll = new ScrollPane();
		scroll.setFitToWidth(true);

		vbox = new VBox();
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		vbox.setFillWidth(true);

		scroll.setContent(vbox);


	}

	void setSize (double width, double height)
	{
		scroll.setPrefSize(width, height);
		vbox.setMinHeight(height - 10);
		this.width = width;
	}

	void relocate (double x, double y)
	{
		scroll.relocate(x, y);
	}

	void add (String text)
	{
		Label newLab = new Label(text + "  ");
		newLab.setMaxWidth(this.width);
		newLab.setAlignment(Pos.CENTER_RIGHT);
		newLab.setStyle("-fx-font-size: 17px");

		vbox.getChildren().add(newLab);
		autoScroll(true);
	}

	void autoScroll (boolean auto)
	{
		if (auto)
			scroll.vvalueProperty().bind(vbox.heightProperty());
		else
			scroll.vvalueProperty().unbind();
	}

	ScrollPane getScrollPane ()
	{
		return this.scroll;
	}
}
