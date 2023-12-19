import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.*;

/*
	Similarly to CardImages, it contains a map of chip images used in the game.
	Key is the chip's value.
 */
public class ChipImage
{
	private static final String filePatch = "chips30/";
	public final ArrayList<Integer> chipVals;
	public final ArrayList<String> chipColors;
	private final Map<Integer, ButtonImage> chips;

	ChipImage (EventHandler<MouseEvent> e)
	{
		chips = new HashMap<>();
		chipVals = new ArrayList<>(Arrays.asList(1000, 500, 250, 100, 50, 25, 20, 10, 5, 1));
		chipColors = new ArrayList<>(Arrays.asList("Yellow", "Purple", "Pink", "Black", "Orange", "Green", "Gray", "Blue", "Red", "White"));

		Iterator<String> colorIterator = chipColors.iterator();

		for (int fileName : chipVals)
		{
			ButtonImage newButton = new ButtonImage(filePatch + fileName + ".png", e);
			Tooltip tip = new Tooltip(colorIterator.next() + " chip is worth $" + fileName);
			tip.setShowDelay(Duration.millis(300));

			newButton.getButton().setTooltip(tip);
			newButton.getButton().value = fileName;

			chips.put(fileName, newButton);
		}
	}

	ChipImage ()
	{
		this(mouseEvent ->
		{
			cButton source = (cButton) mouseEvent.getSource();
			System.out.println("Clicked on chip " + source.toString() + " at (" + source.x + ", " + source.y + ")");
		});
	}

	cButton getChipCopy (int value)
	{
		return chips.get(value).getButtonCopy().getButton();
	}
}
