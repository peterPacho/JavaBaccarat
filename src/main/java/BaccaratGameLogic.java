import java.util.ArrayList;
import java.util.Collections;

/*

	This class contains logic checks that direct the game round progress.

 */
public class BaccaratGameLogic
{
	/*
	whoWon will evaluate two hands at the end of the game and return a string
	depending on the winner: “Player”, “Banker”, “Draw”.

	Not used in the UI of the game. What's the point?
	 */
	public static String whoWon (ArrayList<Card> playerHand, ArrayList<Card> bankerHand)
	{
		int hand1Points = handTotal(playerHand);
		int hand2Points = handTotal(bankerHand);

		if (hand1Points > hand2Points)
			return "Player";
		else if (hand2Points > hand1Points)
			return "Banker";

		return "Draw";
	}

	/*
		Returns how many points given hand is worth.
	 */
	public static int handTotal (ArrayList<Card> hand)
	{
		int sum = 0;

		for (Card card : hand)
		{
			sum += card.value;
		}

		return sum % 10;
	}

	/*
		Returns true if banker should draw another card.
		playerCard is card that was drawn by player, null if player didn't draw third card
	 */
	public static boolean evaluateBankerDraw (ArrayList<Card> hand, Card playerCard)
	{
		int points = handTotal(hand);

		if (points >= 7)
			return false;
		if (points <= 2)
			return true;

		// Method to get rid of IDE warning
		// https://stackoverflow.com/questions/20358883/how-to-quickly-and-conveniently-create-a-one-element-arraylist
		int playerCardWorth;

		if (playerCard == null)
			playerCardWorth = -1;
		else
			playerCardWorth = handTotal(new ArrayList<>(Collections.singletonList(playerCard)));

		if (points == 3)
		{
			return playerCardWorth != 8;
		}
		else if (points == 4)
		{
			return playerCardWorth == -1 || (playerCardWorth >= 2 && playerCardWorth <= 7);
		}
		else if (points == 5)
		{
			return playerCardWorth == -1 || (playerCardWorth >= 4 && playerCardWorth <= 7);
		}

		// if reached this point then points must == 6
		return playerCardWorth == 6 || playerCardWorth == 7;
	}

	/*
	Returns true if player should draw another card.
	Player should draw if:
		- not natural, so sum != 8 || sum != 9
		- if two cards and their sum < 6
	 */
	public static boolean evaluatePlayerDraw (ArrayList<Card> hand)
	{
		int points = handTotal(hand);

		if (points >= 8)
			return false;
		else
			return hand.size() == 2;
	}
}
