package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SubscribeManager extends ManagerModule {
	// Constructor
	SubscribeManager(MainWin win, AppTask app, LogPrinter log) {
		super(app, log);

		// Subscribe Panel
		createSubscribePanel(win);
	}

	void createSubscribePanel(MainWin win) {
		// Subscribe Panel
		JPanel pSubscribe = new JPanel();
		pSubscribe.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pSubscribe);

		// Add "Subscribe Notification" Item
		addSubscribeItem(pSubscribe);
	}

	void addSubscribeItem(JPanel pCurPanel) {
		// Subscribe
		JPanel pSubscribe = new JPanel();
		pSubscribe.setLayout(new FlowLayout());
		pSubscribe.setBorder(BorderFactory.createTitledBorder("Subscribe Notification:"));
		pCurPanel.add(pSubscribe);

		// Notify Type
		pSubscribe.add(new JLabel("Notify Type:"));
		String[] aNotifyType = {"bindDevice", "deviceAdded", "deviceInfoChanged", "deviceDataChanged", "deviceDeleted",
									"deviceEvent", "messageConfirm", "commandRsp", "serviceInfoChanged", "ruleEvent"};
		JComboBox<String> boxSubscribe = new JComboBox<String>(aNotifyType);
		pSubscribe.add(boxSubscribe);
		// Callback URL
		pSubscribe.add(new JLabel("Callback URL:"));
		JTextField tfCallbackUrl = new JTextField(MainWin.TEXT_FIELD_LONG * 2);
		pSubscribe.add(tfCallbackUrl);
		// Subscribe Button
		JButton subscribeButton = new JButton("Subscribe");
		pSubscribe.add(subscribeButton);

		// Button Action
		subscribeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Subscribe");
				// Get Input Data
				String strCallbackUrl = tfCallbackUrl.getText();
				String strNotifyType = boxSubscribe.getSelectedItem().toString();
				mLogPrinter.printlnAsParam("Notify Type:" + strNotifyType + ", Callback Url: " + strCallbackUrl);
				// Application Operation
				try {
					boolean bRet = mAppTask.subscribe(strNotifyType, strCallbackUrl);
					if (bRet) {
						mLogPrinter.printlnAsResult("Subscribe Success.");
					} else {
						mLogPrinter.printlnAsError("Subscribe Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Subscribe.");
				}
				tfCallbackUrl.setText("");
			}
		});
	}
}
