import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BaccaratDealer
{
	private ArrayList<Card> deck;

	/*
	Generate standard 52 card deck. Cards are in order Clubs, Diamonds, Hearts, Spades.
	Cards are not shuffled. If you need to have shuffled deck, call only the shuffleDeck() method!
	 */
	public void generateDeck ()
	{
		deck = new ArrayList<>();
		// prepare all possibilities
		ArrayList<String> suites = new ArrayList<>(Arrays.asList("Clubs", "Diamonds", "Hearts", "Spades"));
		ArrayList<String> names = new ArrayList<>(Arrays.asList("10", "Jack", "Queen", "King"));

		// generate card deck
		suites.forEach(suite ->
		{
			for (int i = 1; i < 14; i++)
			{
				if (i == 1)
					deck.add(new Card("Ace of " + suite, 1));
				else if (i >= 10)
					deck.add(new Card(names.get(i - 10) + " of " + suite, 0));
				else
					deck.add(new Card(i + " of " + suite, i));
			}
		});

		// assign ids that will be later used to get images
		int counter = 0;
		for (Card card : deck)
		{
			card.id = counter;
			counter++;
		}
	}

	/*
	Returns two cards from top of the deck, removing them from the deck.
	 */
	public ArrayList<Card> dealHand ()
	{
		return new ArrayList<>(Arrays.asList(deck.remove(0), deck.remove(0)));
	}

	/*
	Returns a single card from top of the deck, removing that card from the deck.
	 */
	public Card drawOne ()
	{
		return deck.remove(0);
	}

	/*
	Re-creates the deck and shuffles the cards.
	 */
	public void shuffleDeck ()
	{
		generateDeck();
		Collections.shuffle(deck);
	}

	/*
	Returns how many cards are left in the deck
	 */
	public int deckSize ()
	{
		return deck.size();
	}

}
