import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

/*

	Class to create a buttons from the image.
	It actually uses the ImageView object instead of the Button.
	It supports onMouseClick event

 */
public class ButtonImage
{
	private final Image picture;
	private cButton imageView; // this is just an Imageview with extra data, but a Button

	ButtonImage (String filename)
	{
		picture = new Image(filename);
		_ButtonImage();

	}

	ButtonImage (String filename, EventHandler<MouseEvent> mouseEvent)
	{
		this(filename);
		imageView.setOnMouseClicked(mouseEvent);
	}

	/*
	Copy constructor
	 */
	ButtonImage (ButtonImage button)
	{
		this.picture = button.picture;
		_ButtonImage();

		imageView.setTooltip(button.getButton().getTooltip());

		imageView.value = button.getButton().value;
		imageView.setOnMouseClicked(button.getButton().getOnMouseClicked());
	}

	void setEventHandler (EventHandler<MouseEvent> event)
	{
		imageView.setOnMouseClicked(event);
	}

	/*
	Common constructor
	 */
	private void _ButtonImage ()
	{
		imageView = new cButton(picture);
		imageView.setPreserveRatio(true);
		// imageButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: transparent");
	}

	/*
		Use this function to obtain a button representing a complete card.
		Use as CardImages.getCard(id).getButton()
	 */
	cButton getButton ()
	{
		return this.imageView;
	}

	ButtonImage getButtonCopy ()
	{
		return new ButtonImage(this);
	}
}
