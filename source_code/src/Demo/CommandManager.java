package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import Demo.AppTask.CommandDetail;
import Demo.AppTask.CommandInfo;
import Demo.AppTask.ServiceInfo;

import java.util.Vector;

public class CommandManager extends ManagerModule {
	// Constructor
	CommandManager(MainWin win, AppTask app, LogPrinter log) {
		super(app, log, win);

		// Post Async Command Panel
		createPostAsyncCmdPanel(win);

		// Query & Delete Async Command Panel
		createManagerAsyncCmdPanel(win);
	}

	void createPostAsyncCmdPanel(MainWin win) {
		// Post Async Command
		JPanel pPostAsyncCmd = new JPanel();
		pPostAsyncCmd.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pPostAsyncCmd);

		// Add "Post Async Command" Item
		addPostAsyncCmdItem(pPostAsyncCmd);
	}

	void createManagerAsyncCmdPanel(MainWin win) {
		// Query & Delete Async Command
		JPanel pQueryAndDeleteAsyncCmd = new JPanel();
		pQueryAndDeleteAsyncCmd.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pQueryAndDeleteAsyncCmd);

		// Add "Query Async Command" Item
		addQueryAsyncCmdItem(pQueryAndDeleteAsyncCmd);
		// Add "Delete Async Command" Item
		addDeleteAsyncCmdItem(pQueryAndDeleteAsyncCmd);
	}

	void addPostAsyncCmdItem(JPanel pCurPanel) {
		// Post Async Command
		JPanel pPostAsyncCmd = new JPanel();
		pPostAsyncCmd.setLayout(new FlowLayout());
		pPostAsyncCmd.setBorder(BorderFactory.createTitledBorder("Post Async Command:"));
		pCurPanel.add(pPostAsyncCmd);

		// Device ID
		pPostAsyncCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pPostAsyncCmd.add(tfDeviceId);
		// Get Command Button
		JButton bGetCommand = new JButton("Get Command");
		pPostAsyncCmd.add(bGetCommand);

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
					// Update Async Command Panel
					updatePostAsyncCmdItem(pPostAsyncCmd, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Get Device Command.");
				}
			}
		});
	}

	void addQueryAsyncCmdItem(JPanel pCurPanel) {
		// Query Async Command
		JPanel pQueryAsyncCmd = new JPanel();
		pQueryAsyncCmd.setLayout(new FlowLayout());
		pQueryAsyncCmd.setBorder(BorderFactory.createTitledBorder("Query Async Command:"));
		pCurPanel.add(pQueryAsyncCmd);

		// Device ID
		pQueryAsyncCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pQueryAsyncCmd.add(tfDeviceId);
		// Query Button
		JButton bQueryAsyncCmd = new JButton("Query Command");
		pQueryAsyncCmd.add(bQueryAsyncCmd);

		// Button Action
		bQueryAsyncCmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Async Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					Vector<CommandDetail> vCmdList = mAppTask.queryAsyncCommand(strDeviceId);
					// Print Async Command List
					mLogPrinter.printAsyncCmdList(vCmdList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Async Command.");
				}
			}
		});
	}

	void addDeleteAsyncCmdItem(JPanel pCurPanel) {
		// Delete Async Command
		JPanel pDeleteAsyncCmd = new JPanel();
		pDeleteAsyncCmd.setLayout(new FlowLayout());
		pDeleteAsyncCmd.setBorder(BorderFactory.createTitledBorder("Delete Async Command:"));
		pCurPanel.add(pDeleteAsyncCmd);

		// Device ID
		pDeleteAsyncCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pDeleteAsyncCmd.add(tfDeviceId);
		// Delete Button
		JButton bDeleteAsyncCmd = new JButton("Delete Command");
		pDeleteAsyncCmd.add(bDeleteAsyncCmd);

		// Button Action
		bDeleteAsyncCmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Delete Async Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					boolean bRet = mAppTask.deleteAsyncCommand(strDeviceId);
					if (bRet) {
						mLogPrinter.printlnAsResult("Delete Async Command Success.");
					} else {
						mLogPrinter.printlnAsError("Delete Async Command Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Delete Async Command.");
				}
				// Clear Text Field
				tfDeviceId.setText("");
			}
		});
	}

	// Update "Post Async Command" Item After Get Device Command
	void updatePostAsyncCmdItem(JPanel pPostAsyncCmd, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		// Check Param
		if (vServiceList == null || vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("Get Device Command Failed.");
			return;
		}

		if (vServiceList.size() == 1) {
			String strServiceId = vServiceList.elementAt(0).mStrServiceId.toString();
			String strServiceType = vServiceList.elementAt(0).mStrServiceType.toString();
			if (strServiceId.equals("RawData") || strServiceType.equals("RawData")) {
				// Update While Service is RawData
				updatePostAsyncCmdItemWithRawData(pPostAsyncCmd, strDeviceId);
				return;
			}
		}

		// Update in Normal Case
		updatePostAsyncCmdItemNormal(pPostAsyncCmd, strDeviceId, vServiceList);
	}

	void updatePostAsyncCmdItemWithRawData(JPanel pPostAsyncCmd, String strDeviceId) {
		// Update Main Frame
		mWin.setVisible(false);
		pPostAsyncCmd.removeAll();

		// Device ID
		pPostAsyncCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pPostAsyncCmd.add(tfDeviceId);
		tfDeviceId.setText(strDeviceId);
		// Service ID Box
		pPostAsyncCmd.add(new JLabel("Service:"));
		JComboBox<String> boxServiceId = new JComboBox<String>();
		boxServiceId.addItem("RawData");
		pPostAsyncCmd.add(boxServiceId);
		// Data
		pPostAsyncCmd.add(new JLabel("Data:"));
		JTextField tfData = new JTextField(MainWin.TEXT_FIELD_LONG);
		pPostAsyncCmd.add(tfData);
		// Expire Time
		pPostAsyncCmd.add(new JLabel("Expire Time:"));
		JTextField tfExpireTime = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pPostAsyncCmd.add(tfExpireTime);
		// Post Command Button
		JButton bPostAsyncCmd = new JButton("Post Command");
		pPostAsyncCmd.add(bPostAsyncCmd);
		// Refresh Command Button
		JButton bRefreshCommand = new JButton("Refresh");
		pPostAsyncCmd.add(bRefreshCommand);

		// Button Action
		bPostAsyncCmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Post Async Command with RawData");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strData = tfData.getText();
				String strExpireTime = tfExpireTime.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Data: " + strData + ", Expire Time: " + strExpireTime);
				// Application Operation
				try {
					boolean bRet = mAppTask.postAsyncCommandWithRawData(strDeviceId, strData, strExpireTime);
					if (bRet) {
						mLogPrinter.printlnAsResult("Post Async Command with RawData Success.");
					} else {
						mLogPrinter.printlnAsError("Post Async Command with RawData Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Post Async Command with RawData.");
				}
				// Clear Text Field
				tfDeviceId.setText("");
				tfData.setText("");
				tfExpireTime.setText("");
			}
		});

		bRefreshCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Device Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID:" + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getCommandList(strDeviceId);
					// Update Async Command Panel
					updatePostAsyncCmdItem(pPostAsyncCmd, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Device Command.");
				}
			}
		});

		// Update Main Frame
		mWin.setVisible(true);
	}

	void updatePostAsyncCmdItemNormal(JPanel pPostAsyncCmd, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		// Update Main Frame
		mWin.setVisible(false);
		pPostAsyncCmd.removeAll();

		// Device ID
		pPostAsyncCmd.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pPostAsyncCmd.add(tfDeviceId);
		tfDeviceId.setText(strDeviceId);
		// Service Id Box
		pPostAsyncCmd.add(new JLabel("Service:"));
		JComboBox<String> boxServiceId = new JComboBox<String>();
		pPostAsyncCmd.add(boxServiceId);
		for (int i = 0; i < vServiceList.size(); ++i) {
			boxServiceId.addItem(vServiceList.elementAt(i).mStrServiceId);
		}
		// Command Box
		pPostAsyncCmd.add(new JLabel("Command:"));
		JComboBox<String> boxCommand = new JComboBox<String>();
		pPostAsyncCmd.add(boxCommand);
		String strServiceId = boxServiceId.getSelectedItem().toString();
		Vector<CommandInfo> vCmdList = null;
		if (strServiceId != null) {
			for (int i = 0; i < vServiceList.size(); ++i) {
				if (strServiceId.equalsIgnoreCase(vServiceList.elementAt(i).mStrServiceId)) {
					Vector<CommandInfo> vCommandType = vServiceList.elementAt(i).mVecCommand;
					for (int j = 0; j < vCommandType.size(); ++j) {
						boxCommand.addItem(vCommandType.elementAt(j).mStrCommandName);
					}
					vCmdList = vServiceList.elementAt(i).mVecCommand;
				}
			}
		}
		// Item Box
		JComboBox<String> boxItem = new JComboBox<String>();
		pPostAsyncCmd.add(boxItem);
		String strCommand = boxCommand.getSelectedItem().toString();
		if (strCommand != null && vCmdList != null) {
			for (int i = 0; i < vCmdList.size(); ++i) {
				if (strCommand.equalsIgnoreCase(vCmdList.elementAt(i).mStrCommandName)) {
					Vector<String> vParaList = vCmdList.elementAt(i).mVecItem;
					for (int j = 0; j < vParaList.size(); ++j) {
						boxItem.addItem(vParaList.elementAt(j));
					}
				}
			}
		}
		// Value
		pPostAsyncCmd.add(new JLabel("Value:"));
		JTextField tfValue = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pPostAsyncCmd.add(tfValue);
		// Expire Time
		pPostAsyncCmd.add(new JLabel("Expire Time:"));
		JTextField tfExpireTime = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pPostAsyncCmd.add(tfExpireTime);
		// Post Async Command Button
		JButton bPostAsyncCmd = new JButton("Post Command");
		pPostAsyncCmd.add(bPostAsyncCmd);
		// Refresh Command Button
		JButton bRefreshCommand = new JButton("Refresh");
		pPostAsyncCmd.add(bRefreshCommand);

		// Service Box Action
		boxServiceId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boxCommand.removeAllItems();
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
						boxCommand.addItem(vCommandInfo.elementAt(j).mStrCommandName);
					}
				}
			}
		});

		// Command Box Action
		boxCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boxItem.removeAllItems();
				// Add Item
				Object objCurServiceId = boxServiceId.getSelectedItem();
				Object objCurCommandType = boxCommand.getSelectedItem();
				if (objCurServiceId == null || objCurCommandType == null) {
					return;
				}
				for (int i = 0; i < vServiceList.size(); ++i) {
					if (vServiceList.elementAt(i).mStrServiceId.equalsIgnoreCase(objCurServiceId.toString()) == false) {
						continue;
					}
					Vector<CommandInfo> vCommandType = vServiceList.elementAt(i).mVecCommand;
					for (int j = 0; j < vCommandType.size(); ++j) {
						if (vCommandType.elementAt(j).mStrCommandName.equalsIgnoreCase(objCurCommandType.toString()) == false) {
							continue;
						}
						Vector<String> vParaList = vCommandType.elementAt(j).mVecItem;
						for (int k = 0; k < vParaList.size(); ++k) {
							boxItem.addItem(vParaList.elementAt(k));
						}
					}
				}
			}
		});

		// Button Action
		bPostAsyncCmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Post Async Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strServiceId = boxServiceId.getSelectedItem().toString();
				String strCommandId = boxCommand.getSelectedItem().toString();
				String strItem = boxItem.getSelectedItem().toString();
				String strValue = tfValue.getText();
				String strExpireTime = tfExpireTime.getText();
				if (strServiceId == null) {
					mLogPrinter.printlnAsError("Can't find this service.");
				}
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Service ID: " + boxServiceId.getSelectedItem().toString()
											+ ", Command ID: " + strCommandId + ", Item: " + strItem
											+ ", Value: " + strValue + ", Expire Time: " + strExpireTime);
				// Application Operation
				try {
					boolean bRet = mAppTask.postAsyncCommand(strDeviceId, strServiceId, strCommandId, strItem, strValue, strExpireTime);
					if (bRet) {
						mLogPrinter.printlnAsResult("Post Async Command Success.");
					} else {
						mLogPrinter.printlnAsError("Post Async Command Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Post Command.");
				}
				// Clear Text Field
				tfDeviceId.setText("");
				tfValue.setText("");
				tfExpireTime.setText("");
			}
		});

		bRefreshCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Device Command");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID:" + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getCommandList(strDeviceId);
					// Update Async Command Panel
					updatePostAsyncCmdItem(pPostAsyncCmd, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Device Command.");
				}
			}
		});

		// Update Main Frame
		mWin.setVisible(true);
	}
}
