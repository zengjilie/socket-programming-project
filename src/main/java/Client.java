import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application {

	// client information
	private static String clientName;
	private static String clientPassword;

	// when the server update, all clients will get the updates

	// track the latest commodities state
	private static ArrayList<Item> open = new ArrayList<>();
	private static ArrayList<Item> closed = new ArrayList<>();
	private static ArrayList<Bid> clientItems = new ArrayList<>();

	// I/O streams
	private static ObjectOutputStream toServer = null;
	private static ObjectInputStream fromServer = null;

	private static Socket socket = null;

	// Scene
	Scene scene1;
	Scene scene2;

	// important layouts
	private static BorderPane layout2 = new BorderPane();
	private static ScrollPane midLayout = new ScrollPane();

	private static VBox midLayout1 = new VBox();
	private static VBox midLayout2 = new VBox();

	ScrollPane sp2 = new ScrollPane();

	VBox clientItemList = new VBox(20);

	MediaPlayer mediaPlayer;
	// button
	Button openBtn = new Button("Open");
	Button compBtn = new Button("Complete");

	static boolean isMute = true;
	// thread
	Thread inputWait = null;

	@Override
	public void start(Stage primaryStage) throws IOException {

		// Scene 1
		VBox layout1 = new VBox(20);

		playMusic();

		// Logo
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

			try {
				loadScene2();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			primaryStage.setScene(scene2);

		});

		layout1.setPadding(new Insets(80));
		layout1.setAlignment(Pos.TOP_CENTER);
		layout1.setId("pane");
		layout1.getChildren().addAll(logo, nameInput, pwInput, btn1);

		scene1 = new Scene(layout1, 400, 500);
		scene1.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());

		primaryStage.setTitle("Virtual Auction");
		primaryStage.setScene(scene1);
		primaryStage.show();

	}

	public void loadScene2() throws IOException {

		// connect to server
		String serverIP = "localhost";
		try {
			socket = new Socket(serverIP, 5000);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Controller
		layout2.setPadding(new Insets(20));

		// left
		VBox leftLayout = new VBox(80);
		leftLayout.setPadding(new Insets(0, 10, 0, 0));
		leftLayout.setStyle("-fx-border-color:black;-fx-border-width:0 1 0 0");

		// logo
		Image Image2 = new Image("auctionlogo.png");
		ImageView logoImg2 = new ImageView(Image2);

		logoImg2.setFitHeight(70);
		logoImg2.setFitWidth(70);
		logoImg2.setPreserveRatio(true);
		Label logoTxt2 = new Label("Virtual Auciton".toUpperCase());
		logoTxt2.setFont(new Font("Times New Roman", 12));

		VBox logo2 = new VBox();
		logo2.setAlignment(Pos.CENTER);
		logo2.getChildren().addAll(logoImg2, logoTxt2);

		// Buttons --> switch between
		// open button
		VBox btnBox = new VBox(10);
		openBtn.setPrefHeight(100);
		openBtn.setPrefWidth(100);
		// line
		Line line = new Line();
		line.setStartX(0);
		line.setStartY(0);
		line.setEndX(100);
		line.setEndY(0);
		// complete button
		compBtn.setPrefHeight(100);
		compBtn.setPrefWidth(100);

		btnBox.getChildren().addAll(openBtn, line, compBtn);

		// mute
		Button muteBtn = new Button("Mute");
		muteBtn.setPrefWidth(100);

		muteBtn.setOnAction(e -> {
			isMute = !isMute;
			if (isMute) {
				muteBtn.setText("unMute");
			} else {
				muteBtn.setText("Mute");
			}
			mediaPlayer.setMute(isMute);
		});

		// button --> exit
		Button exitBtn = new Button("Exit");
		exitBtn.setPrefWidth(100);

		leftLayout.getChildren().addAll(logo2, btnBox, muteBtn, exitBtn);

		// middle --> displaying 2 sets sections --> ongoing/completed
		// middle is Scrollable
		midLayout.setStyle("-fx-background-color:transparent");
		midLayout.setPrefHeight(600);

		// render item list --> thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Runnable render = new Runnable() {
					@Override
					public void run() {
						render();
					}

				};

				while (socket.isConnected()) {
					try {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						open = (ArrayList<Item>) fromServer.readObject();

						System.out.println(open.get(2).getBidder());
						System.out.println(open.get(2).getBid());

						Platform.runLater(render);
					} catch (IOException e) {
						e.printStackTrace();
						break;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}

				}

				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		});

		t.setDaemon(true);
		t.start();

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
		sp2.setStyle("-fx-background-color:transparent");
		sp2.setPrefHeight(600);
		Label clListTitle = new Label("Your Items");
		clListTitle.setFont(new Font("Times New Roman", 20));

		for (Bid item : clientItems) {

			Text itemName = new Text("Name: " + item.itemName);
			// format currency ...
			Locale locale = new Locale("en", "US");
			NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
			Text itemPrice = new Text("Your Bid: " + formatter.format(item.clientBid));

			clientItemList.getChildren().addAll(itemName, itemPrice);
		}

		rightLayout.getChildren().addAll(clientInfo, line2, clListTitle, sp2);
		sp2.setContent(clientItemList);

		layout2.setLeft(leftLayout);
		layout2.setRight(rightLayout);

		scene2 = new Scene(layout2, 1200, 600);
		scene2.getStylesheets().add("app.css");

		// decode data from server

		exitBtn.setOnAction(e -> {
			try {
				System.out.println("Client socket closed!");
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Platform.exit();
		});

	}

	public void render() {
		midLayout1 = new VBox(30);
		midLayout1.setPadding(new Insets(20));

		// complete item list
		midLayout2 = new VBox(30);
		midLayout2.setPadding(new Insets(20));

		// use setContent to change open/complete different nodes
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
			if (!item.getSold()) {

			}
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
			Text itemBidder = new Text("Bidder: " + item.getBidder());
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
						clientItems.add(newBid);

						clientItemList.getChildren().clear();

						for (Bid it : clientItems) {
							Text itName = new Text("Item: " + it.itemName);
							// format currency ...
							Locale locale2 = new Locale("en", "US");
							NumberFormat formatter2 = NumberFormat.getCurrencyInstance(locale2);
							Text itPrice = new Text("Your Bid: " + formatter.format(it.clientBid));

							clientItemList.getChildren().addAll(itName, itPrice);
						}

						// rerender
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

		// Completed --> display completed items
		Label cp = new Label("Complete list".toUpperCase());
		cp.setFont(new Font("Times New Roman", 20));

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
			Text itemBidder = new Text("Buyer: " + item.getBidder());

			itemInfo.getChildren().addAll(itemName, itemBid, itemBidder);

			itemCard.getChildren().addAll(itemId, itemImage, itemInfo);

			midLayout2.getChildren().addAll(itemCard);
		}

		midLayout.setContent(midLayout1);
		layout2.setCenter(midLayout);
	}

	private void playMusic() {
		String path = getClass().getResource("music.mp3").getPath();
		Media media = new Media(new File(path).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();
		mediaPlayer.setMute(isMute);
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 
	 * @author alex Bid class, send Bid object to the server, and set a new bid
	 */

}
