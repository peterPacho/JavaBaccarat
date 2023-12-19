/*
	This class is responsible for displaying the betting part of the game.
 */

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*

	Class that will handle all betting aspects of the game.
	Has inside classes responsible for displaying the wallet and UI used to pick the bet amount.

 */
public class BaccaratBetting
{
	// location of the elements on the screen
	static final double betFieldBankerX = 35, betFieldBankerY = 50;
	static final double betFieldDrawX = betFieldBankerX, betFieldDrawY = betFieldBankerY + 200;
	static final double betFieldPlayerX = betFieldBankerX, betFieldPlayerY = betFieldDrawY + 200;
	static final double betFieldSize = 140;
	static final double walletWidth = 400, walletHeight = 100;
	static final double bettingFieldWidth = 300;
	private final Pane pane;
	private final ChipImage chipImages;
	private final BettingField fieldPlayer, fieldDraw, fieldBanker, fieldWallet;
	private final ButtonAnimate fieldTrash;
	private final PauseTransition pauseBeforeNextAction = new PauseTransition();
	private final EventHandler<MouseEvent> bettingFieldClick;

	private BettingUI betBanker, betDraw, betPlayer;

	BaccaratBetting (Pane pane)
	{
		this.pane = pane;
		chipImages = new ChipImage();
		fieldPlayer = new BettingField("Player", betFieldPlayerX, betFieldPlayerY, betFieldSize, betFieldSize);
		fieldDraw = new BettingField("Draw", betFieldDrawX, betFieldDrawY, betFieldSize, betFieldSize);
		fieldBanker = new BettingField("Banker", betFieldBankerX, betFieldBankerY, betFieldSize, betFieldSize);
		fieldWallet = new BettingField("Your chips", MainMenu.windowSizeX / 2 - walletWidth / 2, MainMenu.windowSizeY - walletHeight - 20, walletWidth, walletHeight);
		fieldTrash = new ButtonAnimate(pane);

		// event handler to transfer chips from player wallet to some other pool
		bettingFieldClick = event ->
		{
			String buttonID = ((VBox) event.getSource()).getId();

			if (buttonID.charAt(0) == 'b')
			{
				transferChips(fieldWallet, fieldBanker, Integer.parseInt(buttonID.substring(2)));
			}
			else if (buttonID.charAt(0) == 'd')
			{
				transferChips(fieldWallet, fieldDraw, Integer.parseInt(buttonID.substring(2)));
			}
			else if (buttonID.charAt(0) == 'p')
			{
				transferChips(fieldWallet, fieldPlayer, Integer.parseInt(buttonID.substring(2)));
			}
			else
			{
				System.out.println("Pressed unknown button in bettingFieldClick handler!");
			}
		};
	}

	// this constructor used only for test purposes !
	BaccaratBetting ()
	{
		chipImages = new ChipImage();
		fieldPlayer = new BettingField("Player", betFieldPlayerX, betFieldPlayerY, betFieldSize, betFieldSize);
		fieldDraw = new BettingField("Draw", betFieldDrawX, betFieldDrawY, betFieldSize, betFieldSize);
		fieldBanker = new BettingField("Banker", betFieldBankerX, betFieldBankerY, betFieldSize, betFieldSize);
		fieldWallet = new BettingField("Your chips", MainMenu.windowSizeX / 2 - walletWidth / 2, MainMenu.windowSizeY - walletHeight - 20, walletWidth, walletHeight);
		fieldTrash = new ButtonAnimate();
		pane = null;
		bettingFieldClick = null;
	}

	/*
	Called when there's need to redraw the UI (for example because max bet amount if above what the player's wallet holds)
	 */
	public void clearBettingUI ()
	{
		if (betBanker != null)
			betBanker.clear();
		if (betDraw != null)
			betDraw.clear();
		if (betPlayer != null)
			betPlayer.clear();

		betBanker = null;
		betDraw = null;
		betPlayer = null;
	}

