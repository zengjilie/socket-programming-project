import java.io.Serializable;

public class Bid implements Serializable {
	public int itemId;
	public double clientBid;
	public String clientName;

	public Bid(int itemId, double clientBid, String clientName) {
		this.itemId = itemId;
		this.clientBid = clientBid;
		this.clientName = clientName;
	}

}
