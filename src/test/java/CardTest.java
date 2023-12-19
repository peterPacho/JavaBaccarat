import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest
{
	Card card;

	@BeforeEach
	void setup ()
	{
		card = new Card("dd", 13);
	}

	@Test
	void test1 ()
	{
		assertEquals(13, card.value);
	}

	@Test
	void test2 ()
	{
		assertEquals("dd", card.suite);
	}
}
