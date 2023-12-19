import javafx.beans.binding.IntegerBinding;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/*
	Button container class - added buttons are arranged vertically.
 */

public class VBoxButtonContainer
{
	VBox buttonContainer;
	Map<String, VBoxButton> buttons;
	Pane pane;

	VBoxButtonContainer (Pane pane)
	{
		this.pane = pane;
		this.buttons = new HashMap<>();

		buttonContainer = new VBox();

		this.pane.getChildren().add(buttonContainer);
	}

	// enabled and sets visible
	void show (String id)
	{
		setEnabled(id, true);
		setVisible(id, true);
	}

	void addButton (String id, String text, EventHandler<MouseEvent> e)
	{
		addButton(id, text, e, true);
	}

	void addButton (String id, String text, EventHandler<MouseEvent> e, boolean enabled)
	{
		VBoxButton newButton = new VBoxButton(text, e, enabled);
		VBox.setMargin(newButton.button(), new Insets(3));

		this.buttons.put(id, newButton);
		this.buttonContainer.getChildren().add(newButton.button());
	}

	void setEnabled (String id, boolean enabled)
	{
		getButtonWithID(id).buttonEnabled(enabled);
	}

	/*
		It actually removes the button from the container, not just hides it.
		This way the container is resized and buttons are aligned like I want.
	 */
	void setVisible (String id, boolean visible)
	{

		if (!visible)
			this.buttonContainer.getChildren().remove(getButtonWithID(id).button());
		else
		{
			VBox buttonToSet = getButtonWithID(id).button();
			if (!this.buttonContainer.getChildren().contains(buttonToSet))
				this.buttonContainer.getChildren().add(buttonToSet);
		}

	}

	/*
		Because I don't want to check for null, return new button that does nothing if ID not found.
	 */
	private VBoxButton getButtonWithID (String id)
	{
		VBoxButton bt = this.buttons.get(id);
		if (bt != null)
			return bt;

		return new VBoxButton("ee", e ->
		{
		});
	}

	void setTooltip (String id, String tip)
	{
		getButtonWithID(id).setTooltip(tip);
	}

	void clearTooltip (String id)
	{
		getButtonWithID(id).clearTooltip();
	}

	void setLabel (String id, String newText)
	{
		getButtonWithID(id).setLabel(newText);
	}

}
