package Demo;

import java.awt.*;
import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

import Utils.JsonUtil;

public class MainWin extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Windows Size
	static final int WIDTH = 1150;
	//static final int HEIGHT = 1080;

	// Log Text Area Size
	static final int LOG_WIDTH = 8;
	static final int LOG_HEIGHT = 100;

	// Text Field Length
	static final int TEXT_FIELD_SHORT = 5;
	static final int TEXT_FIELD_LONG = 10;

	// Config File
	static final String MODULE_CONFIG_FILE = "Module.json";

	// Main Frame
	JFrame mMainFrame;
	// Layout Constraint
	GridBagConstraints mConstraints;
	// Item Counter
	int mItemCounter = 0;

	// Log Printer
	LogPrinter mLogPrinter;
	JTextArea mLogTextArea;

	// Application
	AppTask mMyApp;

	boolean mAppManagerEnable = false;
	boolean mDeviceManagerEnable = false;
	boolean mDataManagerEnable = false;
	boolean mCommandManagerForNBEnable = false;
	boolean mCommandManagerForAgentEnable = false;
	boolean mRuleManagerEnable = false;
	boolean mSubscribeManagerEnable = false;
	int mHeight;

	// Add Component to Main Frame
	public void addComp(Component component) {
		mConstraints.gridx = 0;
		mConstraints.gridy = mItemCounter;
		mConstraints.gridwidth = 1;
		mConstraints.gridheight = 1;
		add(component, mConstraints);
		++mItemCounter;
	}

	// Create Application
	public AppTask createApp(String strIP, String strPort, String strAppId, String strPassword) throws Exception {
		// Create App Task
		mMyApp = new AppTask(strIP, strPort, strAppId, strPassword);
		return mMyApp;
	}

	// Set Main Frame Visible
	public void setVisible(boolean bVisible) {
		mMainFrame.setVisible(bVisible);
	}

	// Constructor
	public boolean init() {
		// Parse Config From Json File
		boolean bRet = parseConfig();
		if (bRet == false) {
			return false;
		}

		// Main Frame
		mMainFrame = new JFrame("Demo");
		mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mMainFrame.setResizable(false);

		// Set Size and Location
		mMainFrame.setSize(WIDTH, mHeight);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		mMainFrame.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - mHeight) / 2);

		// Layout
		setLayout(new GridBagLayout());
		mMainFrame.add(this, BorderLayout.WEST);
		mConstraints = new GridBagConstraints();
		mConstraints.fill = GridBagConstraints.NONE;
		mConstraints.anchor = GridBagConstraints.WEST;

		// Text Area
		JTextArea taLog = new JTextArea(LOG_WIDTH, LOG_HEIGHT);
		taLog.setLineWrap(true);
		taLog.setWrapStyleWord(true);
		// Log Printer
		mLogPrinter = new LogPrinter(taLog);
		mLogPrinter.printlnAsTitle("Log Content");
		mMyApp.setLogPrinter(mLogPrinter);
		// Log Panel
		JPanel pLog = new JPanel();
		pLog.setLayout(new FlowLayout());
		pLog.setBorder(BorderFactory.createTitledBorder("Debug:"));
		// Scroll Pane
		pLog.add(new JScrollPane(taLog));
		// Add to Main Win
		addComp(pLog);

		if (mAppManagerEnable) {
			// Application Manager
			AppManager objAppManager = new AppManager(this, mMyApp, mLogPrinter);
		}

		if (mDeviceManagerEnable) {
			// Device Manager
			DeviceManager objDeviceManager = new DeviceManager(this, mMyApp, mLogPrinter);
		}

		if (mDataManagerEnable) {
			// Data Manager
			DataManager objDataManager = new DataManager(this, mMyApp, mLogPrinter);
		}

		if (mCommandManagerForNBEnable) {
			// Command Manager
			CommandManager objCmdManager = new CommandManager(this, mMyApp, mLogPrinter);
		}

		if (mCommandManagerForAgentEnable) {
			// Command Manager For Agent
			CommandManagerForAgent objCmdManagerForAgent = new CommandManagerForAgent(this, mMyApp, mLogPrinter);
		}

		if (mRuleManagerEnable) {
			// Rule Manager
			RuleManager objRuleManager = new RuleManager(this, mMyApp, mLogPrinter);
		}

		if (mSubscribeManagerEnable) {
			// Subscribe Manager
			SubscribeManager objSubcribeManager = new SubscribeManager(this, mMyApp, mLogPrinter);
		}

		return true;
	}

	boolean parseConfig() {
		// Read Content From Json File
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(MODULE_CONFIG_FILE)));
			String strTotal = new String();
			String strCurLine = null;
			while((strCurLine = reader.readLine()) != null) {
				strTotal += strCurLine;
			}
			reader.close();
			return getConfig(strTotal);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean getConfig(String strConfig) {
		Map<String, String> mConfig = new HashMap<String, String>();
		mConfig = JsonUtil.jsonString2SimpleObj(strConfig, mConfig.getClass());

		mHeight = 250;

		String strAppManager = mConfig.get("AppManager");
		if (strAppManager != null && strAppManager.equalsIgnoreCase("ENABLE")) {
			mAppManagerEnable = true;
			mHeight += 70;
		}

		String strDeviceManager = mConfig.get("DeviceManager");
		if (strDeviceManager != null && strDeviceManager.equalsIgnoreCase("ENABLE")) {
			mDeviceManagerEnable = true;
			mHeight += 280;
		}

		String strDataManager = mConfig.get("DataManager");
		if (strDataManager != null && strDataManager.equalsIgnoreCase("ENABLE")) {
			mDataManagerEnable = true;
			mHeight += 70;
		}

		String strCommandManagerForNB = mConfig.get("CommandManagerForNB");
		if (strCommandManagerForNB != null && strCommandManagerForNB.equalsIgnoreCase("ENABLE")) {
			mCommandManagerForNBEnable = true;
			mHeight += 140;
		}

		String strCommandManagerForAgent = mConfig.get("CommandManagerForAgent");
		if (strCommandManagerForAgent != null && strCommandManagerForAgent.equalsIgnoreCase("ENABLE")) {
			mCommandManagerForAgentEnable = true;
			mHeight += 70;
		}

		String strRuleManager = mConfig.get("RuleManager");
		if (strRuleManager != null && strRuleManager.equalsIgnoreCase("ENABLE")) {
			mRuleManagerEnable = true;
			mHeight += 210;
		}

		String strSubscribeManager = mConfig.get("SubscribeManager");
		if (strSubscribeManager != null && strSubscribeManager.equalsIgnoreCase("ENABLE")) {
			mSubscribeManagerEnable = true;
			mHeight += 70;
		}

		return true;
	}
}
