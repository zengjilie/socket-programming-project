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

	// record client count
	int clientCount = 0;

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.SetupNetworking();
	}

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

				// convert Json to Java object
				ObjectMapper mapper = new ObjectMapper();
				try {
					Item it = mapper.readValue(s, Item.class);

					// Store items in list

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

		// Start server
		try {
			ss = new ServerSocket(port);
			System.out.println("Server started waiting for connection !");

			while (true) {
				// Waiting for connections ...
				Socket s = ss.accept();

				// Add new observer
				ClientObserver co = new ClientObserver(s.getOutpuStream());

				clientCount++;
				System.out.println(clientCount + " clients connected!");
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
		}
//

		@Override
		public void run() {
			try {
				is = s.getInputStream();
				os = s.getOutputStream();

				toClient = new ObjectOutputStream(os);
				fromClient = new ObjectInputStream(is);

				toClient.writeObject(itemList);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}