package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class AppManager extends ManagerModule {
	// Constructor
	AppManager(MainWin win, AppTask app, LogPrinter log) {
		super(app, log);

		// App Manager Panel
		createAppManagerPanel(win);
	}

	// Create App Manager Panel
	void createAppManagerPanel(MainWin win) {
		// Modify Application Info Panel
		JPanel pAppManager = new JPanel();
		pAppManager.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pAppManager);

		// Add "Modify Application Basic Info" Item
		addModifyAppInfoItem(pAppManager);

		// Add "Refresh Token" Item
		addRefreshTokenItem(pAppManager);
	}

	void addModifyAppInfoItem(JPanel pAppManager) {
		// Application Time Config
		JPanel pModifyAppInfo = new JPanel();
		pModifyAppInfo.setLayout(new FlowLayout());
		pModifyAppInfo.setBorder(BorderFactory.createTitledBorder("Modify Application Time Config:"));
		pAppManager.add(pModifyAppInfo);

		// Abnormal Time
		pModifyAppInfo.add(new JLabel("Abnormal Time:"));
		JTextField tfAbnormalTime = new JTextField(MainWin.TEXT_FIELD_LONG);
		pModifyAppInfo.add(tfAbnormalTime);
		// Offline Time
		pModifyAppInfo.add(new JLabel("Offline Time:"));
		JTextField tfOfflineTime = new JTextField(MainWin.TEXT_FIELD_LONG);
		pModifyAppInfo.add(tfOfflineTime);
		// Modify Application Info Button
		JButton bModifyAppInfo = new JButton("Modify Time Config");
		pModifyAppInfo.add(bModifyAppInfo);

		// Button Action
		bModifyAppInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Modify Application Time Config");
				// Get Input Data
				String strAbnormalTime = tfAbnormalTime.getText();
				String strOfflineTime = tfOfflineTime.getText();
				mLogPrinter.printlnAsParam("Abnormal Time: " + strAbnormalTime + ", Offline Time: " + strOfflineTime);
				// Application Operation
				try {
					boolean bRet = mAppTask.modifyAppInfo(strAbnormalTime, strOfflineTime);
					if (bRet) {
						mLogPrinter.printlnAsResult("Modify Application Info Success.");
					} else {
						mLogPrinter.printlnAsError("Modify Application Info Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Modify Application Time Config.");
				}
				// Clear Text Field
				tfAbnormalTime.setText("");
				tfOfflineTime.setText("");
			}
		});
	}

	void addRefreshTokenItem(JPanel pAppManager) {
		// Refresh Token
		JPanel pRefreshToken = new JPanel();
		pRefreshToken.setLayout(new FlowLayout());
		pRefreshToken.setBorder(BorderFactory.createTitledBorder("Token:"));
		pAppManager.add(pRefreshToken);

		// Refresh Token Button
		JButton bRefreshToken = new JButton("Refresh Token");
		pRefreshToken.add(bRefreshToken);

		// Button Action
		bRefreshToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Token");
				// Application Operation
				try {
					boolean bRet = mAppTask.refreshToken();
					if (bRet) {
						mLogPrinter.printlnAsResult("Refresh Token Success.");
					} else {
						mLogPrinter.printlnAsError("Refresh Token Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Token.");
				}
			}
		});
	}
}
