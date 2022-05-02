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
import javafx.scene.layout.VBox;
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

	@Override
	public void start(Stage primaryStage) throws IOException {

		// 1. Welcome page --> Greetings, get users name and password(no authentication)
		VBox layout1 = new VBox(20);

		// logo
		Label logo = new Label("Virtual Auciton".toUpperCase());
		logo.setFont(new Font("Times New Roman", 24));

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
		});

		layout1.setPadding(new Insets(20));
		layout1.setAlignment(Pos.CENTER);
		layout1.getChildren().addAll(logo, nameInput, pwInput, btn1);

		Scene scene1 = new Scene(layout1, 400, 500);

		primaryStage.setTitle("Virtual Auction"); // Set the stage title
		primaryStage.setScene(scene1); // Place the scene in the stage
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