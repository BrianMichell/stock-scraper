import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.github.BrianMichell.Utils.BriFrame;

public class stockAdd {

	private static SavedStocks storage = new SavedStocks();;
	JFrame frame = new JFrame("Add a stock");
	BriFrame bFrame = new BriFrame(new JFrame("Add a stock"), new GridLayout(2,1),false);

	public stockAdd() {
		JTextField text = new JTextField();
		JTextField purchasePrice = new JTextField();
		text.setToolTipText("The ticker symbol of the stock");
		purchasePrice.setToolTipText("Price the stock was purchased at");
		JButton submit = new JButton("Submit");

		submit.addActionListener((e) -> {
			String str = text.getText();
			String money = purchasePrice.getText();
			str = str.toUpperCase();
			// TODO Add a visible error message
			if (str == null || str.equals("")) {
				System.out.println("There is nothing in the text field!");
			} else {
				if (!storage.isDuplicate(str)) {
					System.out.println("Adding " + str + " bought at "+money+ " to the list of stocks\nThis may require a restart.");
					storage.addToFile(str+" "+money);
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

		bFrame.getFrame().add(text);
		bFrame.getFrame().add(purchasePrice);
		bFrame.getFrame().add(submit);
		bFrame.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	}

	/**
	 * Shows the input box. Clicking the submit button will close it.
	 */
	public void addStock() {
		bFrame.getFrame().setVisible(true);
	}

}
