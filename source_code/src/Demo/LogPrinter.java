package Demo;

import javax.swing.*;

import Demo.AppTask.CommandDetail;
import Demo.AppTask.CurrentData;
import Demo.AppTask.DeviceBasicInfo;
import Demo.AppTask.HistoryData;
import Demo.AppTask.RuleInfo;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Vector;

// Log Printer
public class LogPrinter {
	static String EXCEPTION_TRACE_FILE = "ExceptionTrace.txt";
	// Log Text Area
	JTextArea mLogArea;

	// Exception stream
	PrintWriter mWriter;

	// Constructor
	LogPrinter(JTextArea logArea) {
		mLogArea = logArea;

		// Create Exception Trace File
		try {
			mWriter = new PrintWriter(new FileWriter(EXCEPTION_TRACE_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mWriter.println("Log Begin:");
		mWriter.flush();
	}

	private void println(String strContent) {
		mLogArea.append(strContent + "\r\n");
		mLogArea.setCaretPosition(mLogArea.getText().length());
	}

	void printlnAsTitle(String strTitle) {
		println("----------" + strTitle + "----------");
	}

	void printlnAsParam(String strParam) {
		println("[PARAM]: " + strParam);
	}

	void printlnAsResult(String strResult) {
		println("[RESULT]: " + strResult);
	}

	void printlnAsError(String strErrorMsg) {
		println("[ERROR]: " + strErrorMsg);
	}

	void printlnAsData(String strData) {
		println(" -- " + strData);
	}

	void printExceptionTrace(Exception e) {
		e.printStackTrace(mWriter);
		mWriter.flush();
	}

	// Print Device List
	void printDevicesList(Vector<String> vList) {
		// Check Param
		if (vList == null || vList.size() == 0) {
			printlnAsError("No Device as Needed.");
			return;
		}

		// Print List
		for (int i = 0; i < vList.size(); ++i) {
			printlnAsData("[ " + i + " ]: " + "Device ID: " + vList.elementAt(i));
		}
	}

	// Print Device Basic Info
	void printDeviceBasicInfo(DeviceBasicInfo info) {
		// Check Param
		if (info == null) {
			printlnAsError("Device Basic Info is NULL.");
			return;
		}

		// Print
		printlnAsData("Device ID: " + info.mStrDeviceId + ", Gateway ID: " + info.mStrGateway
						+ ", Node Type: " + info.mStrNodeType + ", Status: " + info.mStrStatus
						+ ", Node ID: " + info.mStrNodeId + ", Device Type: " + info.mStrDeviceType
						+ ", Manufacturer ID: " + info.mStrManufacturerId + ", Manufacturer Name: " + info.mStrManufacturerName
						+ ", Model: " + info.mStrModel + ", Protocol Type: " + info.mStrProtocolType);
	}

	// Print Device Current Data
	void printCurrentData(CurrentData objCurData, Vector<String> vPropertyList) {
		// Check Param
		if (vPropertyList == null || vPropertyList.size() == 0) {
			printlnAsError("No Property For This Device.");
			return;
		}

		// Print
		for (int i = 0; i < vPropertyList.size(); ++i) {
			printCurrentDataWithProperty(objCurData, vPropertyList.elementAt(i));
		}
	}

	// Print Device Current Data with Property
	void printCurrentDataWithProperty(CurrentData objCurData, String strProperty) {
		// Check Param
		if (objCurData == null || objCurData.mMapData == null
				|| objCurData.mMapData.size() == 0
				|| objCurData.mMapData.get(strProperty) == null) {
			printlnAsError("[ " + strProperty + " ]: No Current Data Received Yet.");
			return;
		}

		// Print
		printlnAsData("[ " + strProperty + " ]: " + objCurData.mMapData.get(strProperty)
					+ " ( " + objCurData.mStrDate + " " + objCurData.mStrTime + " )");
	}

	// Print Device History Data
	void printHistoryData(Map<String, Vector<HistoryData>> mHistoryData, Vector<String> vPropertyList) {
		// Check Param
		if (vPropertyList == null || vPropertyList.size() == 0) {
			printlnAsError("No Property For This Device.");
			return;
		}

		// Print
		for (int i = 0; i < vPropertyList.size(); ++i) {
			printHistoryDataWithProperty(mHistoryData, vPropertyList.elementAt(i));
		}
	}

	// Print Device History Data with Property
	void printHistoryDataWithProperty(Map<String, Vector<HistoryData>> mHistoryData, String strProperty) {
		// Check Param
		if (mHistoryData == null || mHistoryData.size() == 0) {
			printlnAsError("[ " + strProperty + " ]: No History Data For This Device.");
			return;
		}
		// Get History Data List
		Vector<HistoryData> vDataList = mHistoryData.get(strProperty);
		if (vDataList == null || vDataList.size() == 0) {
			printlnAsError("[ " + strProperty + " ]: No History Data.");
			return;
		}

		// Print
		String strTotalContent = "[ " + strProperty + " ]: History Data (Count = " + vDataList.size()  + ")\r\n";
		String strDateOfCurData = new String();
		for (int i = 0; i < vDataList.size(); ++i) {
			// Get Current Piece of Data
			HistoryData objCurData = vDataList.elementAt(i);
			String strCurData = objCurData.mObjValue.toString() + " (" + objCurData.mStrTime + ")";

			if (i == 0) {
				strDateOfCurData = objCurData.mStrDate;
				strTotalContent += strDateOfCurData + "\r\n" + strCurData;
			} else if (strDateOfCurData.equalsIgnoreCase(objCurData.mStrDate)) {
				strTotalContent += "; " + strCurData;
			} else {
				strDateOfCurData = objCurData.mStrDate;
				strTotalContent += "\r\n" + strDateOfCurData + "\r\n" + strCurData;
			}
		}
		printlnAsData(strTotalContent);
	}

	// Print Command in Database
	void printAsyncCmdList(Vector<CommandDetail> vCmdList) {
		// Check Param
		if (vCmdList == null || vCmdList.size() == 0) {
			printlnAsError("No Async Command in Database.");
			return;
		}

		for (int i = 0; i < vCmdList.size(); ++i) {
			CommandDetail curCmd = vCmdList.elementAt(i);
			printlnAsData("[ " + curCmd.mStrCommandId + " ]: " + "Status: " + curCmd.mStrResultCode
							+ ", Service ID: " + curCmd.mStrServiceId + ", Method: " + curCmd.mStrMethod
							+ ", Expire Time: " + curCmd.mNTimeout + ", Paras: " + curCmd.mStrParas);
		}
	}

	// Print Rule List
	void printRuleList(Vector<RuleInfo> vRuleList) {
		// Check Param
		if (vRuleList == null || vRuleList.size() == 0) {
			printlnAsError("No Rule For Device.");
			return;
		}

		// Print
		for (int i = 0; i < vRuleList.size(); ++i) {
			RuleInfo info = vRuleList.elementAt(i);
			printlnAsData("[ " + info.mStrRuleId + " ]: " + "Rule Name: " + info.mStrRuleName
							+ ", Author: " + info.mStrAuthor + ", Status: " + info.mStrStatus);
		}
	}
}
