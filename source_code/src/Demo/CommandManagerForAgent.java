package Demo;

import java.awt.*;
import javax.swing.*;

import Demo.AppTask.CommandInfo;
import Demo.AppTask.ServiceInfo;

import java.awt.event.*;
import java.util.Vector;

public class CommandManagerForAgent extends ManagerModule {
	// Constructor
	CommandManagerForAgent(MainWin win, AppTask app, LogPrinter log) {
		super(app, log, win);

		// Send Command Panel
		createSendCmdPanel(win);
	}

	void createSendCmdPanel(MainWin win) {
		// Send Command
		JPanel pSendCmd = new JPanel();
		pSendCmd.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pSendCmd);

		// Add "Send Command" Item
		addSendCmdItem(pSendCmd);
	}

	void addSendCmdItem(JPanel pCurPanel) {
		// Send Command
		JPanel pSendCmd = new JPanel();
		pSendCmd.setLayout(new FlowLayout());
		pSendCmd.setBorder(BorderFactory.createTitledBorder("Send Command:"));
		pCurPanel.add(pSendCmd);

		// Device ID
		pSendCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pSendCmd.add(tfDeviceId);
		// Get Command
		JButton bGetCommand = new JButton("Get Command");
		pSendCmd.add(bGetCommand);

		// Button Action
		bGetCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Get Device Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID:" + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getCommandList(strDeviceId);
					// Update Send Command Panel
					updateSendCmdItem(pSendCmd, strDeviceId, vServiceList);
				} catch (Exception e) {
					e.printStackTrace();
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Get Device Command.");
				}
			}
		});
	}

	void updateSendCmdItem(JPanel pSendCmd, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		if (strDeviceId == null || vServiceList == null || vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("No Command For This Device");
			return;
		}

		// Update Main Frame
		mWin.setVisible(false);
		pSendCmd.removeAll();

		// Device ID
		pSendCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		tfDeviceId.setText(strDeviceId);
		pSendCmd.add(tfDeviceId);
		// Service ID
		pSendCmd.add(new JLabel("Service ID:"));
		JComboBox<String> boxServiceId = new JComboBox<String>();
		pSendCmd.add(boxServiceId);
		for (int i = 0; i < vServiceList.size(); ++i) {
			boxServiceId.addItem(vServiceList.elementAt(i).mStrServiceId);
		}
		// Method
		pSendCmd.add(new JLabel("Method:"));
		JComboBox<String> boxMethod = new JComboBox<String>();
		pSendCmd.add(boxMethod);
		String strServiceId = boxServiceId.getSelectedItem().toString();
		if (strServiceId != null) {
			for (int i = 0; i < vServiceList.size(); ++i) {
				if (strServiceId.equalsIgnoreCase(vServiceList.elementAt(i).mStrServiceId)) {
					Vector<CommandInfo> vCommandType = vServiceList.elementAt(i).mVecCommand;
					for (int j = 0; j < vCommandType.size(); ++j) {
						boxMethod.addItem(vCommandType.elementAt(j).mStrCommandName);
					}
				}
			}
		}
		// Mode
		pSendCmd.add(new JLabel("Mode:"));
		JTextField tfMode = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pSendCmd.add(tfMode);
		// From
		pSendCmd.add(new JLabel("From:"));
		JTextField tfFrom = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pSendCmd.add(tfFrom);
		// Key
		pSendCmd.add(new JLabel("Key:"));
		JTextField tfKey = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pSendCmd.add(tfKey);
		// Value
		pSendCmd.add(new JLabel(" = "));
		JTextField tfValue = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pSendCmd.add(tfValue);
		// Send Command Button
		JButton bSendCommand = new JButton("Send Command");
		pSendCmd.add(bSendCommand);

		// Service Box Action
		boxServiceId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boxMethod.removeAllItems();
				// Add Service Box
				String strServiceId = boxServiceId.getSelectedItem().toString();
				if (strServiceId == null) {
					return;
				}
				for (int i = 0; i < vServiceList.size(); ++i) {
					if (strServiceId.equalsIgnoreCase(vServiceList.elementAt(i).mStrServiceId) == false) {
						continue;
					}
					Vector<CommandInfo> vCommandInfo = vServiceList.elementAt(i).mVecCommand;
					for (int j = 0; j < vCommandInfo.size(); ++j) {
						boxMethod.addItem(vCommandInfo.elementAt(j).mStrCommandName);
					}
				}
			}
		});

		// Button Action
		bSendCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Send Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strServiceId = boxServiceId.getSelectedItem().toString();
				String strMode = tfMode.getText();
				String strFrom = tfFrom.getText();
				String strMethod = boxMethod.getSelectedItem().toString();
				String strKey = tfKey.getText();
				String strValue = tfValue.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Service ID: " + strServiceId
												+ ", Mode: " + strMode + ", From: " + strFrom
												+ ", Method: " + strMethod + ", Key: " + strKey
												+ ", Value: " + strValue);
				// Application Operation
				try {
					boolean bRet = mAppTask.sendCommand(strDeviceId, strServiceId, strMode, strFrom, strMethod, strKey, strValue);
					if (bRet) {
						mLogPrinter.printlnAsResult("Send Command Success.");
					} else {
						mLogPrinter.printlnAsError("Send Command Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Send Command.");
				}
			}
		});

		// Update Main Frame
		mWin.setVisible(true);
	}
}