	/*
		Displays the UI to place bets
	*/
	public void displayBettingUI ()
	{
		clearBettingUI();
		betBanker = new BettingUI("b", 160 + betFieldBankerX, betFieldBankerY, bettingFieldWidth, betFieldSize, bettingFieldClick);
		betDraw = new BettingUI("d", 160 + betFieldBankerX, betFieldDrawY, bettingFieldWidth, betFieldSize, bettingFieldClick);
		betPlayer = new BettingUI("p", 160 + betFieldBankerX, betFieldPlayerY, bettingFieldWidth, betFieldSize, bettingFieldClick);
	}

	public int betAmountOnPlayer ()
	{
		return fieldPlayer.getTotal();
	}

	public int betAmountOnDraw ()
	{
		return fieldDraw.getTotal();
	}

	public int betAmountOnBanker ()
	{
		return fieldBanker.getTotal();
	}

	public int walletBalance ()
	{
		return fieldWallet.getTotal();
	}


	// those are used when button "Place same bet" is pressed
	// I'm lazy so the animation is broken
	private void _setBet (BettingField target, int amount)
	{
		if (amount == 0)
			return;

		// get all chips in wallet in the middle
		double x = fieldWallet.x + fieldWallet.sizeX / 2;
		double y = fieldWallet.y + fieldWallet.sizeY / 2;

		// if bet equal to some chip value, use that value
		int value = 0;
		for (int val : chipImages.chipVals)
		{
			if (amount == val)
			{
				value = val;
				break;
			}
		}

		// if found amount that can be placed with single chip
		if (value != 0)
		{
			transferChips(fieldWallet, target, value);
			return;
		}

		// otherwise transfer chips lazy way
		for (cButton chip : fieldWallet.chips.buttonsDisplayed)
		{
			ButtonAnimate.relocateButton(chip, chip.x, chip.y, x, y, 0);
		}

		// save sum and delete all chips
		int sum = fieldWallet.getTotal();
		fieldWallet.removeAllChips();

		// transfer to the target
		fillWallet(target, amount, x, y);

		// refill the wallet
		fillWallet(fieldWallet, sum - amount, x, y);
	}

	public void setBetOnPlayer (int amount)
	{
		_setBet(fieldPlayer, amount);
	}

	public void setBetOnBanker (int amount)
	{
		_setBet(fieldBanker, amount);
	}

	public void setBetOnDraw (int amount)
	{
		_setBet(fieldDraw, amount);
	}

	/*
	This transfers single chip of given value
	 */
	private void transferChips (BettingField from, BettingField to, int chipVal)
	{
		cButton chipToTransfer = from.removeChip(chipVal);

		// if no chip of that value available try to exchange some
		if (chipToTransfer == null)
		{
			ArrayList<cButton> chips = new ArrayList<>(from.chips.getAllButtons());
			Collections.sort(chips);

			// is there couple smaller value chips that can be easily combined?
			int sum = 0;
			for (cButton chip : chips)
			{
				sum += chip.value;

				if (sum >= chipVal)
				{
					break;
				}
			}

			// exchange bigger chip into smaller
			if (sum != chipVal)
			{
				for (cButton chip : chips)
				{
					if (chip.value > chipVal)
					{
						exchangeChip(from, chip.value);
						pauseBeforeNextAction.setOnFinished(event -> transferChips(from, to, chipVal));
						pauseBeforeNextAction.setDelay(Duration.millis(50));
						pauseBeforeNextAction.play();
						return;
					}
				}
			}

			// combineChips will take some time, so wait before continuing
			combineChips(from, chipVal, chips);

			pauseBeforeNextAction.setOnFinished(event -> transferChips(from, to, chipVal));
			pauseBeforeNextAction.setDelay(Duration.millis(500));
			pauseBeforeNextAction.play();

			return;
		}

		from.updateTotalLabel();
		to.throwChip(chipToTransfer);
		displayBettingUI();
	}

