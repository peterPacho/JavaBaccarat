/*

	This class handles loading card images.
	Images names have format <name>_of_<suite>.png
	Each card object is actually a button.
	Cards are always in order.

 */


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;

/*

	This class collects all card images in ButtonImage objects.

 */

public class CardImages
{
	static final String filePatch = "cards80/";
	ArrayList<ButtonImage> deck;

	// this is basically a copy of "BaccaratDealer.generateDeck()" slightly modified to generate the filenames
	CardImages (EventHandler<MouseEvent> e)
	{
		deck = new ArrayList<>();

		// prepare all possibilities
		ArrayList<String> suites = new ArrayList<>(Arrays.asList("clubs", "diamonds", "hearts", "spades"));
		ArrayList<String> names = new ArrayList<>(Arrays.asList("10", "jack", "queen", "king"));

		// generate card deck
		suites.forEach(suite ->
		{
			for (int i = 1; i < 14; i++)
			{
				String filename;
				if (i == 1)
					filename = filePatch + "ace_of_" + suite + ".png";
				else if (i >= 10)
					filename = filePatch + names.get(i - 10) + "_of_" + suite + ".png";
				else
					filename = filePatch + i + "_of_" + suite + ".png";

				deck.add(new ButtonImage(filename, e));
			}
		});

		// assign IDs to each created button
		int id = 0;
		for (ButtonImage card : deck)
		{
			card.getButton().setId(String.valueOf(id++));
		}
	}

	cButton getCardButton (int id)
	{
		return deck.get(id).getButtonCopy().getButton();
	}

	/*
		Single card data.
	 */
}
