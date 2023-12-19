import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

/*

	This is the starting state of the application. It displays the main menu.

 */


public class MainMenu extends Application
{
	public static final double windowSizeX = 1024;
	public static final double windowSizeY = 768;
	private static BaccaratGame game;
	private static Stage stage;

	public static void main (String[] args)
	{
		launch(args);
	}


	@Override
	public void start (Stage primaryStage)
	{
		stage = primaryStage;
		primaryStage.setTitle("Baccarat 9001");
		primaryStage.setResizable(false);

		Pane gameMenuPane = new Pane();
		gameMenuPane.setStyle("-fx-background-color: black");


		Scene mainMenuScene = new Scene(gameMenuPane, windowSizeX, windowSizeY);
		mainMenuScene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
		primaryStage.setScene(mainMenuScene);
		primaryStage.show();

		// there's no menu, just start the game
		run();
	}

	public static void run ()
	{
		stage.setScene(null);
		game = new BaccaratGame();
		game.start(stage);
	}
}
