import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.binding.IntegerBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;


/*

	Call this class start(Stage) function to immediately start the game.
	This function should be called from the main menu when user presses "Play" button.

 */

public class BaccaratGame extends Application
{
	// card positions in the window
	static final double bankerStartX = 350, bankerStartY = 50;
	static final double playerStartX = bankerStartX, playerStartY = 450;
	static final double cardXIncrement = 100, labelPointsXOffset = 0, labelPointsYOffset = 120;
	static final double logWindowWidth = 300, logWindowHeight = 600, logWindowX = MainMenu.windowSizeX - logWindowWidth - 10, logWindowY = 10;

	Scene gameScene;
	Pane gamePane;
	Stage primaryStage;
	BaccaratDealer theDealer; // contains cards that are not played yet, card deck
	VBoxButtonContainer buttonContainer; // contains buttons to play round or bet
	VBoxButtonContainer buttonContainerRight;
	LogWindow log;  // game progress log
	ButtonImage tutorialScreen;
	MenuBar menuBar;

	// those are mostly for the test cases as project requirements
	double betOnBanker = 0, betOnPlayer = 0, betOnDraw = 0;
	double currentBet;
	double totalWinnings;
	double startMoney;
	double maxMoney;
	int lastPlayerBet, lastDrawBet, lastBankerBet; // how much was bet on each of the fields
	int keepPlaying = 0;
	int gameInProgress = 0; // global flag, if 1 don't allow to start new round, set to 0 at the end of the round
	static int firstStart = 1;

	// variables used to count and display some messages - not crucial for game
	int roundCounter = 0;
	int notValidBetCounter = 0;
	int youCheater = 0;
	int betEverythingOnDraw = 0;
	int naturalWin = 0;


	// betting fields
	BaccaratBetting bettingUI;

	CardButtonAnimate game;
	Label lblPlayerPoints, lblBankerPoints; // labels that display how many points each player has
	Label lblWinner;

	// animations
	PauseTransition pauseBeforeNextAction = new PauseTransition();


	ArrayList<Card> playerHand;
	ArrayList<Card> bankerHand;

	// this is here so the test works
	BaccaratGame ()
	{
		playerHand = new ArrayList<>();
		bankerHand = new ArrayList<>();
	}

	// How is that even supposed to work? I don't need it.
	public double evaluateWinnings ()
	{
		int bankerPoints = BaccaratGameLogic.handTotal(bankerHand);
		int playerPoints = BaccaratGameLogic.handTotal(playerHand);

		if (bankerPoints == playerPoints)
		{
			return betOnDraw * 8;
		}
		else if (bankerPoints > playerPoints)
		{
			return betOnBanker * 1.95;
		}
		else
		{
			return betOnPlayer * 1.95;
		}
	}

	/*
		Helper function to set all labels to the same style
	 */
	private void setLabelStyle (Label... labels)
	{
		for (Label label : labels)
		{
			label.getStyleClass().add("outline");
		}
	}

	public void start (Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		// load the background
		BackgroundSize bgSize = new BackgroundSize(1024, 768, false, false, true, false);
		BackgroundImage bgImage = new BackgroundImage(new Image("background.jpg"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
		Background bg = new Background(bgImage);

		// Pane
		gamePane = new Pane();
		gamePane.setBackground(bg);
		gameScene = new Scene(gamePane, 1024, 768);

		gameScene.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<Event>) event -> System.out.println("Clicked on scene X: " + ((MouseEvent) event).getX() + " Y: " + ((MouseEvent) event).getY()));

		currentBet = 0;
		totalWinnings = 250; // how much money to start with?
		maxMoney = totalWinnings;
		startMoney = totalWinnings;

		lblPlayerPoints = new Label();
		lblBankerPoints = new Label();

		lblWinner = new Label("");
		lblWinner.setVisible(false);
		lblWinner.relocate(0, MainMenu.windowSizeY / 2 - 90);
		lblWinner.setPrefWidth(MainMenu.windowSizeX);
		lblWinner.setStyle("-fx-font-size: 50");
		lblWinner.setAlignment(Pos.CENTER);
		setLabelStyle(lblPlayerPoints, lblBankerPoints, lblWinner);

		log = new LogWindow();
		log.relocate(logWindowX, logWindowY);
		log.setSize(logWindowWidth, logWindowHeight);
		gamePane.getChildren().add(log.getScrollPane());

		if (firstStart == 1)
		{
			tutorialScreen = new ButtonImage("tutorial.png");
			tutorialScreen.setEventHandler(e ->
			{
				gamePane.getChildren().remove(tutorialScreen.getButton());
			});
			// this button is added later so it's drawn on top of everything else
			firstStart = 0;
		}


		// project requirements:
		Menu m = new Menu("Options");

		MenuItem m1 = new MenuItem("Fresh start");
		m1.setOnAction(e -> MainMenu.run());

		MenuItem m2 = new MenuItem("Exit");
		m2.setOnAction(e -> primaryStage.close());

		m.getItems().addAll(m1, m2);

		menuBar = new MenuBar();
		menuBar.getMenus().add(m);

		gamePane.getChildren().add(menuBar);

		play();
	}

