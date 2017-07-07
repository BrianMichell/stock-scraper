import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.github.BrianMichell.Utils.BriFrame;

public class GUI extends Runner {

	// Constants
	private final static Color RED = new Color(255, 0, 0);
	private final static Color GREEN = new Color(0, 200, 0);
	private final static Border margin = new EmptyBorder(10, 10, 10, 10);
	private final static Font f = new Font(Font.SANS_SERIF, 15, 15);
	// ArrayLists
	private static ArrayList<JLabel> gainsLosses = new ArrayList<>(); // Contains
																		// the
																		// values
																		// of
																		// stocks
	private static ArrayList<JLabel> tick = new ArrayList<>(); // Contains the
																// ticker
																// information
	private static ArrayList<JLabel> updated = new ArrayList<>(); // When each
																	// field was
																	// last
																	// updated
	// Objects
	private static JFrame frame = new JFrame("Stock Scraper Version 1.0.2");;
	private static stockAdd addAStock = new stockAdd();
	private static LocalTime time = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
	private static BriFrame bFrame;
	// Primitives
	private static int position = 0;
	private static boolean firstRun = true;

	public GUI(ArrayList<String> tickers, ArrayList<String> changes) {
		// Objects
		bFrame = new BriFrame(new JFrame("Stock Scraper Version 1.0.2"),new GridLayout(tickers.size() + 1, 3),true);
		GridLayout layout = new GridLayout(tickers.size() + 1, 3);
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JButton addStock = new JButton("Add a stock");
		// Action Listeners
		addStock.addActionListener((e) -> {
			addAStock.addStock();
		});

		frame.add(new JLabel("Ticker Symbol", JLabel.CENTER));
		frame.add(new JLabel("Day Change", JLabel.CENTER));
		frame.add(new JLabel("Last Updated (Local Time)", JLabel.CENTER));

		for (int i = 0; i < changes.size(); i++) {
			gainsLosses.add(new JLabel("", JLabel.CENTER));
			tick.add(new JLabel("", JLabel.CENTER));
			updated.add(new JLabel("", JLabel.CENTER));
		}
		// Elements added
		bar.add(menu);
		menu.add(addStock);
		newScrape(tickers, changes);
		// GUI setup
		frame.setLayout(layout);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(bar);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		// Refresh thread
		for (int i = 0; i < changes.size(); i++) {
			final int tmp = i;
			new Thread(() -> {
				String str;
				while (true) {
					str = refreshOne(tmp);
					time = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
					if (!str.contains("span")) {
						gainsLosses.get(tmp).setText(str);
						gainsLosses.get(tmp).setForeground(setColor(str));
						updated.get(tmp).setText(time.toString());
					}

					frame.repaint();
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
				frame.revalidate();
			} else {
				firstRun = !firstRun;
				refreshed = changes;
			}

			for (int i = 0; i < refreshed.size(); i++) {
				gainsLosses.get(i).setText(refreshed.get(i));
				gainsLosses.get(i).setForeground(setColor(refreshed.get(i)));

				gainsLosses.get(i).setFont(f);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < gainsLosses.size(); i++) {
			tick.set(i, new JLabel());
			tick.get(i).setText(tickers.get(i));
			tick.get(i).setBorder(margin);
			tick.get(i).setFont(f);

			frame.add(tick.get(i));
			frame.add(gainsLosses.get(i));
			frame.add(updated.get(i));
			frame.repaint();
		}

		frame.repaint();

	}

	/**
	 * Creates a blank slate of the current frame
	 * 
	 * @return The current frame
	 */
	public static JFrame getFrame() {
		for (int i = 0; i < tick.size(); i++) {
			frame.remove(tick.get(i));
			frame.remove(gainsLosses.get(i));
		}
		tick.clear();
		gainsLosses.clear();
		return frame;
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

}
