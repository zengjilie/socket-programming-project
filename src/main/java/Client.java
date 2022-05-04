import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javafx.application.Application;
import javafx.application.Platform;
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
	private String clientName = "Bob";
	private String clientPassword;

	// when the server update, all clients will get the updates

	// track the latest commodities state
	private ArrayList<Item> open = new ArrayList<>();

	// history
	private ArrayList<Item> closed = new ArrayList<>();

	// client items
	private ArrayList<Item> clientItems = new ArrayList<>();
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

		// real data
		// Create a socket to connect to the server
		String serverIP = "localhost"; // change [localhost] to actual server IP
		s = new Socket(serverIP, 5000);
		System.out.println("New client created!");

		// client <--> server data exchange
		fromServer = new ObjectInputStream(s.getInputStream());
		toServer = new ObjectOutputStream(s.getOutputStream());
		try {
			open = (ArrayList<Item>) fromServer.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
			clientName = nameInput.getText();
			clientPassword = pwInput.getText();
			System.out.println(clientName);
			System.out.println(clientPassword);
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

		// Buttons --> switching different item lists
		// open button
		VBox btnBox = new VBox(10);
		Button openBtn = new Button("Open");
		openBtn.setPrefHeight(100);
		openBtn.setPrefWidth(100);
		// line
		Line line = new Line();
		line.setStartX(0);
		line.setStartY(0);
		line.setEndX(100);
		line.setEndY(0);
		// complete button
		Button compBtn = new Button("Complete");
		compBtn.setPrefHeight(100);
		compBtn.setPrefWidth(100);

		btnBox.getChildren().addAll(openBtn, line, compBtn);

		// button --> exit
		Button exitBtn = new Button("Exit");
		exitBtn.setPrefWidth(100);

		leftLayout.getChildren().addAll(logo2, btnBox, exitBtn);

		// middle --> displaying 2 sets sections --> ongoing/completed
		// middle is Scrollable
		ScrollPane midLayout = new ScrollPane();
		midLayout.setStyle("-fx-background-color:transparent");
		midLayout.setPrefHeight(600);

		// open item list
		VBox midLayout1 = new VBox(30);
		midLayout1.setPadding(new Insets(20));

		// complete item list
		VBox midLayout2 = new VBox(30);
		midLayout2.setPadding(new Insets(20));

		// use setContent to change open/complete different nodes
		// default open
		midLayout.setContent(midLayout1);
		// switching open/complete
		openBtn.setOnAction(e -> {
			// see open item list
			midLayout.setContent(midLayout1);
		});
		compBtn.setOnAction(e -> {
			midLayout.setContent(midLayout2);
		});

		// open --> display opening items
		Label op = new Label("Open List".toUpperCase());
		midLayout1.getChildren().add(op);
		op.setFont(new Font("Times New Roman", 20));

		// Add item card to midLayout1
		for (Item item : open) {
			HBox itemCard = new HBox(20);
			// id + image + info + button
			// itemId
			Text itemId = new Text("Id: " + String.valueOf(item.getItemId()));

			// image
			Image itemImg = new Image(item.getImage());
			ImageView itemImage = new ImageView(itemImg);
			itemImage.setFitHeight(150);
			itemImage.setFitWidth(150);
			itemImage.setPreserveRatio(true);

			// info
			VBox itemInfo = new VBox(12);
			itemInfo.setPadding(new Insets(0, 0, 0, 10));
			itemInfo.setPrefWidth(300);
			// time
			Text itemTime = new Text("Remaining Time: " + "05:00");
			// name
			Text itemName = new Text(item.getName());

			// bid
			double bid = item.getBid();
			Locale locale = new Locale("en", "US");
			NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
			Text itemBid = new Text("Highest Bid: " + formatter.format(bid));
			// bidder
			Text itemBidder = new Text("Bidder: " + "N/A");
			itemInfo.getChildren().addAll(itemTime, itemName, itemBid, itemBidder);

			// Get bid from client
			VBox bidLayout = new VBox(20);
			bidLayout.setPadding(new Insets(0, 0, 0, 50));
			bidLayout.setAlignment(Pos.TOP_RIGHT);
			// bid input + bid button
			// bid input
			TextField bidInput = new TextField();
			bidInput.setPromptText("Enter your bid");
			bidInput.setFocusTraversable(false);

			// bid button
			Button bidBtn = new Button("BID");
			bidBtn.setPrefWidth(70);
			// bid warning sign
			Text bidWarning = new Text();
			// validate input
			bidBtn.setOnAction(e -> {
				try {
					double bidAmount = Double.parseDouble(bidInput.getText());
					if (bidAmount <= item.getBid()) {
						bidWarning.setText("Bid not big enough!");
					} else {
						// send bid info to server --> server update all clients
						Bid newBid = new Bid(item.getItemId(), bidAmount, clientName, item.getName());
						toServer.writeObject(newBid);
					}
				} catch (NumberFormatException ex) {
					bidWarning.setText("Input Must Be a Number!");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});

			bidLayout.getChildren().addAll(bidInput, bidBtn, bidWarning);

			itemCard.getChildren().addAll(itemId, itemImage, itemInfo, bidLayout);

			midLayout1.getChildren().addAll(itemCard);
		}

		// Completed item list

		// Completed --> display completed items
		Label cp = new Label("Complete list".toUpperCase());
		cp.setFont(new Font("Times New Roman", 20));
//		midLayout.setContent(midLayout2);

		midLayout2.getChildren().add(cp);

		// Add item card to midLayout2
		for (Item item : closed) {
			HBox itemCard = new HBox(20);
			// id + image + info
			// itemId
			Text itemId = new Text(String.valueOf("Id: " + item.getItemId()));

			// image
			Image itemImg = new Image(item.getImage());
			ImageView itemImage = new ImageView(itemImg);
			itemImage.setFitHeight(150);
			itemImage.setFitWidth(150);
			itemImage.setPreserveRatio(true);

			// info
			// highest bid
			VBox itemInfo = new VBox(10);
			itemInfo.setPadding(new Insets(0, 0, 0, 10));
			// name
			Text itemName = new Text("Name:" + item.getName());
			// bid
			double bid = item.getBid();
			Locale locale = new Locale("en", "US");
			NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
			Text itemBid = new Text("Highest Bid: " + formatter.format(bid));
			// bidder
			Text itemBidder = new Text("Buyer: " + "Judy");
			itemInfo.getChildren().addAll(itemName, itemBid, itemBidder);

			itemCard.getChildren().addAll(itemId, itemImage, itemInfo);

			midLayout2.getChildren().addAll(itemCard);
		}

		// Right --> client info + client items
		VBox rightLayout = new VBox(20);
		rightLayout.setPadding(new Insets(0, 0, 0, 10));
		// client info
		Label clientInfo = new Label(clientName);
		clientInfo.setFont(new Font("Times New Roman", 20));

		// line
		Line line2 = new Line();
		line2.setStartX(0);
		line2.setStartY(0);
		line2.setEndX(160);
		line2.setEndY(0);

		// client items
		ScrollPane sp2 = new ScrollPane();
		sp2.setStyle("-fx-background-color:transparent");
		VBox clientItemList = new VBox(20);
		Label clListTitle = new Label("Your Items");
		clListTitle.setFont(new Font("Times New Roman", 20));

		for (Item item : clientItems) {
			Text itemName = new Text("Name: " + item.getName());
			// format currency ...
			Text itemPrice = new Text(String.valueOf(item.getBid()));
			clientItemList.getChildren().addAll(itemName, itemPrice);
		}

		rightLayout.getChildren().addAll(clientInfo, line2, clListTitle, sp2);
		sp2.setContent(clientItemList);

		layout2.setLeft(leftLayout);

		// switching panes
		layout2.setCenter(midLayout);

		layout2.setRight(rightLayout);
		scene2 = new Scene(layout2, 1200, 600);
		scene2.getStylesheets().add("app.css");

		primaryStage.setTitle("Virtual Auction"); // Set the stage title
		primaryStage.setScene(scene2); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		// decode data from server
		exitBtn.setOnAction(e -> {
			try {
				System.out.println("Client socket closed!");
				s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Platform.exit();
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 
	 * @author alex Bid class, send Bid object to the server, and set a new bid
	 */

}