	/*
		This function should be called once for the game.
		If you want to play again (for example when cards in deck run out),
		call this function again.
	 */
	private void play ()
	{
		// clear the hands
		playerHand = new ArrayList<>();
		bankerHand = new ArrayList<>();
		currentBet = 0;

		// generate brand new deck
		theDealer = new BaccaratDealer();
		theDealer.generateDeck();
		theDealer.shuffleDeck();

		bettingUI = new BaccaratBetting(gamePane);
		bettingUI.fillPlayerWallet(this.totalWinnings);

		// create new "table"
		try
		{
			game = new CardButtonAnimate(gamePane);
		}
		catch (Exception ignored)
		{

		}

		gameScene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
		primaryStage.setScene(gameScene);

		// add labels
		game.getPane().getChildren().addAll(lblBankerPoints, lblPlayerPoints, lblWinner);

		// add buttons
		buttonContainer = new VBoxButtonContainer(gamePane);

		// set position to lower-left corner
		buttonContainer.buttonContainer.setLayoutX(10);
		buttonContainer.buttonContainer.layoutYProperty().bind((new IntegerBinding()
		{
			@Override
			protected int computeValue ()
			{
				return (int) MainMenu.windowSizeY;
			}
		}).subtract(buttonContainer.buttonContainer.heightProperty().add(10)));


		buttonContainer.addButton("play_round", "Play round", e ->
		{
			playRoundEvent();
		}, true);

		buttonContainer.addButton("new_bet", "New bet", e ->
		{
			if (gameInProgress > 1)
				return;
			clearCards();
			buttonContainer.show("play_round");
			bettingUI.displayBettingUI();
			buttonContainer.setVisible("same_bet", false);
			buttonContainer.setVisible("new_bet", false);
			buttonContainer.setVisible("same_bet_play", false);
		}, false);

		buttonContainer.addButton("same_bet", "Same bet", e ->
		{
			if (gameInProgress > 1)
				return;
			clearCards();
			bettingUI.displayBettingUI();
			bettingUI.setBetOnBanker(lastBankerBet);
			bettingUI.setBetOnDraw(lastDrawBet);
			bettingUI.setBetOnPlayer(lastPlayerBet);

			buttonContainer.show("play_round");
			buttonContainer.setVisible("same_bet", false);
			buttonContainer.setVisible("new_bet", false);
			buttonContainer.setVisible("same_bet_play", false);
		}, false);

		buttonContainer.addButton("same_bet_play", "Same bet and play", e ->
		{
			if (gameInProgress > 0)
				return;
			sameBetAndPlay();
			buttonContainerRight.setEnabled("combine_wallet", false);
		}, false);
		buttonContainer.setVisible("same_bet_play", false);


		buttonContainerRight = getvBoxButtonContainer();
		// set position to lower-right corner
		buttonContainerRight.buttonContainer.layoutXProperty().bind((new IntegerBinding()
		{
			@Override
			protected int computeValue ()
			{
				return (int) MainMenu.windowSizeX;
			}
		}).subtract(buttonContainerRight.buttonContainer.widthProperty().add(10)));
		buttonContainerRight.buttonContainer.layoutYProperty().bind((new IntegerBinding()
		{
			@Override
			protected int computeValue ()
			{
				return (int) MainMenu.windowSizeY;
			}
		}).subtract(buttonContainerRight.buttonContainer.heightProperty().add(10)));


		buttonContainer.setVisible("same_bet", false);
		buttonContainer.setVisible("new_bet", false);
		buttonContainer.setTooltip("play_round", "Place some bet before playing");

		bettingUI.displayBettingUI();

		if (tutorialScreen != null)
			gamePane.getChildren().add(tutorialScreen.getButton());
	}

