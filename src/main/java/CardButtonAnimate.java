import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/*

	ButtonAnimate extension specifically for animating cards.
	ButtonImage->CardImages + CardButtonAnimate

 */

public class CardButtonAnimate extends ButtonAnimate
{
	private CardImages cardImages;

	CardButtonAnimate (Pane pane)
	{
		try
		{
			cardImages = new CardImages(event -> System.out.println("Clicked on a card ID: " + ((cButton) event.getSource()).getId()));
		}
		catch (Exception ignored)
		{

		}

		this.gamePane = pane;
	}

	public void throwNewCard (Card card, double msDelay)
	{
		cButton button = cardImages.getCardButton(card.id);

		// generate Tooltip
		String pointsWorth = "points.";
		if (card.value == 1)
			pointsWorth = "point.";

		Tooltip tip = new Tooltip(card.suite + "\nThis card is worth " + card.value + " " + pointsWorth);
		tip.setShowDelay(Duration.millis(100));
		tip.setStyle("-fx-font-size: 15");
		button.setTooltip(tip);

		throwNewButton(button, 500, -200, card.posX, card.posY, msDelay);
	}
}
