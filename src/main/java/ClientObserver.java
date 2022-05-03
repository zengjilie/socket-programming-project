import java.util.Observable;
import java.util.Observer;

public class ClientObserver implements Observer
{

	// Send updated itemList to their client
	public ClientObserver(OutputStream out) {

	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("good");
	}
}