	private void sameBetAndPlay ()
	{
		clearCards();
		// bettingUI.displayBettingUI();
		gameInProgress = 2;


		bettingUI.setBetOnBanker(lastBankerBet);
		bettingUI.setBetOnDraw(lastDrawBet);
		bettingUI.setBetOnPlayer(lastPlayerBet);

		buttonContainer.setVisible("same_bet", false);
		buttonContainer.setVisible("new_bet", false);
		buttonContainer.setVisible("same_bet_play", false);

		PauseTransition pause = new PauseTransition();
		pause.setDelay(Duration.millis(100));
		pause.setOnFinished(e -> playRoundEvent());
		pause.play();
	}

	private VBoxButtonContainer getvBoxButtonContainer ()
	{
		VBoxButtonContainer buttonContainerRight = new VBoxButtonContainer(gamePane);
		buttonContainerRight.addButton("combine_wallet", "Combine chips", e ->
		{
			final int moneyStart = bettingUI.walletBalance();

			if (youCheater < 15)
				bettingUI.combineWallet();

			// error checking - multiplies chips when spamming the button
			// so allow that, but only a little :)
			PauseTransition pppp = new PauseTransition();
			pppp.setDelay(Duration.millis(400));
			pppp.setOnFinished(eeeeee ->
			{
				if (moneyStart < bettingUI.walletBalance())
				{
					youCheater++;
				}

				if (youCheater == 15)
				{
					log.add("");
					log.add("");
					log.add("You dirty cheater.");

					youCheater++;
				}

				if (youCheater >= 15)
				{
					bettingUI.transferWinnings("cheater");
					buttonContainerRight.setEnabled("combine_wallet", false);
					buttonContainer.setEnabled("same_bet", false);
					buttonContainer.setEnabled("new_bet", false);
					buttonContainer.setEnabled("same_bet_play", false);
				}
			});
			pppp.play();

		});
		buttonContainerRight.setTooltip("combine_wallet", "Press if you have too many chips in the wallet");

		buttonContainerRight.addButton("keep_playing", "Enable autoplay", e ->
		{
			if (keepPlaying == 0)
			{
				keepPlaying = 1;
				buttonContainerRight.setLabel("keep_playing", "Pause autoplay");
			}
			else
			{
				keepPlaying = 0;
				buttonContainerRight.setLabel("keep_playing", "Enable autoplay");
			}
		});
		buttonContainerRight.setTooltip("keep_playing", "Repeats the last bet until you run out of money.");

		return buttonContainerRight;
	}

	private void playRoundEvent ()
	{
		// check if any bet was placed, if not temporarily disable the button
		if (bettingUI.betAmountOnBanker() == 0 && bettingUI.betAmountOnDraw() == 0 && bettingUI.betAmountOnPlayer() == 0)
		{
			// if autoplay is enabled, then that must mean that previous animations took too long
			// so schedule another function call
			if (keepPlaying == 1 || gameInProgress == 2)
			{
				PauseTransition tryAgain = new PauseTransition();
				tryAgain.setDelay(Duration.millis(300));
				tryAgain.setOnFinished(e -> playRoundEvent());
				tryAgain.play();
				return;
			}


			buttonContainer.setLabel("play_round", "Place a bet first");
			buttonContainer.setEnabled("play_round", false);

			PauseTransition waitBeforeEnabling = new PauseTransition();
			waitBeforeEnabling.setDelay(Duration.millis(1000));
			waitBeforeEnabling.setOnFinished(event ->
			{
				buttonContainer.setLabel("play_round", "Play round");
				buttonContainer.setEnabled("play_round", true);
			});
			waitBeforeEnabling.play();

			notValidBetCounter++;

			if (notValidBetCounter > 30)
			{
				log.add("I need money too");
			}
			else if (notValidBetCounter > 20)
			{
				log.add("This must be fun for you, ya?");
			}
			else if (notValidBetCounter % 10 == 0)
			{
				log.add("Stop trying to play for free");
			}
			else
			{
				log.add("- Place some bet before playing -");
			}

			return;
		}

		// clear the betting ui and save values for the button "Place same bet"
		bettingUI.clearBettingUI();
		buttonContainer.setVisible("play_round", false);
		buttonContainer.setVisible("same_bet_play", false);
		lastPlayerBet = bettingUI.betAmountOnPlayer();
		lastDrawBet = bettingUI.betAmountOnDraw();
		lastBankerBet = bettingUI.betAmountOnBanker();

		playRound();
	}

