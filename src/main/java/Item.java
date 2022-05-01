import java.io.Serializable;

public class Item implements Serializable {
	private Object _id; // _id
	private int itemId; // commodity identifier
	private String name; // record the commodity's name
	private int bid; // start with base price, later record the highest bid
	private String bidder; // bidder with the highest price
	private String image; // image url
	private boolean sold; // default false, if sold --> true

//	public Item(int itemId, String name, int bid, String bidder, String image) {
//		this.itemId = itemId;
//		this.name = name;
//		this.bid = bid;
//		this.bidder = bidder;
//		this.image = image;
//		this.sold = false;
//	}

	public String getName() {
		return name;
	}

	public int getBid() {
		return bid;
	}

	public boolean setBid(int bid, String bidder) {
		if (!sold) {
			if (bid > this.bid) {
				this.bid = bid;
				this.bidder = bidder;

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getBidder() {
		return bidder;
	}

	public void setSold() {
		sold = true;
	}

	public Object get_id() {
		return _id;
	}

	public void set_id(Object _id) {
		this._id = _id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}
}
