import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
	Since it's not possible to test the shuffle methods (besides checking if they are not shuffled)
	tests for this class are limited.
 */
public class BaccaratDealerTest
{
	BaccaratDealer dealer;

	@BeforeEach
	void setup ()
	{
		dealer = new BaccaratDealer();
		dealer.generateDeck();
	}

	@Test
	void test1_deckSize ()
	{
		int counter = 0;

		while (dealer.deckSize() > 0)
		{
			Card singleCard = dealer.drawOne();
			assertEquals(counter++, singleCard.id);
		}
	}

	@Test
	void testDraw ()
	{
		// if starting with un-shuffled deck, first should be Ace of Clubs
		assertEquals("Ace of Clubs", dealer.drawOne().suite);
		assertEquals(51, dealer.deckSize());
	}

	@Test
	void testHand ()
	{
		ArrayList<Card> hand = dealer.dealHand();
		assertEquals("Ace of Clubs", hand.get(0).suite);
		assertEquals("2 of Clubs", hand.get(1).suite);
	}
}
