/*

	This class represents a single card in the game.
	It contains all info required to calculate the score
	display the card images, or where those images should appear.

 */

public class Card
{
	String suite; // contains full card name, like "2 of Diamonds"
	int value;    // how many points is the card worth in the game
	int id;       // id of the card, from 0 to 51, corresponds to the card images
	double posX, posY; // position of the image representing this class in the window

	Card ()
	{
		this("", 0, -1);
	}

	Card (String suite, int value)
	{
		this(suite, value, -1, 0, 0);
	}

	Card (String suite, int value, int id)
	{
		this(suite, value, id, 0, 0);
	}

	Card (String suite, int value, int id, double posX, double posY)
	{
		this.suite = suite;
		this.value = value;
		this.id = id;
		this.posX = posX;
		this.posY = posY;
	}
}
