import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaccaratGameTest
{
	BaccaratGame game;

	@BeforeEach
	void setup ()
	{
		game = new BaccaratGame();
	}

	@Test
	void testEvalWinnings ()
	{
		game.betOnPlayer = 10;
		game.playerHand.add(new Card("", 9));
		assertEquals(19.5, game.evaluateWinnings());
	}

	@Test
	void testEvalWinnings2 ()
	{
		game.betOnDraw = 10;
		assertEquals(80, game.evaluateWinnings());
	}
}
