import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	// uri
	private String uri = "mongodb+srv://test:test@cluster0.2lpk4.mongodb.net/AuctionDatabase?retryWrites=true&w=majority";

	// itemList
	static ArrayList<Item> itemList = new ArrayList<>();
	// clientHanlders
	static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

	static int min = 5;
	static int sec = 0;
	static boolean ongoing = true;

	private void SetupNetworking() throws IOException {
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (sec == 0) {
						sec = 60;
						min--;
					}
					sec--;
					if (sec == 0 && min == 0) {
						ongoing = false;
						break;
					}
					System.out.println("mins: " + min + " secs: " + sec);
				}
			};

		});
		t1.start();

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
					itemList.add(it);

				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

			}
		}

		int port = 5000;

		try {
			ss = new ServerSocket(port);
			System.out.println("Server started waiting for connection!");

			while (true) {
				Socket s = ss.accept();

				ClientHandler clientHandler = new ClientHandler(s);

				clientHandlers.add(clientHandler);
				System.out.println(clientHandlers.size() + " clients");

				Thread t = new Thread(clientHandler);
				t.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
			ss.close();
		}
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.SetupNetworking();
	}

	class ClientHandler implements Runnable {
		private Socket socket;
		public ObjectOutputStream toClient;
		public ObjectInputStream fromClient;

		public ClientHandler(Socket s) throws IOException {
			socket = s;
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());
		}

		@Override
		public void run() {
			try {
				toClient.writeObject(itemList);
//				Thread t = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						while (ongoing) {
//							try {
//								Thread.sleep(5000);
//							} catch (InterruptedException e) {
//
//								e.printStackTrace();
//							}
//
//						}
//					}
//
//				});
//				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (socket.isConnected()) {
				try {
					Bid clientBid = (Bid) fromClient.readObject();

					// update itemList
					for (Item item : itemList) {
						if (item.getItemId() == clientBid.itemId) {
							item.setBidder(clientBid.clientName);
							item.setBid(clientBid.clientBid);
						}
					}

					System.out.println(itemList.get(2).getBidder());
					System.out.println(itemList.get(2).getBid());
					System.out.println(clientHandlers.size() + "clients");

					// broadcast new itemList
					for (ClientHandler c : clientHandlers) {
						c.toClient.writeObject(itemList);
						System.out.println("cast!!!");
					}

				} catch (IOException e) {
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
			}

			clientHandlers.remove(this);
		}

		public void removeClientHandler() {
			clientHandlers.remove(this);
			System.out.println("1 client left");
		}
	}
}