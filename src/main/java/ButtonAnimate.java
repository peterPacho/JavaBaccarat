import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;

/*
	General button animating class. Use it to "throw" a button to the screen.

	The way to animate cards found at:
	https://stackoverflow.com/questions/29594707/moving-a-button-to-specified-coordinates-in-javafx-with-a-path-transition-using
 */
public class ButtonAnimate
{
	EventHandler<ActionEvent> cardEvent;// common event handler for all cards
	Pane gamePane; // buttons will be drawn on this pane
	ArrayList<cButton> buttonsDisplayed; // list of currently displayed buttons

	// Constructor that overrides the default event handler.
	ButtonAnimate (Pane gamePane, EventHandler<ActionEvent> e)
	{
		this.gamePane = gamePane;

		buttonsDisplayed = new ArrayList<>();
		// setup card event handler

		cardEvent = e;
	}

	// Default constructor - do nothing on card click
	ButtonAnimate (Pane gamePane)
	{
		this(gamePane, actionEvent ->
		{
		});
	}

	ButtonAnimate ()
	{
		this(null);
	}

	void removeAllButtons ()
	{
		gamePane.getChildren().removeAll(buttonsDisplayed);
		buttonsDisplayed.clear();
	}

	/*
		Like throwNewButton but doesn't animate it, just adds it to the screen
	 */
	void placeNewButton (cButton button, double x, double y)
	{
		throwNewButton(button, x, y, x, y, 0);
	}

	void throwNewButton (cButton button, double startX, double startY, double endX, double endY, double msDelay)
	{
		buttonsDisplayed.add(button);
		gamePane.getChildren().add(button);

		relocateButton(button, startX, startY, endX, endY, msDelay);
	}

	/*
		Animates the position change without adding/removing the button
	 */
	static void relocateButton (cButton button, double startX, double startY, double endX, double endY, double msDelay)
	{
		button.relocate(startX, startY);

		// save the button's absolute location - useful when transferring between containers
		button.x = endX;
		button.y = endY;

		// if animation not required
		if (startX == endX && startY == endY)
		{
			button.relocate(endX, endY);
			return;
		}

		// prepare the transition
		PathTransition trans = new PathTransition();
		trans.setNode(button);
		trans.setDuration(Duration.millis(300));

		Path path = new Path();
		path.getElements().add(new MoveToAbs(button));
		path.getElements().add(new LineToAbs(button, endX, endY));

		trans.setDelay(Duration.millis(msDelay));
		trans.setPath(path);
		trans.play();
	}

	public ArrayList<cButton> getAllButtons ()
	{
		return this.buttonsDisplayed;
	}

	boolean removeButton (cButton button)
	{
		if (buttonsDisplayed.remove(button))
		{
			gamePane.getChildren().remove(button);
			return true;
		}
		return false;
	}

	public Pane getPane ()
	{
		return this.gamePane;
	}

	// stuff from stack overflow post mentioned above
	public static class MoveToAbs extends MoveTo
	{
		public MoveToAbs (Node node)
		{
			super(node.getLayoutBounds().getWidth() / 2, node.getLayoutBounds().getHeight() / 2);
		}

		public MoveToAbs (Node node, double x, double y)
		{
			super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
		}
	}

	public static class LineToAbs extends LineTo
	{
		public LineToAbs (Node node, double x, double y)
		{
			super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
		}
	}
}