	// pointsDisplay() helper, see pointsDisplay() description
	private void _pointsDisplay (Label label, ArrayList<Card> hand)
	{
		if (hand == null || hand.isEmpty())
		{
			label.setVisible(false);
			return;
		}

		// generate label tooltip - how score is calculated
		StringBuilder tooltip = new StringBuilder("Points are calculated as:\n");
		StringBuilder tooltip2 = new StringBuilder("Which gives ");

		int points = 0;
		for (int i = 0; i < hand.size(); i++)
		{
			Card card = hand.get(i);
			tooltip.append(" - ").append(card.suite).append(" is worth ").append(card.value).append(" points\n");
			tooltip2.append(card.value);
			if (i < hand.size() - 1)
				tooltip2.append(" + ");

			points += card.value;
		}

		tooltip2.append(" = ").append(points).append("\n\nTherefore the final score is ").append(BaccaratGameLogic.handTotal(hand));
		Tooltip tp = new Tooltip();
		tp.setText(tooltip + tooltip2.toString());
		tp.setShowDelay(Duration.millis(100));
		label.setTooltip(tp);

		label.setText("Points: " + BaccaratGameLogic.handTotal(hand));
		label.relocate(hand.get(0).posX + labelPointsXOffset, hand.get(0).posY + labelPointsYOffset);
		label.setVisible(true);
	}

	/*
		Relocates and updates labels that display current hand's points
	 */
	private void pointsDisplay ()
	{
		_pointsDisplay(lblBankerPoints, bankerHand);
		_pointsDisplay(lblPlayerPoints, playerHand);
	}

	/*
		Hide points - used when starting another round.
	 */
	private void pointsHide ()
	{
		lblPlayerPoints.setVisible(false);
		lblBankerPoints.setVisible(false);
	}

	/*
		Use to fade-in animate some label
	 */
	private void labelFadeIn (Label label, double delay)
	{
		label.setOpacity(0);
		FadeTransition ft = new FadeTransition(Duration.millis(1000), label);
		ft.setDelay(Duration.millis(delay));
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.play();
	}


	// Used when adding another card, when 2 are already displayed.
	// It sets the newCard position to be next to lastCard
	private void updateCardPos (Card lastCard, Card newCard)
	{
		newCard.posX = lastCard.posX + cardXIncrement;
		newCard.posY = lastCard.posY;
	}


	/*
	After round is completed and some button is pressed, call this to clear the cards and labels
	before displaying something else.
	 */
	void clearCards ()
	{
		lblWinner.setVisible(false);
		lblPlayerPoints.setVisible(false);
		lblBankerPoints.setVisible(false);
		game.removeAllButtons();
	}


	/*
		After bets are set, this function will deal some cards
		Call it again to play another round without
		generating new deck.
	 */
	private void playRound ()
	{
		gameInProgress = 1;
		buttonContainerRight.setEnabled("combine_wallet", false);
		bettingUI.clearTrash();


		// reset the hands
		playerHand.clear();
		bankerHand.clear();
		currentBet = 0;
		game.removeAllButtons();

		// hide elements that are not needed
		lblWinner.setVisible(false);

		// get cards to the player and banker
		playerHand.addAll(theDealer.dealHand());
		bankerHand.addAll(theDealer.dealHand());

		// set card positions, first draw player cards
		double delay = 0;
		double position = playerStartX;
		for (Card card : playerHand)
		{
			card.posY = playerStartY;
			card.posX = position;
			position += cardXIncrement;
			game.throwNewCard(card, delay);
			delay += 300;
		}

		// now draw banker cards
		position = bankerStartX;
		for (Card card : bankerHand)
		{
			card.posY = bankerStartY;
			card.posX = position;
			position += cardXIncrement;
			game.throwNewCard(card, delay);
			delay += 300;
		}

		pointsDisplay();
		labelFadeIn(lblPlayerPoints, delay);
		labelFadeIn(lblBankerPoints, delay);

		// check for natural wins
		if (BaccaratGameLogic.handTotal(playerHand) >= 8 || BaccaratGameLogic.handTotal(bankerHand) >= 8)
		{
			pauseBeforeNextAction.setOnFinished(e -> evaluateWinner());
			naturalWin = 1;
		}
		else
		{
			pauseBeforeNextAction.setOnFinished(e -> playPlayerMove());
			naturalWin = 0;
		}

		pauseBeforeNextAction.setDelay(Duration.millis(delay + 1200));
		pauseBeforeNextAction.play();

		log.add("");
		roundCounter++;
		if (roundCounter == 30)
		{
			log.add("You still didn't get enough?");
		}
		else if (roundCounter == 50)
		{
			log.add("I'm surprised you lasted that long");
		}
		else if (roundCounter >= 100)
		{
			log.add("Please just stop. Round " + roundCounter);
		}
		else
		{
			log.add("Round " + roundCounter + " started.");
		}

		if (bettingUI.walletBalance() == 0 && bettingUI.betAmountOnDraw() != 0 && bettingUI.betAmountOnBanker() == 0 && bettingUI.betAmountOnPlayer() == 0)
		{
			log.add("Bet everything on draw?");
			if (bettingUI.betAmountOnDraw() < 100)
			{
				log.add("Well it's not like you had much");
				log.add("to begin with.");
			}

			betEverythingOnDraw = 1;
		}

	}

