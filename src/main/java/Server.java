import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Server extends Observable {
	// mongodb database
	private MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> coll;

	// static database...
	private static ArrayList<Item> items = new ArrayList<>();

	// uri
	private String uri = "mongodb+srv://test:test@cluster0.2lpk4.mongodb.net/AuctionDatabase?retryWrites=true&w=majority";

	// record client names
	private ArrayList<String> clients = new ArrayList<>();

	private void SetupNetworking() {

		// Request data from MongoDB database, store on server
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			db = mongoClient.getDatabase("AuctionDatabase");
			coll = db.getCollection("commodity");

			FindIterable<Document> fi = coll.find();
			Iterator<Document> iter = fi.iterator();

			while (iter.hasNext()) {
				Document doc = iter.next();
				String s = doc.toJson();
				System.out.println(s);

				// convert Json to Java object
				ObjectMapper mapper = new ObjectMapper();
				try {
					Item it = mapper.readValue(s, Item.class);

					// populate items with mongodb data
					items.add(it);

				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		int port = 8000;

		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Server started waiting for connection !");

			while (true) {
				// Waiting for connections ...
				Socket s = ss.accept();

				// Print the number of connected clients
				System.out.println("New connection !" + clients.size());

				// send auction items data to clients

				// Create new thread for that client
				Thread t = new Thread(new ClientHandler(s));
				t.start();

//				addObserver(writer);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Server server = new Server();
		server.SetupNetworking();

//		server.populateItems();
	}

	class ClientHandler implements Runnable {
		private ObjectInputStream reader;
		private Socket s;
//		private ClientObserver writer; // See Canvas. Extends ObjectOutputStream,

//        implements
//        Observer Socket clientSocket;
//

		public ClientHandler(Socket s) {
			this.s = s;
//			writer = new ClientObserver(s.getOutputStream());
		}
//

		@Override
		public void run() {

		}
	}
}