	// after round is played, call this method to transfer the winning chips somewhere
	public void transferWinnings (String winner)
	{
		int amountWon = 0;
		int amountMultplier = 2;

		BettingField fieldThatWon = null;

		if (winner.equals("banker"))
		{
			fieldThatWon = fieldBanker;
		}
		else if (winner.equals("draw"))
		{
			fieldThatWon = fieldDraw;
			amountMultplier = 8;
		}
		else if (winner.equals("cheater"))
		{
			fieldWallet.removeAllChips();
			fieldWallet.updateTotalLabel();
			return;
		}
		else // player
		{
			fieldThatWon = fieldPlayer;
		}


		amountWon = fieldThatWon.getTotal();
		amountWon = amountWon * amountMultplier - amountWon; // this contains only how many extra $$ should be added to the wallet

		// transfer the chips from winning field to the wallet
		ArrayList<cButton> chipsToTransfer = fieldThatWon.removeAllChips();
		chipsToTransfer.forEach(fieldWallet::throwChip);

		// clear other fields
		ArrayList<cButton> removedChips = new ArrayList<>();
		removedChips.addAll(fieldBanker.removeAllChips());
		removedChips.addAll(fieldDraw.removeAllChips());
		removedChips.addAll(fieldPlayer.removeAllChips());

		// add all buttons to "trash" field and relocate them outside the window
		for (cButton bt : removedChips)
		{
			fieldTrash.throwNewButton(bt, bt.x, bt.y, MainMenu.windowSizeX / 2, -50, 0);
		}

		final int amountToAdd = amountWon;
		fillPlayerWallet(amountToAdd);

		// update labels
		fieldBanker.updateTotalLabel();
		fieldDraw.updateTotalLabel();
		fieldPlayer.updateTotalLabel();
		fieldWallet.updateTotalLabel();
	}

	/*
		This fills the wallet with new money
	 */
	private void fillWallet (BettingField wallet, double money, double startX, double startY)
	{
		for (int value : chipImages.chipVals)
		{
			while ((int) money >= value)
			{
				money -= value;
				cButton newChip = chipImages.getChipCopy(value);
				newChip.x = startX;
				newChip.y = startY;
				wallet.throwChip(newChip);
			}
		}

		wallet.updateTotalLabel();
	}

	void fillPlayerWallet (double money)
	{
		fillWallet(fieldWallet, money, MainMenu.windowSizeX / 2, -100);
	}

	// nice test case for "combine" function:
	private void giveMeThousandDollars ()
	{
		for (int i = 0; i < 1000; i++)
			fieldWallet.addChip(chipImages.getChipCopy(1));
	}

	public void clearTrash ()
	{
		fieldTrash.removeAllButtons();
	}

	/*
		Combines all chips in the wallet to represent same value in the least number of chips
	 */
	public void combineWallet ()
	{
		combineChips(fieldWallet, fieldWallet.getTotal());
	}

	/*
		Combines the chips inside the wallet until the moneyTarget is reached.
	 */
	private void combineChips (BettingField wallet, int moneyTarget)
	{
		ArrayList<cButton> chips = new ArrayList<>(wallet.chips.getAllButtons());
		Collections.sort(chips);
		combineChips(wallet, moneyTarget, chips);
		wallet.updateTotalLabel(); // this shouldn't change
	}

	private void combineChips (BettingField wallet, int moneyTarget, ArrayList<cButton> chips)
	{
		// if reached this point, then there must be enough money, but it's represented in multiple chips
		// so need to combine some chips to place the bet
		int reachedSum = 0;

		if (chips.isEmpty())
			return;

		// save location of the first chip - all removed chips will float towards this one
		double x = chips.get(0).x, y = chips.get(0).y;

		// save buttons to be removed
		ArrayList<cButton> buttonsToRemove = new ArrayList<>();

		// loop over the chips until we get enough money
		int counter = 0;
		for (cButton chip : chips)
		{
			reachedSum += chip.value;
			buttonsToRemove.add(chip);

			// don't relocate first chip
			if (counter != 0)
				ButtonAnimate.relocateButton(chip, chip.x, chip.y, x, y, 0);

			if (reachedSum >= moneyTarget)
				break;

			counter++;
		}

		final int finalReachedSum = reachedSum;

		PauseTransition waitForRelocation = new PauseTransition();
		waitForRelocation.setDelay(Duration.millis(100));
		waitForRelocation.setOnFinished(event ->
		{
			buttonsToRemove.forEach(e -> wallet.removeChip(e.value));

			// all removed chips value is enough to cover the bet, so now add "new" chips equal to the combined value, and call this function again
			fillWallet(wallet, finalReachedSum, x, y);
		});

		waitForRelocation.play();
	}


