import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BaccaratGameLogicTest
{
	ArrayList<Card> playerHand, bankerHand;

	@BeforeEach
	void setup ()
	{
		playerHand = new ArrayList<>();
		bankerHand = new ArrayList<>();
	}

	@Test
	void testWhoWon1 ()
	{
		playerHand.add(new Card("name", 3));

		assertEquals("Player", BaccaratGameLogic.whoWon(playerHand, bankerHand));
	}

	@Test
	void testWhoWon2 ()
	{
		bankerHand.add(new Card("name", 3));

		assertEquals("Banker", BaccaratGameLogic.whoWon(playerHand, bankerHand));
	}

	@Test
	void testWhoWon3 ()
	{
		assertEquals("Draw", BaccaratGameLogic.whoWon(playerHand, bankerHand));
	}

	@Test
	void testPlayerDraw ()
	{
		playerHand.add(new Card("e", 1));
		playerHand.add(new Card("e", 1));

		assertTrue(BaccaratGameLogic.evaluatePlayerDraw(playerHand));
	}

	@Test
	void testPlayerDraw2 ()
	{
		playerHand.add(new Card("", 7));
		playerHand.add(new Card("e", 1));
		assertFalse(BaccaratGameLogic.evaluatePlayerDraw(playerHand));
	}

	@Test
	void testBankerDraw ()
	{
		bankerHand.add(new Card("", 0));
		assertTrue(BaccaratGameLogic.evaluateBankerDraw(bankerHand, new Card("", 0)));
	}

	@Test
	void testBankerDraw2 ()
	{
		bankerHand.add(new Card("", 9));
		assertFalse(BaccaratGameLogic.evaluateBankerDraw(bankerHand, new Card("", 0)));
	}
}
