import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.BrianMichell.Utils.BriRegex;

public class Runner {

	private final static String BASE_URL = "http://finance.yahoo.com/quote/";
	private final static String SEARCH = "/?p=";

	private static String url;
	private static ArrayList<String> ticker;
	private static ArrayList<Double> purchasePrice;
	private static Document document;
	private static BriRegex reg = new BriRegex();

	public static void main(String[] args) throws Exception {

		ticker = new ArrayList<>();
		purchasePrice = new ArrayList<>();
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

		new GUI(names, changes, purchasePrice);

	}

	/**
	 * Pulls the current market value, monetary change, and percentage change of
	 * a particular stock
	 * 
	 * @return Change in a particular stock
	 */
	public static String gainLoss() {
		Elements el = document.getElementsByTag("span");
		String ret = "";
		for (Element e : el) {
			String str = e.toString();
			if (str.contains("Trsdu(0.3s) Fw(b) Fz(36px) Mb(-4px) D(ib)") || str.contains("Trsdu(0.3s) Fw(500) Pstart(10px) Fz(24px)")) {
				try {
					String tmp = reg.find("[>]+(-)?+[0-9|(-|\\.\\(|\\)|%| )?]+[<]", e.toString()).get(0);
					tmp = trimTag(tmp);
					ret += tmp + " ";
				} catch (IndexOutOfBoundsException ex) {
					ex.printStackTrace();
				}
			}
		}

		return ret;
	}

	/**
	 * Pulls the name and stock symbol off Yahoo Finance
	 * 
	 * @return Name and stock symbol of stock.
	 */
	public static String ticker() {
		ArrayList<String> ret;
		Elements el = document.getElementsByTag("h1");
		for (Element e : el) {
			System.out.println(e);
			ret = reg.find("[>]+[" + reg.CHAR + "|( |,|\\.|&|;)?]+[<]", e.toString());
			try {
				return trimTag(ret.get(0));
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
		return "Error finding symbol! Please restart program.";
	}

	/**
	 * Refreshes every asset available all at once.
	 * 
	 * @return A batch of the most up-to-date information on all the stocks (Do
	 *         not use except to setup the frame of the GUI)
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
	 * To be used to update an individual stock. In an interminable thread to
	 * make sure program has the most up-to-date information on the stocks.
	 * 
	 * @param position
	 *            The index number of the stock
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

		return change;
	}

	/**
	 * Adds a ticker symbol to the existing Array List
	 * 
	 * @param str
	 *            Ticker symbol of desired stock
	 */
	public static void addToList(String str) {
		ticker.add(findSymbol(str));
		purchasePrice.add(findVal(str));
	}

	public static void newFrame(JFrame frame) throws IOException {
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

		new GUI(names, changes, purchasePrice);
	}

	private static double findVal(String s) {
		ArrayList<String> str = reg.find("[" + reg.DOUBLE + "]{3,}", s);
		try {
			return Double.valueOf(str.get(0));
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			return (0.00);
		}
	}

	private static String findSymbol(String s) {
		ArrayList<String> str = reg.find("[" + reg.CHAR + "]{1,}", s);
		try {
			return str.get(0).toUpperCase();
		} catch (IndexOutOfBoundsException e) {
			return "---";
		}
	}

	private static String trimTag(String str) {
		String tmp = str;
		tmp = tmp.replace(">", "");
		tmp = tmp.replace("<", "");
		return tmp;
	}

}
