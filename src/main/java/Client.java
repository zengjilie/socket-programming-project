import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Client extends Application {

	// client information
	private String userName;
	private String userPassword;

	// when the server update, all clients will get the updates
	// track the latest commodities state
	private ArrayList<Item> unsold;

	// history
	private ArrayList<Item> sold;

	// client --> server
	// bids info = productId, clientInfo
	// sever --> client
	// success --> new status
	// fail --> reasons

	// I/O streams
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;

//	@Override
//	public void update(Observable o, Object arg) {
//		// TODO Auto-generated method stub
//		System.out.println("test");
//	}
//
	Socket s;

	// Scene
	Scene scene1;
	Scene scene2;

	@Override
	public void start(Stage primaryStage) throws IOException {

		// 1. Welcome scene --> Greetings, get users name and password(no
		// authentication)
		VBox layout1 = new VBox(20);

		// logo
		Image Image = new Image("auctionlogo.png");
		ImageView logoImg = new ImageView(Image);
		logoImg.setFitHeight(100);
		logoImg.setFitWidth(100);
		logoImg.setPreserveRatio(true);

		Label logoTxt = new Label("Virtual Auciton".toUpperCase());
		logoTxt.setFont(new Font("Times New Roman", 15));

		VBox logo = new VBox();
		logo.setAlignment(Pos.TOP_CENTER);
		logo.getChildren().addAll(logoImg, logoTxt);

		// name input
		TextField nameInput = new TextField();
		nameInput.setMaxWidth(200);
		nameInput.setPromptText("Enter username");
		nameInput.setFocusTraversable(false);

		// password input
		PasswordField pwInput = new PasswordField();
		pwInput.setMaxWidth(200);
		pwInput.setPromptText("Enter password");
		pwInput.setFocusTraversable(false);

		// button
		Button btn1 = new Button("Submit");
		btn1.setMaxWidth(200);
		btn1.setOnAction(e -> {
			userName = nameInput.getText();
			userPassword = pwInput.getText();
			System.out.println(userName);
			System.out.println(userPassword);
			primaryStage.setScene(scene2);
		});

		layout1.setPadding(new Insets(80));
		layout1.setAlignment(Pos.TOP_CENTER);
		layout1.getChildren().addAll(logo, nameInput, pwInput, btn1);

		scene1 = new Scene(layout1, 400, 500, Color.WHITE);

		// 2. main scene, controller
		BorderPane layout2 = new BorderPane();
		layout2.setPadding(new Insets(20));

		// left -->
		VBox leftLayout = new VBox(40);
		// logo
		Image Image2 = new Image("auctionlogo.png");
		ImageView logoImg2 = new ImageView(Image);
		logoImg2.setFitHeight(50);
		logoImg2.setFitWidth(50);
		logoImg2.setPreserveRatio(true);

		// bottons
		VBox btnBox = new VBox(10);
		Button onGoBtn = new Button("On Going");

		Button compBtn = new Button("Completed");

		btnBox.getChildren().addAll(onGoBtn, compBtn);

		Button exitBtn = new Button("Exit");
		exitBtn.setPrefWidth(100);

		leftLayout.getChildren().addAll(logoImg2, btnBox, exitBtn);

		// middle --> displaying items
		VBox midLayout = new VBox();

		// right --> user info
		VBox rightLayout = new VBox();

		layout2.setLeft(leftLayout);
		layout2.setCenter(midLayout);
		layout2.setRight(rightLayout);
		scene2 = new Scene(layout2, 600, 500);

		primaryStage.setTitle("Virtual Auction"); // Set the stage title
		primaryStage.setScene(scene2); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		// Create a socket to connect to the server
//		String serverIP = "localhost"; // change [localhost] to actual IP address
//		s = new Socket(serverIP, 5000);
//		System.out.println("New client created!");
//
//		// client <--> server data exchange
//		fromServer = new ObjectInputStream(s.getInputStream());
//		toServer = new ObjectOutputStream(s.getOutputStream());
//
//		try {
//			unsold = (ArrayList<Item>) fromServer.readObject();
//
//			System.out.println(unsold.toString());
//
//			// UI
//			// Display section
//
//			//
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		// decode data from server
//		System.out.println("Closing sockets!");
//		s.close();

	}

	public static void main(String[] args) {
		launch(args);
	}
}