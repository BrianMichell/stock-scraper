import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SavedStocks extends Runner {
	
	private static FileReader fr;
	private static BufferedReader br;
	private static FileWriter fw;
	private static PrintWriter pw;

	public SavedStocks() {

		try {
			fr = new FileReader("SavedStocks.txt");
			br = new BufferedReader(fr);

			String str = "";
			while ((str = br.readLine()) != null) {
				Runner.addToList(str);
			}

			br.close();

		} catch (IOException e) {
			System.out.println("Error! No file found! \nCreating a new file for you.");

			try {
				fw = new FileWriter("SavedStocks.txt");
				pw = new PrintWriter(fw);
				pw.close();

			} catch (IOException ex) {
				System.out.println("Error! Could not create a file for you.");
			}

		}

	}

	/**
	 * Adds a new stock ticker to the saved list.
	 * @param str Desired stock ticker.
	 */
	public void addToFile(String str) {
		try {

			fr = new FileReader("SavedStocks.txt");
			br = new BufferedReader(fr);

			String current = "";
			ArrayList<String> saved = new ArrayList<>();
			
			while ((current = br.readLine()) != null) {
				saved.add(current);
			}

			saved.add(str);

			br.close();

			fw = new FileWriter("SavedStocks.txt");
			pw = new PrintWriter(fw);

			for (int i = 0; i < saved.size(); i++) {
				pw.println(saved.get(i));
			}

			pw.close();
		} catch (IOException ex) {
			System.out.println("Error! Could not find that file!");
		}

	}

	/**
	 * Checks if the program is already tracking a particular stock
	 * @param str Ticker symbol of the desired stock
	 * @return True if the program is already tracking the stock or if there is an error. False otherwise.
	 */
	public boolean isDuplicate(String str) {

		try {
			fr = new FileReader("SavedStocks.txt");
			br = new BufferedReader(fr);

			String current = "";
			
			while ((current = br.readLine()) != null) {
				if (current.equals(str)) {
					return true;
				}
			}

			br.close();

		} catch (IOException e) {
			System.out.println("There was an error checking this file!");
			return true;
		}

		return false;
	}

}
