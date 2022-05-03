import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	ServerSocket ss;
	// mongodb database
	private MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> coll;

	// static database...
	private static ArrayList<Item> itemList = new ArrayList<>();

	// uri
	private String uri = "mongodb+srv://test:test@cluster0.2lpk4.mongodb.net/AuctionDatabase?retryWrites=true&w=majority";

	// record client names
	private ArrayList<String> clientList = new ArrayList<>();

	private void SetupNetworking() throws IOException {

		// Request data from MongoDB database, store on server
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			db = mongoClient.getDatabase("AuctionDatabase");
			coll = db.getCollection("commodity");

			FindIterable<Document> fi = coll.find();
			Iterator<Document> iter = fi.iterator();

			while (iter.hasNext()) {
				Document doc = iter.next();
				String s = doc.toJson();
//				System.out.println(s);

				// convert Json to Java object
				ObjectMapper mapper = new ObjectMapper();
				try {
					Item it = mapper.readValue(s, Item.class);

					// Store items in list
					itemList.add(it);

				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		int port = 5000;

		try {
			ss = new ServerSocket(port);
			System.out.println("Server started waiting for connection !");

			while (true) {
				// Waiting for connections ...
				Socket s = ss.accept();

				// Add new Client[info] to client list...

				// Print the number of connected clients
				System.out.println("New connection !" + clientList.size());

				// Send auction items data to clients...

				// Create new thread for that client
				Thread t = new Thread(new ClientHandler(s));
				t.start();

//				addObserver(writer);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Server socket closed!");
			ss.close();
		}
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.SetupNetworking();
//		server.populateItems();
	}

	class ClientHandler implements Runnable {
		private Socket s;
//		private ClientObserver writer; // See Canvas. Extends ObjectOutputStream,

//        implements
//        Observer Socket clientSocket;
		private InputStream is;
		private OutputStream os;
		private ObjectInputStream fromClient;
		private ObjectOutputStream toClient;

		public ClientHandler(Socket s) {
			this.s = s;
//			writer = new ClientObserver(s.getOutputStream());
		}
//

		@Override
		public void run() {
			try {
				is = s.getInputStream();
				os = s.getOutputStream();

				toClient = new ObjectOutputStream(os);
				fromClient = new ObjectInputStream(is);

				// send itemList --> client
				toClient.writeObject(itemList);
				System.out.println("itemList has been sent to client!");

				// Get client bid info <-- client
				Bid clientBid = (Bid) fromClient.readObject();

				System.out.println("Got bid info from client!");
				System.out.println(clientBid.clientName);
				System.out.println(clientBid.clientBid);
				System.out.println(clientBid.itemId);

				// Change the info of that item
				for (Item item : itemList) {
					if (clientBid.itemId == item.getItemId()) {
						item.setBid(clientBid.clientBid);
						item.setBidder(clientBid.clientName);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IO exception!");
			} catch (ClassNotFoundException e) {
				System.out.println("Can't find class Bid!");
			}

		}
	}
}