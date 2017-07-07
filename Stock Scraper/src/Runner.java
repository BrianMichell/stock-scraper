import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Runner {

	private static String url;
	private static ArrayList<String> ticker;
	private static ArrayList<Double> purchasePrice;
	private final static String BASE_URL = "http://finance.yahoo.com/quote/";
	private final static String SEARCH = "/?p=";
	private static Document document;

	public static void main(String[] args) throws Exception {

		ticker = new ArrayList<>();
		new SavedStocks();

		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> changes = new ArrayList<>();
		for (int i = 0; i < ticker.size(); i++) {

			String stock = ticker.get(i);
			url = BASE_URL + stock + SEARCH + stock;
			document = Jsoup.connect(url).get();
			String tick = ticker() + "   ";
			String change = gainLoss();
			System.out.println(tick + ": " + change);
			names.add(tick);
			changes.add(change);
		}

		new GUI(names, changes);

	}

	/**
	 * Pulls the current market value, monetary change, and percentage change of a particular stock
	 * @return Change in a particular stock
	 */
	public static String gainLoss() {
		Elements el = document.getElementsByTag("span");
		String ret = "";
		for (Element e : el) {
			String str = e.toString();
			if (str.contains("Trsdu(0.3s) Fw(b) Fz(36px) Mb(-4px) D(ib)")
					|| str.contains("Trsdu(0.3s) Fw(500) Pstart(10px) Fz(24px)")) {
				boolean startAdding = false;
				for (int i = 1; i < str.length(); i++) {
					if (str.substring(i, i + 1).equals("<") || str.substring(i, i + 1).equals(">")) {
						startAdding = !startAdding;
						i++;
					}
					if (startAdding && i < str.length()) {
						ret += str.substring(i, i + 1);
					}
				}
				ret += " ";
			}
		}
		return ret;
	}

	/**
	 * Pulls the name and stock symbol off Yahoo Finance
	 * @return Name and stock symbol of stock.
	 */
	public static String ticker() {
		String ret = "";
		Elements el = document.getElementsByTag("h1");
		for (Element e : el) {
			String str = e.toString();
			boolean startAdding = false;
			for (int i = 1; i < str.length(); i++) {
				if (str.substring(i, i + 1).equals("<") || str.substring(i, i + 1).equals(">")) {
					startAdding = !startAdding;
					i++;
				}
				if (startAdding && i < str.length()) {
					ret += str.substring(i, i + 1);
				}
			}
		}
		return ret;
	}

	/**
	 * Refreshes every asset available all at once.
	 * @return A batch of the most up-to-date information on all the stocks (Do not use except to setup the 
	 * frame of the GUI)
	 */
	public static ArrayList<String> refreshAll() {

		ArrayList<String> changes = new ArrayList<>();

		for (int i = 0; i < ticker.size(); i++) {

			System.out.println("Pass " + i + " out of " + ticker.size());

			String stock = ticker.get(i);
			url = BASE_URL + stock + SEARCH + stock;
			
			try {
				document = Jsoup.connect(url).get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String change = gainLoss();
			changes.add(change);
			
		}
		
		return changes;

	}

	/**
	 * To be used to update an individual stock. In an interminable thread to make sure program has the most
	 * up-to-date information on the stocks.
	 * @param position The index number of the stock
	 * @return The updated change in market condition for the stock
	 */
	public static String refreshOne(int position) {
		String symbol = ticker.get(position);
		url = BASE_URL + symbol + SEARCH + symbol;

		try {
			document = Jsoup.connect(url).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String change = gainLoss();

		//System.out.println("Stock number " + position + " is now " + change);

		return change;
	}

	/**
	 * Adds a ticker symbol to the existing Array List
	 * @param str Ticker symbol of desired stock
	 */
	public static void addToList(String str) {
		String tick="";
		String tmp="";
		for(int i=0; i<str.length(); i++){
			tmp=str.substring(i,i+1);
			if(tmp.equals(" ")){
				break;
			} else {
				tick+=tmp;
			}
		}
		ticker.add(tick.toUpperCase());
		purchasePrice.add(Double.parseDouble(str));

	}
	
	public static void newFrame(JFrame frame) throws IOException{
		ticker = new ArrayList<>();
		new SavedStocks();

		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> changes = new ArrayList<>();
		for (int i = 0; i < ticker.size(); i++) {

			String stock = ticker.get(i);
			url = BASE_URL + stock + SEARCH + stock;
			document = Jsoup.connect(url).get();
			String tick = ticker();
			String change = gainLoss();
			names.add(tick);
			changes.add(change);
		}

		new GUI(names, changes);
	}

}
