import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.github.BrianMichell.Utils.*;

public class GUI extends Runner {

	// Constants
	private final static Color RED = new Color(255, 0, 0);
	private final static Color GREEN = new Color(0, 200, 0);
	private final static Border margin = new EmptyBorder(10, 10, 10, 10);
	private final static Font f = new Font(Font.SANS_SERIF, 15, 15);
	// ArrayLists
	private static ArrayList<JLabel> marketChanges = new ArrayList<>(); // Contains the values of stocks
	private static ArrayList<JLabel> tick = new ArrayList<>(); // Contains the ticker information
	private static ArrayList<JLabel> updated = new ArrayList<>(); // When each field was last updated
	private static ArrayList<JLabel> gainsLosses = new ArrayList<>();
	// Objects
	private static stockAdd addAStock = new stockAdd();
	private static LocalTime time = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
	private static BriFrame frame;
	// Primitives
	private static boolean firstRun = true;

	public GUI(ArrayList<String> tickers, ArrayList<String> changes, ArrayList<Double> purchasePrice) {
		// Objects
		frame = new BriFrame(new JFrame("Stock Scraper Version 1.1.0"), new GridLayout(tickers.size() + 1, 4), true);
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JButton addStock = new JButton("Add a stock");
		// Action Listeners
		addStock.addActionListener((e) -> {
			addAStock.addStock();
		});

		frame.addLabel(new JLabel("Ticker Symbol", JLabel.CENTER));
		frame.addLabel(new JLabel("Day Change", JLabel.CENTER));
		frame.addLabel(new JLabel("Profit/Loss", JLabel.CENTER));
		frame.addLabel(new JLabel("Last Updated (Local Time)", JLabel.CENTER));

		for (int i = 0; i < changes.size(); i++) {
			marketChanges.add(new JLabel("", JLabel.CENTER));
			tick.add(new JLabel("", JLabel.CENTER));
			updated.add(new JLabel("", JLabel.CENTER));
			gainsLosses.add(new JLabel("", JLabel.CENTER));
		}
		// Elements added
		bar.add(menu);
		menu.add(addStock);
		newScrape(tickers, changes);
		frame.getFrame().setJMenuBar(bar);
		// Refresh thread
		for (int i = 0; i < changes.size(); i++) {
			final int tmp = i;
			new Thread(() -> {
				String str;
				while (true) {
					str = refreshOne(tmp);
					time = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
					if (!str.contains("span")) {
						marketChanges.get(tmp).setText(str);
						marketChanges.get(tmp).setForeground(setColor(str));
						gainsLosses.get(tmp).setText(calcGainLoss(str,purchasePrice.get(tmp)));
						updated.get(tmp).setText(time.toString());
					}

					frame.repack();
				}
			}).start();
		}

	}

	/**
	 * Requests a new scrape of all stock resources and refreshes all available
	 * assets
	 * 
	 * @param tickers
	 *            Ticker symbols of the stocks (Currently the full name and
	 *            symbol)
	 * @param changes
	 *            The current price per share, change since the opening of the
	 *            market, and percent change
	 */
	private static void newScrape(ArrayList<String> tickers, ArrayList<String> changes) {

		ArrayList<String> refreshed = new ArrayList<>();

		try {

			if (!firstRun) {
				frame.getFrame().revalidate();
			} else {
				firstRun = !firstRun;
				refreshed = changes;
			}

			for (int i = 0; i < refreshed.size(); i++) {
				marketChanges.get(i).setText(refreshed.get(i));
				marketChanges.get(i).setForeground(setColor(refreshed.get(i)));

				marketChanges.get(i).setFont(f);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < marketChanges.size(); i++) {
			tick.set(i, new JLabel());
			tick.get(i).setText(tickers.get(i));
			tick.get(i).setBorder(margin);
			tick.get(i).setFont(f);

			frame.getFrame().add(tick.get(i));
			frame.getFrame().add(marketChanges.get(i));
			frame.getFrame().add(gainsLosses.get(i));
			frame.getFrame().add(updated.get(i));
			frame.getFrame().repaint();
		}

		frame.repack();

	}

	/**
	 * Creates a blank slate of the current frame
	 * 
	 * @return The current frame
	 */
	public static JFrame getFrame() {
		for (int i = 0; i < tick.size(); i++) {
			frame.getFrame().remove(tick.get(i));
			frame.getFrame().remove(marketChanges.get(i));
		}
		tick.clear();
		marketChanges.clear();
		return frame.getFrame();
	}

	/**
	 * Decides what color the numbers should be set to
	 * 
	 * @param str
	 *            The current value being evaluated
	 * @return The desired color
	 */
	private static Color setColor(String str) {
		if (str.contains("+"))
			return GREEN;
		return RED;
	}
	
	private static String calcGainLoss(String str,double price){
		String val = Double.toString(Math.round((Double.valueOf(regexChecker(str))-price)*100.0)/100.0);
		return val;
	}
	
	private static double regexChecker(String input){
		String ex = "[\\d\\.]{3,}";
		Pattern checkRegex = Pattern.compile(ex);
		Matcher regexMatcher=checkRegex.matcher(input);
		while(regexMatcher.find()){
			if(regexMatcher.group().length()!=0){
				return Double.valueOf(regexMatcher.group().trim());
			}
		}
		System.out.println("Nothing found!");
		return 0.00;
	}

}
