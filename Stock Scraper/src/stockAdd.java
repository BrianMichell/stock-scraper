import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class stockAdd {

	private static SavedStocks storage = new SavedStocks();;
	JFrame frame = new JFrame("Add a stock");

	public stockAdd() {
		GridLayout layout = new GridLayout(2, 1);
		JTextField text = new JTextField();
		JTextField purchasePrice = new JTextField();
		text.setToolTipText("The ticker symbol of the stock");
		purchasePrice.setToolTipText("Price the stock was purchased at");
		JButton submit = new JButton("Submit");

		submit.addActionListener((e) -> {
			String str = text.getText();
			str = str.toUpperCase();
			// TODO Add a visible error message
			if (str == null || str.equals("")) {
				System.out.println("There is nothing in the text field!");
			} else {
				if (!storage.isDuplicate(str)) {
					System.out.println("Adding " + str + " to the list of stocks\nThis may require a restart.");
					storage.addToFile(str);
				} else {
					System.out.println(str + " is already listed.");
				}
			}
			try {
				Runner.newFrame(GUI.getFrame());
			} catch (IOException ex) {
				System.out.println("There was an error reconstructing frame");
			}
			frame.setVisible(false);
		});

		frame.setLayout(layout);
		frame.add(text);
		frame.add(purchasePrice);
		frame.add(submit);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Shows the input box. Clicking the submit button will close it.
	 */
	public void addStock() {
		frame.setVisible(true);
	}

}
