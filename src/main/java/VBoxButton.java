import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class VBoxButton
{
	private VBox button;
	private Label label;
	private EventHandler<MouseEvent> event;
	private boolean enabled;
	Tooltip tip;

	VBoxButton (String label, EventHandler<MouseEvent> event)
	{
		this(label, event, true);
	}

	VBoxButton (String label, EventHandler<MouseEvent> event, boolean enabled)
	{
		button = new VBox();

		this.label = new Label(label);
		VBox.setMargin(this.label, new Insets(2, 5, 2, 5));
		button.getChildren().add(this.label);

		button.setOnMouseClicked(event);
		this.event = event;

		buttonEnabled(enabled);
	}

	VBox button ()
	{
		return this.button;
	}

	void buttonEnabled (boolean enabled)
	{
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;

		if (this.enabled)
		{
			button.setOnMouseClicked(event);

			button.getStyleClass().clear();
			button.getStyleClass().add("vbox_button");
			label.getStyleClass().remove("vbox_button_disabled_label");
		}
		else
		{
			button.setOnMouseClicked(e ->
			{
			});

			button.getStyleClass().clear();
			button.getStyleClass().add("vbox_button_disabled");
			label.getStyleClass().add("vbox_button_disabled_label");
		}
	}

	void setTooltip (String tipText)
	{
		tip = new Tooltip(tipText);
		tip.setShowDelay(Duration.millis(200));
		Tooltip.install(this.button, this.tip);
	}

	void clearTooltip ()
	{
		Tooltip.uninstall(this.button, this.tip);
		this.tip = null;
	}

	void setLabel (String label)
	{
		this.label.setText(label);
	}
}