	/*
		Slightly modified fillPlayerWallet - it adds chip with animation, allowing to
		set the start location.
	 */
	private void _exchangeChip (BettingField wallet, int money, cButton removedChip)
	{
		for (int value : chipImages.chipVals)
		{
			while (money >= value)
			{
				money -= value;
				cButton newChip = chipImages.getChipCopy(value);
				newChip.x = removedChip.x;
				newChip.y = removedChip.y;
				wallet.throwChip(newChip);
			}
		}
	}

	/*
		Given value of the chip to exchange, it removes it from the field and inserts smaller
		chips equal in value to the removed chip. Called when placing a valid bet but no chips of that amount are available.
	*/
	private void exchangeChip (BettingField wallet, int value)
	{
		// need to save the chip's location before we remove it
		cButton chipToRemove = wallet.getChipWithVal(value);
		if (chipToRemove == null)
			return;

		// remove it
		wallet.removeChip(value);

		// find next smaller chip value
		int smaller = 1;
		for (int val : chipImages.chipVals)
		{
			if (val >= chipToRemove.value)
				break;
			smaller = val;
		}

		// insert the chips of equal value, starting from the location of the old chip
		_exchangeChip(wallet, smaller, chipToRemove);
		_exchangeChip(wallet, value - smaller, chipToRemove);
	}


	/*
		This class represent a single field on the screen where chips are placed.
	 */
	private class BettingField
	{
		static final double chipMargin = 40;
		double x, y, sizeX, sizeY; // x,y coordinates and the size of the square that is the box where chips will land
		ButtonAnimate chips; // chips currently in this field
		Label labelTotal;

		BettingField ()
		{
			this("Empty", 0, 0, 100, 100);
		}

		BettingField (String fieldName, double x, double y, double sizeX, double sizeY)
		{
			this.x = x;
			this.y = y;
			this.sizeX = sizeX;
			this.sizeY = sizeY;

			Label fieldTitle = new Label(fieldName);
			fieldTitle.relocate(this.x, this.y - 20);
			fieldTitle.setStyle("-fx-font-size: 15px;");

			labelTotal = new Label("Total: $0");
			labelTotal.relocate(this.x, this.y + this.sizeY);
			labelTotal.setPrefWidth(this.sizeY);
			labelTotal.setStyle("-fx-font-size: 12px;");
			labelTotal.setTextAlignment(TextAlignment.RIGHT);

			Region field = new Region();
			field.getStyleClass().add("region");
			field.setPrefSize(sizeX, sizeY);
			field.relocate(this.x, this.y);

			pane.getChildren().addAll(field, fieldTitle, labelTotal);

			try
			{
				chips = new ButtonAnimate(pane);
			}
			catch (Exception ignored)
			{

			}
		}

		/*
		Returns how much money (in chips) is in this container.
		 */
		int getTotal ()
		{
			int sum = 0;

			for (cButton b : chips.getAllButtons())
			{
				sum += b.value;
			}

			return sum;
		}

		// call after any changed to the container
		public void updateTotalLabel ()
		{
			labelTotal.setText("Total: $" + (int) getTotal());
		}

		// returns a two-element list that has x and y coordinates for the new button
		ArrayList<Double> getNewChipCords (int rollsLeft)
		{
			// get random location for the chip
			double x = Math.random() * this.sizeX + this.x;
			double y = Math.random() * this.sizeY + this.y;
			if (x > this.sizeX + this.x - chipMargin)
				x = this.sizeX + this.x - chipMargin;
			if (y > this.sizeY + this.y - chipMargin)
				y = this.sizeY + this.y - chipMargin;

			// try to prevent overlapping
			if (rollsLeft > 0)
			{
				for (cButton b : chips.getAllButtons())
				{
					// if distance to any other button is smaller, try again
					if (Math.abs(x - b.x) + Math.abs(y - b.y) < chipMargin)
						return getNewChipCords(rollsLeft - 1); // try again
				}
			}
			else
			{
				// if it couldn't find a suitable location using the "random" method, just go line by line
				for (double xx = this.x; xx < this.x + this.sizeX - 30; xx += 5)
				{
					for (double yy = this.y; yy < this.y + this.sizeY - 30; yy += 5)
					{
						boolean marginOK = true;

						for (cButton b : chips.getAllButtons())
						{
							if (Math.abs(xx - b.x) + Math.abs(yy - b.y) < chipMargin)
							{
								marginOK = false;
								break;
							}
						}

						if (marginOK)
							return new ArrayList<>(Arrays.asList(xx, yy));
					}
				}
			}

			return new ArrayList<>(Arrays.asList(x, y));
		}