	/*
		Helper function for playPlayerMove() and playBankerMove()
	 */
	private void _drawAnotherCard (ArrayList<Card> hand, Label label)
	{
		Card newCard = theDealer.drawOne();
		hand.add(newCard);
		updateCardPos(hand.get(hand.size() - 2), hand.get(hand.size() - 1));
		game.throwNewCard(newCard, 0);
		labelFadeIn(label, 500);
		pointsDisplay();
	}

	/*
		Separate function for clarity and to set the animation.
		Draw another card for player if needed and go to banker move.
	 */
	private void playPlayerMove ()
	{
		if (BaccaratGameLogic.evaluatePlayerDraw(playerHand))
		{
			lblPlayerPoints.setVisible(false);
			_drawAnotherCard(playerHand, lblPlayerPoints);

			pauseBeforeNextAction.setOnFinished(e -> playBankerMove());
			pauseBeforeNextAction.setDelay(Duration.millis(1000));
			pauseBeforeNextAction.play();
		}
		else
		{
			playBankerMove();
		}
	}

	/*
		Draw another card for banker if needed.
	 */
	private void playBankerMove ()
	{
		if (BaccaratGameLogic.evaluateBankerDraw(bankerHand, playerHand.get(playerHand.size() - 1)))
		{
			lblBankerPoints.setVisible(false);
			_drawAnotherCard(bankerHand, lblBankerPoints);
			pauseBeforeNextAction.setDelay(Duration.millis(1000));
		}
		else
		{
			pauseBeforeNextAction.setDelay(Duration.millis(0));
		}

		pauseBeforeNextAction.setOnFinished(e -> evaluateWinner());
		pauseBeforeNextAction.play();
	}


