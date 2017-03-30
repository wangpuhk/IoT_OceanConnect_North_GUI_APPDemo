package Demo;

import java.awt.*;
import javax.swing.*;

import Demo.AppTask.RuleInfo;
import Demo.AppTask.ServiceInfo;

import java.awt.event.*;
import java.util.Vector;

public class RuleManager extends ManagerModule {
	// Constructor
	RuleManager(MainWin win, AppTask app, LogPrinter log) {
		super(app, log, win);
	
		// Create And Update Rule Panel
		createCreateAndUpdateRulePanel(win);

		// Query And Delete Rule Panel
		createQueryAndModifyAndDeleteRulePanel(win);
	}

	void createCreateAndUpdateRulePanel(MainWin win) {
		// Create And Update Rule Panel
		JPanel pCreateAndUpdateRule = new JPanel();
		pCreateAndUpdateRule.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pCreateAndUpdateRule);

		// Add "Create And Update Rule" Item
		addCreateAndUpdateRuleItem(pCreateAndUpdateRule);
	}

	void createQueryAndModifyAndDeleteRulePanel(MainWin win) {
		// Post Async Command
		JPanel pPostAsyncCmd = new JPanel();
		pPostAsyncCmd.setLayout(new FlowLayout());
		// Add to Main Win
		win.addComp(pPostAsyncCmd);

		// Add "Query Rule" Item
		addQueryRuleItem(pPostAsyncCmd);
		// Add "Modify Rule" Item
		addModifyRuleItem(pPostAsyncCmd);
		// Add "Delete Rule" Item
		addDeleteRuleItem(pPostAsyncCmd);
	}

	void addCreateAndUpdateRuleItem(JPanel pCurPanel) {
		// Create And Update Rule
		JPanel pCreateAndUpdateRule = new JPanel();
		pCreateAndUpdateRule.setLayout(new FlowLayout());
		pCreateAndUpdateRule.setBorder(BorderFactory.createTitledBorder("Create And Update Rule:"));
		pCurPanel.add(pCreateAndUpdateRule);

		// Device ID
		pCreateAndUpdateRule.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pCreateAndUpdateRule.add(tfDeviceId);
		// Get Service Button
		JButton bGetService = new JButton("Get Service");
		pCreateAndUpdateRule.add(bGetService);

		// Button Action
		bGetService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Get Service For Rule");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				// Application Operation
				mLogPrinter.printlnAsResult("Device ID: " + strDeviceId);
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getServiceList(strDeviceId);
					// Update Create And Update Rule Panel
					updateCreateAndUpdateRuleItem(pCreateAndUpdateRule, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Get Service For Rule.");
				}
			}
		});
	}

	void addQueryRuleItem(JPanel pCurPanel) {
		// Query Rule
		JPanel pQueryRule = new JPanel();
		pQueryRule.setLayout(new FlowLayout());
		pQueryRule.setBorder(BorderFactory.createTitledBorder("Query Rule:"));
		pCurPanel.add(pQueryRule);

		// Author
		pQueryRule.add(new JLabel("Author:"));
		JTextField tfAuthor = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pQueryRule.add(tfAuthor);
		// Rule Name
		pQueryRule.add(new JLabel("Name(O):"));
		JTextField tfName = new JTextField(MainWin.TEXT_FIELD_LONG);
		pQueryRule.add(tfName);
		// Query Rule Button
		JButton bQueryRule = new JButton("Query");
		pQueryRule.add(bQueryRule);

		// Button Action
		bQueryRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Rule");
				// Get Input Data
				String strAuthor = tfAuthor.getText();
				String strRuleName = tfName.getText();
				mLogPrinter.printlnAsResult("Author: " + strAuthor + ", Rule Name: " + strRuleName);
				// Application Operation
				try {
					Vector<RuleInfo> vRuleList = mAppTask.queryRule(strAuthor, strRuleName);
					// Print Rule List
					mLogPrinter.printRuleList(vRuleList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Rule.");
				}
				// Clear Text Field
				tfAuthor.setText("");
				tfName.setText("");
			}
		});
	}

	void addModifyRuleItem(JPanel pCurPanel) {
		// Modify Rule
		JPanel pModifyRule = new JPanel();
		pModifyRule.setLayout(new FlowLayout());
		pModifyRule.setBorder(BorderFactory.createTitledBorder("Modify Rule:"));
		pCurPanel.add(pModifyRule);

		// Rule ID
		pModifyRule.add(new JLabel("Rule ID:"));
		JTextField tfRuleID = new JTextField(MainWin.TEXT_FIELD_LONG);
		pModifyRule.add(tfRuleID);
		// Status
		JComboBox<String> boxStatus = new JComboBox<String>();
		pModifyRule.add(boxStatus);
		boxStatus.addItem("active");
		boxStatus.addItem("inactive");
		// Modify Rule Status Button
		JButton bModifyRuleStatus = new JButton("Modify");
		pModifyRule.add(bModifyRuleStatus);

		// Button Action
		bModifyRuleStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Modify Rule Status");
				// Get Input Data
				String strRuleId = tfRuleID.getText();
				String strStatus = boxStatus.getSelectedItem().toString();
				mLogPrinter.printlnAsParam("Rule ID: " + strRuleId + ", Status: " + strStatus);
				// Application Operation
				try {
					boolean bRet = mAppTask.modifyRuleStatus(strRuleId, strStatus);
					if (bRet) {
						mLogPrinter.printlnAsResult("Modify Rule Status Success.");
					} else {
						mLogPrinter.printlnAsError("Modify Rule Status Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Modify Rule Status.");
				}
				// Clear Text Field
				tfRuleID.setText("");
			}
		});
	}

	void addDeleteRuleItem(JPanel pCurPanel) {
		// Delete Rule
		JPanel pDeleteRule = new JPanel();
		pDeleteRule.setLayout(new FlowLayout());
		pDeleteRule.setBorder(BorderFactory.createTitledBorder("Delete Rule:"));
		pCurPanel.add(pDeleteRule);

		// Rule ID
		pDeleteRule.add(new JLabel("Rule ID:"));
		JTextField tfRuleID = new JTextField(MainWin.TEXT_FIELD_LONG);
		pDeleteRule.add(tfRuleID);
		// Delete Rule Button
		JButton bDeleteRule = new JButton("Delete");
		pDeleteRule.add(bDeleteRule);

		bDeleteRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Modify Rule Status");
				// Get Input Data
				String strRuleId = tfRuleID.getText();
				mLogPrinter.printlnAsParam("Rule ID: " + strRuleId);
				// Application Operation
				try {
					boolean bRet = mAppTask.deleteRule(strRuleId);
					if (bRet) {
						mLogPrinter.printlnAsResult("Delete Rule Success.");
					} else {
						mLogPrinter.printlnAsError("Delete Rule Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Delete Rule.");
				}
				// Clear Text Field
				tfRuleID.setText("");
			}
		});
	}

	// Update "Create And Update Rule" Item
	void updateCreateAndUpdateRuleItem(JPanel pCreateAndUpdate, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		// Check Param
		if (vServiceList == null || vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("Get Device Service For Rule Failed.");
			return;
		}

		// Check if Service is RawData
		ServiceInfo info = vServiceList.elementAt(0);
		if (info.mStrServiceId.equalsIgnoreCase("RawData") || info.mStrServiceType.equalsIgnoreCase("RawData")) {
			mLogPrinter.printlnAsError("Service is RawData, Ignore Rule Engine.");
			return;
		}

		// Update Main Frame
		mWin.setVisible(false);
		pCreateAndUpdate.removeAll();

		// Too Many Items, Need 2 Layer
		pCreateAndUpdate.setLayout(new GridLayout(2, 1));
		pCreateAndUpdate.setBorder(BorderFactory.createTitledBorder("Create And Update Rule:"));

		// ********** Layer A **********
		JPanel pLayerA = new JPanel();
		FlowLayout flLayerA = new FlowLayout();
		flLayerA.setAlignment(FlowLayout.LEFT);
		pLayerA.setLayout(flLayerA);
		pCreateAndUpdate.add(pLayerA);

		// Device ID
		pLayerA.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		tfDeviceId.setText(strDeviceId);
		pLayerA.add(tfDeviceId);
		// Author
		pLayerA.add(new JLabel("Author:"));
		JTextField tfAuthor = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pLayerA.add(tfAuthor);
		// Rule Name
		pLayerA.add(new JLabel("Rule Name:"));
		JTextField tfRuleName = new JTextField(MainWin.TEXT_FIELD_LONG);
		pLayerA.add(tfRuleName);
		// Service ID
		pLayerA.add(new JLabel("Service:"));
		JComboBox<String> boxServiceId = new JComboBox<String>();
		for (int i = 0; i < vServiceList.size(); ++i) {
			boxServiceId.addItem(vServiceList.elementAt(i).mStrServiceId);
		}
		pLayerA.add(boxServiceId);
		// Property
		pLayerA.add(new JLabel("Property:"));
		JComboBox<String> boxProperty = new JComboBox<String>();
		for (int i = 0; i < vServiceList.size(); ++i) {
			if (vServiceList.elementAt(i).mStrServiceId.equalsIgnoreCase(boxServiceId.getSelectedItem().toString())) {
				for (int j = 0; j < vServiceList.elementAt(i).mVecProperty.size(); ++j) {
					boxProperty.addItem(vServiceList.elementAt(i).mVecProperty.elementAt(j));
				}
			}
		}
		pLayerA.add(boxProperty);
		// Operation
		JComboBox<String> boxOperation = new JComboBox<String>();
		boxOperation.addItem("<");
		boxOperation.addItem("=");
		boxOperation.addItem(">");
		pLayerA.add(boxOperation);
		// Value
		pLayerA.add(new JLabel("Value:"));
		JTextField tfValue = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pLayerA.add(tfValue);

		// Combo Box Action
		boxServiceId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boxProperty.removeAllItems();
				for (int i = 0; i < vServiceList.size(); ++i) {
					if (vServiceList.elementAt(i).mStrServiceId.equalsIgnoreCase(boxServiceId.getSelectedItem().toString())) {
						for (int j = 0; j < vServiceList.elementAt(i).mVecProperty.size(); ++j) {
							boxProperty.addItem(vServiceList.elementAt(i).mVecProperty.elementAt(j));
						}
					}
				}
			}
		});

		// ********** Layer B **********
		JPanel pLayerB = new JPanel();
		FlowLayout flLayerB = new FlowLayout();
		flLayerB.setAlignment(FlowLayout.LEFT);
		pLayerB.setLayout(flLayerB);
		pCreateAndUpdate.add(pLayerB);

		// Action Type
		pLayerB.add(new JLabel("Notification Type:"));
		JComboBox<String> boxAction = new JComboBox<String>();
		boxAction.addItem("SMS");
		boxAction.addItem("EMAIL");
		pLayerB.add(boxAction);
		// Address
		pLayerB.add(new JLabel("Address:"));
		JTextField tfAddress = new JTextField(MainWin.TEXT_FIELD_SHORT);
		pLayerB.add(tfAddress);
		// Tilte
		pLayerB.add(new JLabel("Title:"));
		JTextField tfTitle = new JTextField(MainWin.TEXT_FIELD_LONG);
		pLayerB.add(tfTitle);
		// Content
		pLayerB.add(new JLabel("Content:"));
		JTextField tfContent = new JTextField(MainWin.TEXT_FIELD_LONG);
		pLayerB.add(tfContent);
		// Create Rule Button
		JButton bCreateRule = new JButton("Create Rule");
		pLayerB.add(bCreateRule);
		pLayerB.add(new JLabel("Rule ID:"));
		JTextField tfRuleId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pLayerB.add(tfRuleId);
		// Update Rule Button
		JButton bUpdateRule = new JButton("Update Rule");
		pLayerB.add(bUpdateRule);
		// Refresh Button
		JButton bRefreshService = new JButton("Refresh");
		pLayerB.add(bRefreshService);

		// Button Action
		bCreateRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Create Rule");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strRuleName = tfRuleName.getText();
				String strAuthor = tfAuthor.getText();
				String strServiceId = boxServiceId.getSelectedItem().toString();
				String strProperty = boxProperty.getSelectedItem().toString();
				String strOperation = boxOperation.getSelectedItem().toString();
				String strValue = tfValue.getText();
				String strActionType = boxAction.getSelectedItem().toString();
				String strAddress = tfAddress.getText();
				String strTitle = tfTitle.getText();
				String strContent = tfContent.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Author: " + strAuthor
											+ ", Rule Name: " + strRuleName + ", Service ID: " + strServiceId
											+ ", Property: "+ strProperty + ", Operation: " + strOperation
											+ ", Value: "+ strValue + ", Action Type: " + strActionType
											+ ", Address: " + strAddress + ", Title: " + strTitle
											+ ", Content: " + strContent);
				// Application Operation
				try {
					String strRuleId = mAppTask.createRule(strDeviceId, strRuleName, strAuthor, strServiceId, strProperty,
															strOperation, strValue, strActionType, strAddress, strTitle, strContent);
					if (strRuleId != null) {
						mLogPrinter.printlnAsResult("Create Rule Success, Rule ID is " + strRuleId);
					} else {
						mLogPrinter.printlnAsError("Create Rule Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Create Rule.");
				}
				// Clear Text Field
				tfDeviceId.setText("");
				tfAuthor.setText("");
				tfRuleName.setText("");
				tfValue.setText("");
				tfAddress.setText("");
				tfTitle.setText("");
				tfContent.setText("");
			}
		});

		bUpdateRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Update Rule");
				// Get Input Data
				String strRuleId = tfRuleId.getText();
				String strDeviceId = tfDeviceId.getText();
				String strRuleName = tfRuleName.getText();
				String strAuthor = tfAuthor.getText();
				String strServiceId = boxServiceId.getSelectedItem().toString();
				String strProperty = boxProperty.getSelectedItem().toString();
				String strOperation = boxOperation.getSelectedItem().toString();
				String strValue = tfValue.getText();
				String strActionType = boxAction.getSelectedItem().toString();
				String strAddress = tfAddress.getText();
				String strTitle = tfTitle.getText();
				String strContent = tfContent.getText();
				mLogPrinter.printlnAsParam("Rule ID: " + strRuleId + ", Device ID: " + strDeviceId
											+ ", Author: " + strAuthor + ", Rule Name: " + strRuleName
											+ ", Service ID: " + strServiceId + ", Property: "+ strProperty
											+ ", Operation: " + strOperation + ", Value: "+ strValue
											+ ", Action Type: " + strActionType + ", Address: " + strAddress
											+ ", Title: " + strTitle + ", Content: " + strContent);
				// Application Operation
				try {
					boolean bRet = mAppTask.updateRule(strRuleId, strDeviceId, strRuleName, strAuthor, strServiceId, strProperty,
														strOperation, strValue, strActionType, strAddress, strTitle, strContent);
					if (bRet) {
						mLogPrinter.printlnAsResult("Update Rule Success.");
						
					} else {
						mLogPrinter.printlnAsError("Update Rule Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Update Rule.");
				}
				// Clear Text Field
				tfRuleId.setText("");
				tfDeviceId.setText("");
				tfAuthor.setText("");
				tfRuleName.setText("");
				tfValue.setText("");
				tfAddress.setText("");
				tfTitle.setText("");
				tfContent.setText("");
			}
		});

		bRefreshService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Device Service For Rule");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				// Application Operation
				mLogPrinter.printlnAsResult("Device ID: " + strDeviceId);
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getServiceList(strDeviceId);
					// Update Create And Update Rule Panel
					updateCreateAndUpdateRuleItem(pCreateAndUpdate, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Service For Rule.");
				}
			}
		});

		// Update Main Frame
		mWin.setVisible(true);
	}
}
