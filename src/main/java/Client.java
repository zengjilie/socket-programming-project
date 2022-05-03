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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application {

	// client information
	private String userName;
	private String userPassword;

	// when the server update, all clients will get the updates
	// track the latest commodities state
	private ArrayList<Item> open = new ArrayList<>();

	// history
	private ArrayList<Item> closed = new ArrayList<>();

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

		// testing data
		for (int i = 0; i < 10; i++) {
			Item it = new Item(i, "Papi", i + 10, "Bob",
					"https://im.indiatimes.in/photogallery/2021/Jul/1afp_60ed83c04c151.jpg?w=600&h=450&cc=1");
			open.add(it);
		}

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
		VBox leftLayout = new VBox(80);
		leftLayout.setPadding(new Insets(0, 10, 0, 0));
		leftLayout.setStyle("-fx-border-color:black;-fx-border-width:0 1 0 0");

		// logo
		Image Image2 = new Image("auctionlogo.png");
		ImageView logoImg2 = new ImageView(Image);

		logoImg2.setFitHeight(70);
		logoImg2.setFitWidth(70);
		logoImg2.setPreserveRatio(true);
		Label logoTxt2 = new Label("Virtual Auciton".toUpperCase());
		logoTxt2.setFont(new Font("Times New Roman", 12));

		VBox logo2 = new VBox();
		logo2.setAlignment(Pos.CENTER);
		logo2.getChildren().addAll(logoImg2, logoTxt2);

		// bottons --> switching scene
		VBox btnBox = new VBox(10);
		Button onGoBtn = new Button("On Going");
		onGoBtn.setPrefHeight(100);
		onGoBtn.setPrefWidth(100);

		Line line = new Line();
		line.setStartX(0);
		line.setStartY(0);
		line.setEndX(100);
		line.setEndY(0);

		Button compBtn = new Button("Completed");
		compBtn.setPrefHeight(100);
		compBtn.setPrefWidth(100);

		btnBox.getChildren().addAll(onGoBtn, line, compBtn);

		// button --> exit
		Button exitBtn = new Button("Exit");
		exitBtn.setPrefWidth(100);

		leftLayout.getChildren().addAll(logo2, btnBox, exitBtn);

		// middle --> displaying 2 sets sections --> ongoing/completed
		// middle is Scrollable
		ScrollPane midLayout = new ScrollPane();
		midLayout.setPrefHeight(600);

		VBox midLayout1 = new VBox(30); // ongoing
		midLayout1.setPadding(new Insets(20));

		VBox midLayout2 = new VBox(30); // completed

		midLayout.setContent(midLayout1);

		// Ongoing --> display opening items
		Label og = new Label("< Ongoing >".toUpperCase());
		midLayout1.getChildren().add(og);
		og.setStyle("-fx-font-family:Time New Roman;-fx-font-size:15");

		// Add item card to midLayout1
		for (Item item : open) {
			HBox itemCard1 = new HBox();
			// image + time + highest bid
			// image
			Image itemImg = new Image(item.getImage());
			ImageView itemImage = new ImageView(itemImg);
			itemImage.setFitHeight(100);
			itemImage.setFitWidth(100);
			itemImage.setPreserveRatio(true);

			// time + highest bid
			VBox itemInfo = new VBox(10);
			itemInfo.setPadding(new Insets(0, 0, 0, 10));

			Text itemTime = new Text("Remaining Time:" + "some time");
			Text itemName = new Text(item.getName());
			Text itemBid = new Text("Highest Bid:" + "someone rich guy's bid");
			itemInfo.getChildren().addAll(itemTime, itemName, itemBid);

			itemCard1.getChildren().addAll(itemImage, itemInfo);

			midLayout1.getChildren().addAll(itemCard1);
		}

		// Completed --> display closed items(history)
		Label cp = new Label("[Completed]");

		// right --> user info
		VBox rightLayout = new VBox();

		layout2.setLeft(leftLayout);

		// switching panes
		layout2.setCenter(midLayout);

		layout2.setRight(rightLayout);
		scene2 = new Scene(layout2, 700, 600);
		scene2.getStylesheets().add("app.css");

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
//			open = (ArrayList<Item>) fromServer.readObject();
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