	/*
		Used to evaluate who won and progress the game.
	 */
	private void evaluateWinner ()
	{
		int playerPoints = BaccaratGameLogic.handTotal(playerHand);
		int bankerPoints = BaccaratGameLogic.handTotal(bankerHand);
		int valueOfWinningField = 0;
		int totalBetAmount = bettingUI.betAmountOnDraw() + bettingUI.betAmountOnBanker() + bettingUI.betAmountOnPlayer();
		int multiplier = 2;

		if (naturalWin == 1)
		{
			log.add("Natural Win!");
		}

		if (playerPoints == bankerPoints)
		{
			if (betEverythingOnDraw != 1)
				log.add("Draw");
			valueOfWinningField = bettingUI.betAmountOnDraw();
			multiplier = 8;

			lblWinner.setText("It's a draw");
			bettingUI.transferWinnings("draw");
		}
		else if (playerPoints > bankerPoints)
		{
			if (betEverythingOnDraw != 1)
				log.add("Player wins");
			valueOfWinningField = bettingUI.betAmountOnPlayer();

			lblWinner.setText("Player wins !");
			bettingUI.transferWinnings("player");
		}
		else
		{
			if (betEverythingOnDraw != 1)
				log.add("Banker wins");
			valueOfWinningField = bettingUI.betAmountOnBanker();

			lblWinner.setText("Banker wins !");
			bettingUI.transferWinnings("banker");
		}

		lblWinner.setOpacity(0);
		lblWinner.setVisible(true);
		labelFadeIn(lblWinner, 0);

		// enable buttons
		buttonContainer.show("new_bet");
		if (bettingUI.walletBalance() >= totalBetAmount)
		{
			buttonContainer.show("same_bet");
			buttonContainer.show("same_bet_play");
		}


		int winnings = valueOfWinningField * multiplier;
		if (winnings == 0)
		{
			if (betEverythingOnDraw != 1)
				log.add("You just lost $" + totalBetAmount);
		}
		else
		{
			if (totalBetAmount >= winnings)
			{
				log.add("You just spent $" + totalBetAmount + " to win $" + winnings);
				log.add("Nice.");
			}
			else
			{
				if (betEverythingOnDraw == 1)
				{
					log.add("You went all-in on draw");
					log.add("and somehow won.");
					log.add("");
				}
				else
				{
					if (playerPoints == 1)
					{
						log.add("Did you just won with 1 point?");
					}
					else
					{
						log.add("You won $" + winnings);
					}

					if (totalBetAmount != valueOfWinningField)
					{
						log.add("Net winnings: " + (winnings - totalBetAmount));
					}
				}

			}
		}

		if (theDealer.deckSize() < 6)
		{
			log.add("Run out of cards.");
			log.add("Generating new deck.");

			theDealer = new BaccaratDealer();
			theDealer.generateDeck();
			theDealer.shuffleDeck();
		}

		// pause before enabling scrolling of the log
		PauseTransition pauseBES = new PauseTransition();
		pauseBES.setDelay(Duration.millis(500));
		pauseBES.setOnFinished(e ->
		{
			log.autoScroll(false);

			if (bettingUI.walletBalance() == 0)
			{
				if (betEverythingOnDraw == 1)
				{
					log.add("");
					log.add("What did I say?");
				}
				else if (roundCounter == 1)
				{
					log.add("");
					log.add("Really? You bet everything on round 1?");
					log.add("Well you lost it.");
				}
				else
				{
					log.add("************************");
					log.add("You lost everything you had :)");
					log.add("");
					log.add("If you still didn't grasp the situation,");
					log.add("you lost $" + (int) startMoney + " that you initially had.");

					if (maxMoney != startMoney)
					{
						log.add("");
						log.add("At some point you had $" + maxMoney);
						log.add("Should've walked away then");
					}
					else
					{
						log.add("And at no point you had more");
						log.add("money than you started with.");
						log.add("There must be a valuable lesson");
						log.add("about gambling here.");
						log.add("but what do I know?");
					}
				}

				log.add("");

				if (betEverythingOnDraw != 1)
					log.add("* you can close the game now *");
				else
					log.add("You can go home now");

				buttonContainer.setVisible("same_bet", false);
				buttonContainer.setVisible("new_bet", false);
				buttonContainer.setVisible("same_bet_play", false);
				buttonContainerRight.setVisible("combine_wallet", false);
			}
			betEverythingOnDraw = 0;
		});
		pauseBES.play();

		if (maxMoney < bettingUI.walletBalance())
			maxMoney = bettingUI.walletBalance();

		if (keepPlaying == 1)
		{
			// wait for other things to finish
			PauseTransition pause = new PauseTransition();
			pause.setDelay(Duration.millis(500));
			pause.setOnFinished(e ->
			{
				if (gameInProgress > 0)
				{
					pause.play();
				}
				else
				{
					// in case user disabled autoplay during the wait period
					if (keepPlaying == 1)
					{
						// make sure we have enough money to auto-play
						int moneyRequiredToBet = lastBankerBet + lastDrawBet + lastPlayerBet;
						if (moneyRequiredToBet > bettingUI.walletBalance())
						{
							// exit the keepPlaying mode
							keepPlaying = 0;
							buttonContainerRight.setLabel("keep_playing", "Enable autoplay");
							buttonContainerRight.show("combine_wallet");
							buttonContainer.show("new_bet");
							log.add("Not enough money to autoplay");

							return;
						}

						sameBetAndPlay();
					}
					else
						buttonContainerRight.show("combine_wallet");
				}
			});

			pause.play();
		}

		if (keepPlaying == 0)
			buttonContainerRight.show("combine_wallet");
		gameInProgress = 0;
	}
}