		/*
			This draw the chip on the screen without animating it.
		 */
		void addChip (cButton chip)
		{
			ArrayList<Double> coordinates = getNewChipCords(10);
			chips.placeNewButton(chip, coordinates.get(0), coordinates.get(1));
		}

		/*
		Like addChip but animates the chip
		 */
		void throwChip (cButton chip)
		{
			ArrayList<Double> coordinates = getNewChipCords(10);
			double x = chip.x, y = chip.y;

			// if default location, assume they come from outside the screen
			if (x == 0 && y == 0)
			{
				x = MainMenu.windowSizeX / 2;
				y = MainMenu.windowSizeY + 100;
			}
			chips.throwNewButton(chip, x, y, coordinates.get(0), coordinates.get(1), 0);
			updateTotalLabel();
		}

		/*
		Returns first chip with specified value
		 */
		cButton getChipWithVal (int value)
		{
			for (cButton bt : chips.getAllButtons())
			{
				if (bt.value == value)
				{
					return bt;
				}
			}

			return null;
		}

		/*
		Removes a single chip of specified value from the pool.
		 */
		cButton removeChip (int value)
		{
			cButton removed = getChipWithVal(value);
			chips.removeButton(removed);
			return removed;
		}

		/*
		Removes all buttons and returns them
		 */
		ArrayList<cButton> removeAllChips ()
		{
			ArrayList<cButton> chipsRemoved = new ArrayList<>(chips.getAllButtons());

			for (cButton button : chipsRemoved)
			{
				chips.removeButton(button);
			}

			return chipsRemoved;
		}
	}

	/*
	this class creates a simple interface to transfer chips to the fields
	displayed before cards are dealt
	 */
	private class BettingUI
	{
		HBox buttonContainer;
		private Pane field;

		BettingUI (String id, double x, double y, double sizeX, double sizeY, EventHandler<MouseEvent> mouseClick)
		{
			if (fieldWallet.getTotal() <= 0)
				return;

			field = new Pane();
			field.getStyleClass().add("betButtonRegion");
			field.setPrefHeight(sizeY);
			field.relocate(x, y);

			buttonContainer = new HBox();
			buttonContainer.setPrefHeight(100);
			buttonContainer.setAlignment(Pos.CENTER);

			// add all possible coins which values are below or equal to amount in the wallet
			ArrayList<Integer> possibleVals = new ArrayList<>(chipImages.chipVals);
			Collections.reverse(possibleVals); // so it's sorted in decrementing order
			double availableMoney = fieldWallet.getTotal();

			for (int value : possibleVals)
			{
				if (value <= availableMoney)
				{
					// each chip is placed in VBox together with a label, each VBox is clickable to transfer the chips
					VBox chipContainer = new VBox();
					chipContainer.setId(id + "_" + value);
					Label chipValue = new Label("$" + value);
					chipContainer.setStyle("");
					chipContainer.setAlignment(Pos.CENTER);
					chipContainer.getStyleClass().add("vbox_button");

					chipContainer.setOnMouseClicked(mouseClick);

					// create button
					cButton newButton = chipImages.getChipCopy(value);

					chipContainer.getChildren().addAll(newButton, chipValue);
					VBox.setMargin(newButton, new Insets(5));
					buttonContainer.getChildren().add(chipContainer);

					HBox.setMargin(chipContainer, new Insets((sizeX / 2) - buttonContainer.getPrefHeight() - 15, 3, 0, 3));
				}

			}

			field.getChildren().add(buttonContainer);
			pane.getChildren().add(field);
		}

		// call it before refreshing/moving on to different function
		void clear ()
		{
			pane.getChildren().remove(field);
		}
	}
}
