package Demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


import com.fasterxml.jackson.databind.node.ObjectNode;

import Utils.JsonUtil;
import Utils.HttpsUtil;

public class AppTask {
	/***** Data Struct *****/
	// Device Basic Info
	public class DeviceBasicInfo {
		String mStrDeviceId;
		String mStrNodeId;
		String mStrManufacturerId;
		String mStrManufacturerName;
		String mStrModel;
		String mStrDeviceType;
		String mStrProtocolType;
		String mStrGateway;
		String mStrNodeType;
		String mStrStatus;

		// Constructor
		DeviceBasicInfo(String strDeviceId, String strNodeId,
							String strManufacturerId, String strManufacturerName,
							String strModel, String strDeviceType,
							String strProtocolType, String strGateway,
							String strNodeType, String strStatus) {
			mStrDeviceId = strDeviceId;
			mStrNodeId = strNodeId;
			mStrManufacturerId = strManufacturerId;
			mStrManufacturerName = strManufacturerName;
			mStrModel = strModel;
			mStrDeviceType = strDeviceType;
			mStrProtocolType = strProtocolType;
			mStrGateway = strGateway;
			mStrNodeType = strNodeType;
			mStrStatus = strStatus;
		}
	}

	// Service with Property
	public class ServiceInfo {
		String mStrServiceId;
		String mStrServiceType;
		Vector<String> mVecProperty;
		Vector<CommandInfo> mVecCommand;

		// Constructor
		ServiceInfo(String strServiceId, String strServiceType, Vector<String> vProperty, Vector<CommandInfo> vCommand) {
			mStrServiceId = strServiceId;
			mStrServiceType = strServiceType;
			mVecProperty = vProperty;
			mVecCommand = vCommand;
		}
	}

	// Command Info
	public class CommandInfo {
		String mStrCommandName;
		Vector<String> mVecItem;

		// Constructor
		CommandInfo(String strCommand, Vector<String> vItem) {
			mStrCommandName = strCommand;
			mVecItem = vItem;
		}
	}

	// Current Device Data with Time Stamp
	public class CurrentData {
		String mStrDate;
		String mStrTime;
		// Report Data With Property
		Map<String, Object> mMapData;

		// Constructor
		CurrentData(String strDataAndTime, Map<String, Object> mData) {
			mStrDate = getDate(strDataAndTime);
			mStrTime = getTime(strDataAndTime);
			mMapData = mData;
		}
	}

	// History Device Data with Time Stamp
	public class HistoryData {
		String mStrDate;
		String mStrTime;
		Object mObjValue;

		// Constructor
		HistoryData(String strDataAndTime, Object objValue) {
			mStrDate = getDate(strDataAndTime);
			mStrTime = getTime(strDataAndTime);
			mObjValue = objValue;
		}
	}

	// Command Detail
	public class CommandDetail {
		String mStrCommandId;
		String mStrServiceId;
		String mStrMethod;
		Integer mNTimeout;
		String mStrParas;
		String mStrResultCode;

		// Constructor
		CommandDetail(String strCommandId, String strServiceId, String strMethod, Integer nTimeout, String strParas, String strResultCode) {
			mStrCommandId = strCommandId;
			mStrServiceId = strServiceId;
			mStrMethod = strMethod;
			mNTimeout = nTimeout;
			mStrParas = strParas;
			mStrResultCode = strResultCode;
		}
	}

	// Rule Info
	public class RuleInfo {
		String mStrRuleId;
		String mStrRuleName;
		String mStrAuthor;
		String mStrStatus;

		// Constructor
		RuleInfo(String strRuleId, String strRuleName, String strAuthor, String strStatus) {
			mStrRuleId = strRuleId;
			mStrRuleName = strRuleName;
			mStrAuthor = strAuthor;
			mStrStatus = strStatus;
		}
	}

	/***** Data Member *****/
	// Basic Param of Connection
	String mStrBaseUrl;
	String mStrAppId;
	String mStrPassword;

	// HTTPS Connection Utils
	HttpsUtil mObjHttpsUtil;

	// Authentication
	HashMap<String, String> mMapHeader;
	String mStrRefreshToken;

	// Log Printer
	LogPrinter mLogPrinter;

	/***** Public Function *****/
	// Constructor
	AppTask(String strIp, String strPort, String strAppId, String strPassword) throws Exception {
		// Set Data Member
		mStrBaseUrl = "https://" + strIp + ":" + strPort + "/";
		mStrAppId = strAppId;
		mStrPassword = strPassword;

		// Create And Init HttpsUtil Object
		mObjHttpsUtil = new HttpsUtil();
		mObjHttpsUtil.initSSLConfigForTwoWay();
	}

	// Set Log Printer
	public void setLogPrinter(LogPrinter printer) {
		mLogPrinter = printer;
	}

	// Authentication
	public boolean authentication() throws Exception {
		// Check Param
		if (!checkParam(mStrBaseUrl) || !checkParam(mStrAppId) || !checkParam(mStrPassword) || mObjHttpsUtil == null) {
			return false;
		}

		// Request URL
		String strUrlLogin = mStrBaseUrl + "iocm/app/sec/v1.1.0/login";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("appId", mStrAppId);
		mParam.put("secret", mStrPassword);
		// Send Request
		String strResult = mObjHttpsUtil.doPostFormUrlEncodedForString(strUrlLogin, mParam);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			return false;
		}

		// Update Data Member
		mMapHeader = new HashMap<String, String>();
		mMapHeader.put("app_key", mStrAppId);
		mMapHeader.put("Authorization", mResult.get("tokenType").toString() + " " + mResult.get("accessToken").toString());
		mStrRefreshToken = mResult.get("refreshToken").toString();
		return true;
	}

	// Refresh Token
	public boolean refreshToken() throws Exception {
		// Check Param
		if (!checkParam(mStrRefreshToken)) {
			return false;
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/sec/v1.1.0/refreshToken";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("appId", mStrAppId);
		mParam.put("secret", mStrPassword);
		mParam.put("refreshToken", mStrRefreshToken);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, strRequest);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			return false;
		}

		// Update Data Member
		mMapHeader = new HashMap<String, String>();
		mMapHeader.put("app_key", mStrAppId);
		mMapHeader.put("Authorization", mResult.get("tokenType").toString() + " " + mResult.get("accessToken").toString());
		mStrRefreshToken = mResult.get("refreshToken").toString();
		return true;
	}

	// Modify Application Info
	public boolean modifyAppInfo(String strAbnormalTime, String strOfflineTime) throws Exception {
		// Check Param
		if (!checkParam(strAbnormalTime) && !checkParam(strOfflineTime)) {
			mLogPrinter.printlnAsError("Modify Application Info, All Param are NULL.");
			return false;
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/am/v1.1.0/applications/" + mStrAppId;
		// Param
		Map<String, Object> mTimeConfig = new HashMap<String, Object>();
		if (checkParam(strAbnormalTime)) {
			mTimeConfig.put("abnormalTime", strAbnormalTime);
		}
		if (checkParam(strOfflineTime)) {
			mTimeConfig.put("offlineTime", strOfflineTime);
		}
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("deviceStatusTimeConfig", mTimeConfig);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPutJsonForString(strUrl, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	// Register Direct Device
	public String registerDirectDevice(String strNodeId, String strVerifyCode, int nTimeout) throws Exception {
		// Check Param
		boolean bValidNodeId = checkParam(strNodeId);
		boolean bValidVerifyCode = checkParam(strVerifyCode);
		if (!bValidNodeId && !bValidVerifyCode) {
			mLogPrinter.printlnAsError("Register Direct Device, All Param are NULL.");
			return null;
		} else if (!bValidNodeId) {
			strNodeId = strVerifyCode;
		} else if (!bValidVerifyCode) {
			strVerifyCode = strNodeId;
		}

		// Request URL
		String strUrlRegister = mStrBaseUrl + "iocm/app/reg/v1.1.0/devices";
		// Param
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("verifyCode", strVerifyCode);
		mParam.put("nodeId", strNodeId);
		mParam.put("timeout", nTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrlRegister, mMapHeader, strRequest);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Return Device ID
		return mResult.get("deviceId").toString();
	}

	// Delete Device
	public boolean deleteDevice(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Delete Device Failed, Device ID is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doDeleteForString(strUrl, mMapHeader);

		// Parse Result
		return parseResultString(strResult);
	}

	// Modify Device Info
	public boolean modifyDeviceInfo(String strDeviceId, String strManufacturerId,
									String strManufacturerName, String strModel,
									String strProtocolType, String strDeviceType) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strManufacturerId) || !checkParam(strManufacturerName)
				|| !checkParam(strModel) || !checkParam(strProtocolType) || !checkParam(strDeviceType)) {
			mLogPrinter.printlnAsError("Modify Device Info, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrlModifyDeviceInfo = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Param
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("manufacturerId", strManufacturerId);
		mParam.put("manufacturerName", strManufacturerName);
		mParam.put("model", strModel);
		mParam.put("deviceType", strDeviceType);
		mParam.put("protocolType", strProtocolType);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPutJsonForString(strUrlModifyDeviceInfo, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	// Get Device List
	public Vector<String> getDeviceList(String strNodeType, String strStatus, String strGatewayId) throws Exception {
		// Check Param
		if (!checkParam(strNodeType) || !checkParam(strStatus)) {
			mLogPrinter.printlnAsError("Get Device List, Some Param is NULL.");
			return null;
		}
		while (strGatewayId.endsWith(" ")) {
			strGatewayId = strGatewayId.substring(0, strGatewayId.length() - 1);
		}

		// Request URL
		String strUrlQueryDevices = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("appId", mStrAppId);
		mParam.put("pageNo", "0");
		mParam.put("pageSize", "1000");			// Just a Demo, no need to take care more than 1000 device
		if (strStatus.equalsIgnoreCase("ALL") == false) {
			mParam.put("status", strStatus);
		}
		if (strNodeType.equalsIgnoreCase("ALL") == false) {
			mParam.put("nodeType", strNodeType);
		}
		if (checkParam(strGatewayId)) {
			mParam.put("gatewayId", strGatewayId);
		}
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrlQueryDevices, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}
		if ((Integer)mResult.get("totalCount") == 0) {
			return null;
		}

		// Get Device List
		String strDevicesList = JsonUtil.jsonObj2Sting(mResult.get("devices"));
		strDevicesList = strDevicesList.substring(1, strDevicesList.length() - 1);
		Vector<String> vDeviceInfo = parseStringToArray(strDevicesList);
		Vector<String> vDeviceList = new Vector<String>(10, 1);
		for (int i = 0; i < vDeviceInfo.size(); ++i) {
			Map<String, Object> mDeviceInfo = new HashMap<String, Object>();
			mDeviceInfo = JsonUtil.jsonString2SimpleObj(vDeviceInfo.elementAt(i), mDeviceInfo.getClass());
			vDeviceList.addElement(mDeviceInfo.get("deviceId").toString());
		}
		return vDeviceList;
	}

	// Check Device Activated Status
	public boolean checkDeviceStatus(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Check Device Status, Device ID is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrlQueryStatus = mStrBaseUrl + "iocm/app/reg/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrlQueryStatus, null, mMapHeader);
		System.out.println(strResult);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			throw new Exception();
		}
		return (Boolean)mResult.get("activated");
	}

	// Query Device Basic Info
	public DeviceBasicInfo queryDeviceBasicInfo(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Query Device Basic Info, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, null, mMapHeader);
		System.out.println(strResult);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		String strGateway = mResult.get("gatewayId").toString();
		String strNodeType = mResult.get("nodeType").toString();

		String strDeviceInfo = JsonUtil.jsonObj2Sting(mResult.get("deviceInfo"));
		Map<String, Object> mDeviceInfo = new HashMap<String, Object>();
		mDeviceInfo = JsonUtil.jsonString2SimpleObj(strDeviceInfo, mDeviceInfo.getClass());

		String strNodeId = new String();
		if (mDeviceInfo.get("nodeId") != null) {
			strNodeId = mDeviceInfo.get("nodeId").toString();
		}
		String strManufacturerId = new String();
		if (mDeviceInfo.get("manufacturerId") != null) {
			strManufacturerId = mDeviceInfo.get("manufacturerId").toString();
		}
		String strManufacturerName = new String();
		if (mDeviceInfo.get("manufacturerName") != null) {
			strManufacturerName = mDeviceInfo.get("manufacturerName").toString();
		}
		String strDeviceType = new String();
		if (mDeviceInfo.get("deviceType") != null) {
			strDeviceType = mDeviceInfo.get("deviceType").toString();
		}
		String strModel = new String();
		if (mDeviceInfo.get("model") != null) {
			strModel = mDeviceInfo.get("model").toString();
		}
		String strProtocolType = new String();
		if (mDeviceInfo.get("protocolType") != null) {
			strProtocolType = mDeviceInfo.get("protocolType").toString();
		}
		String strStatus = mDeviceInfo.get("status").toString();
		return new DeviceBasicInfo(strDeviceId, strNodeId, strManufacturerId, strManufacturerName,
									strModel, strDeviceType, strProtocolType, strGateway,
									strNodeType, strStatus);
	}

	// Get Device Service List
	public Vector<ServiceInfo> getServiceList(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Get Device Service List, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/data/v1.1.0/deviceCapabilities";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Get Device List
		String strDeviceList = JsonUtil.jsonObj2Sting(mResult.get("deviceCapabilities"));
		strDeviceList = strDeviceList.substring(1, strDeviceList.length() - 1);
		Vector<String> vDeviceList = parseStringToArray(strDeviceList);
		if (vDeviceList.size() == 0) {
			mLogPrinter.printlnAsError("Can't Find The Device.");
			return null;
		} else if (vDeviceList.size() > 1) {
			mLogPrinter.printlnAsError("Find Too Many Devices (>2).");
			return null;
		}

		// Get Service List
		Map<String, Object> mDevice = new HashMap<String, Object>();
		mDevice = JsonUtil.jsonString2SimpleObj(vDeviceList.elementAt(0), mDevice.getClass());
		String strServiceList = JsonUtil.jsonObj2Sting(mDevice.get("serviceCapabilities"));
		strServiceList = strServiceList.substring(1, strServiceList.length() - 1);
		Vector<String> vServieInfo = parseStringToArray(strServiceList);
		if (vServieInfo.size() == 0) {
			mLogPrinter.printlnAsError("No Service For Current Device.");
			return null;
		}

		// Get Property List
		Vector<ServiceInfo> vServiceList = new Vector<ServiceInfo>(10, 1);
		for (int i = 0; i < vServieInfo.size(); ++i) {
			Map<String, Object> mService = new HashMap<String, Object>();
			mService = JsonUtil.jsonString2SimpleObj(vServieInfo.elementAt(i), mService.getClass());
			String strServiceId = mService.get("serviceId").toString();
			String strServiceType = mService.get("serviceType").toString();
			if (strServiceId.equalsIgnoreCase("RawData") || strServiceType.equalsIgnoreCase("RawData")) {
				vServiceList.addElement(new ServiceInfo(strServiceId, strServiceType, null, null));
			} else {
				String strPropertyList = JsonUtil.jsonObj2Sting(mService.get("properties"));
				strPropertyList = strPropertyList.substring(1, strPropertyList.length() - 1);
				Vector<String> vPropertyInfo = parseStringToArray(strPropertyList);
				if (vPropertyInfo.size() == 0) {
					mLogPrinter.printlnAsError("This Service ( " + strServiceType + " ) Has No Property.");
					continue;
				}
				Vector<String> vProperty = new Vector<String>(10, 1);
				for (int j = 0; j < vPropertyInfo.size(); ++j) {
					Map<String, Object> mPropertyInfo = new HashMap<String, Object>();
					mPropertyInfo = JsonUtil.jsonString2SimpleObj(vPropertyInfo.elementAt(j).toString(), mPropertyInfo.getClass());
					vProperty.addElement(mPropertyInfo.get("propertyName").toString());
				}
				vServiceList.addElement(new ServiceInfo(strServiceId, strServiceType, vProperty, null));
			}
		}
		return vServiceList;
	}

	// Query Device Current Raw Data
	public CurrentData queryRawData(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Query Device Raw Data, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, null, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Get Service List
		String strServiceData = JsonUtil.jsonObj2Sting(mResult.get("services"));
		strServiceData = strServiceData.substring(1, strServiceData.length() - 1);
		Vector<String> vServiceInfo = parseStringToArray(strServiceData);
		if (vServiceInfo.size() == 0) {
			mLogPrinter.printlnAsError("No Data For Current Device.");
			return null;
		} else if (vServiceInfo.size() > 1) {
			mLogPrinter.printlnAsError("RawData Should be the Only Service.");
			return null;
		}

		// Get Raw Data
		Map<String, Object> mRawDataService = new HashMap<String, Object>();
		mRawDataService = JsonUtil.jsonString2SimpleObj(vServiceInfo.elementAt(0), mRawDataService.getClass());
		return new CurrentData(mRawDataService.get("eventTime").toString(), (Map<String, Object>)mRawDataService.get("data"));
	}

	// Query History Raw Data
	public Map<String, Vector<HistoryData>> queryHistoryRawData(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Query Device History Raw Data, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Get GatewayID
		String strGatewayId = getGatewayIdOfDevice(strDeviceId);
		if (!checkParam(strGatewayId)) {
			mLogPrinter.printlnAsError("Query Device History Raw Data, Can't Find Gateway ID.");
			return null;
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/data/v1.1.0/deviceDataHistory";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		mParam.put("gatewayId", strGatewayId);
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Get History Data List
		String strDeviceHistoryData = JsonUtil.jsonObj2Sting(mResult.get("deviceDataHistoryDTOs"));
		strDeviceHistoryData = strDeviceHistoryData.substring(1, strDeviceHistoryData.length() - 1);
		Vector<String> vDeviceHistoryData = parseStringToArray(strDeviceHistoryData);
		if (vDeviceHistoryData.size() == 0) {
			mLogPrinter.printlnAsError("No Device History Data.");
			return null;
		}
		Map<String, Vector<HistoryData>> mHistoryData = new HashMap<String, Vector<HistoryData>>();
		// Init Data Struct
		Vector<HistoryData> vData = new Vector<HistoryData>(10, 1);
		mHistoryData.put("rawData", vData);
		for (int i = 0; i < vDeviceHistoryData.size(); ++i) {
			Map<String, Object> mServiceData = new HashMap<String, Object>();
			mServiceData = JsonUtil.jsonString2SimpleObj(vDeviceHistoryData.elementAt(i), mServiceData.getClass());
			Map<String, Object> mData = new HashMap<String, Object>();
			mData = JsonUtil.jsonString2SimpleObj(JsonUtil.jsonObj2Sting(mServiceData.get("data")), mData.getClass());
			vData.addElement(new HistoryData(mServiceData.get("timestamp").toString(), mData.get("rawData")));
		}
		return mHistoryData;
	}

	// Get Gateway ID of Device
	String getGatewayIdOfDevice(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Get Gateway ID From Device, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, null, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Return Gateway ID
		return mResult.get("gatewayId").toString();
	}

	// Query Device Current Data
	public CurrentData queryData(String strDeviceId, String strServiceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId)) {
			mLogPrinter.printlnAsError("Query Device Data, Some Param is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/dm/v1.1.0/devices/" + strDeviceId;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, null, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Get Service List
		String strServiceData = JsonUtil.jsonObj2Sting(mResult.get("services"));
		strServiceData = strServiceData.substring(1, strServiceData.length() - 1);
		Vector<String> vServiceList = parseStringToArray(strServiceData);
		if (vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("No Data For Current Device.");
			return null;
		}

		// Get Data
		for (int i = 0; i < vServiceList.size(); ++i) {
			Map<String, Object> mService = new HashMap<String, Object>();
			mService = JsonUtil.jsonString2SimpleObj(vServiceList.elementAt(i), mService.getClass());
			if (strServiceId.equalsIgnoreCase(mService.get("serviceId").toString())) {
				return new CurrentData(mService.get("eventTime").toString(), (Map<String, Object>)mService.get("data"));
			}
		}
		return null;
	}

	// Query History Data
	public Map<String, Vector<HistoryData>> queryHistoryData(String strDeviceId, String strServiceId, Vector<String> vProperty, String strProperty) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strProperty)
				|| vProperty == null || vProperty.size() == 0) {
			mLogPrinter.printlnAsError("Query Device History Data, Some Param is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Get GatewayID
		String strGatewayId = getGatewayIdOfDevice(strDeviceId);
		if (!checkParam(strGatewayId)) {
			mLogPrinter.printlnAsError("Query Device History Data, Can't Find Gateway ID.");
			return null;
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/data/v1.1.0/deviceDataHistory";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		mParam.put("gatewayId", strGatewayId);
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		if ((Integer)mResult.get("totalCount") == 0) {
			return null;
		}

		// Get History Data List
		String strDeviceHistoryData = JsonUtil.jsonObj2Sting(mResult.get("deviceDataHistoryDTOs"));
		strDeviceHistoryData = strDeviceHistoryData.substring(1, strDeviceHistoryData.length() - 1);
		Vector<String> vDeviceHistoryData = parseStringToArray(strDeviceHistoryData);
		if (vDeviceHistoryData.size() == 0) {
			mLogPrinter.printlnAsError("No History Data For This Device.");
			return null;
		}
		Map<String, Vector<HistoryData>> mHistoryData = new HashMap<String, Vector<HistoryData>>();
		// Init Data Struct
		for (int i = 0; i < vProperty.size(); ++i) {
			Vector<HistoryData> vData = new Vector<HistoryData>(10, 5);
			mHistoryData.put(vProperty.elementAt(i), vData);
		}
		for (int i = 0; i < vDeviceHistoryData.size(); ++i) {
			Map<String, Object> mDeviceHistoryData = new HashMap<String, Object>();
			mDeviceHistoryData = JsonUtil.jsonString2SimpleObj(vDeviceHistoryData.elementAt(i), mDeviceHistoryData.getClass());
			if (strServiceId.equalsIgnoreCase(mDeviceHistoryData.get("serviceId").toString())) {
				Map<String, Object> mData = new HashMap<String, Object>();
				mData = JsonUtil.jsonString2SimpleObj(JsonUtil.jsonObj2Sting(mDeviceHistoryData.get("data")), mData.getClass());
				for (int j = 0; j < vProperty.size(); ++j) {
					String strCurProperty = vProperty.elementAt(j);
					if (strProperty.equalsIgnoreCase("ALL") || strProperty.equalsIgnoreCase(strCurProperty)) {
						HistoryData objData = new HistoryData(mDeviceHistoryData.get("timestamp").toString(), mData.get(strCurProperty));
						mHistoryData.get(strCurProperty).addElement(objData);
					}
				}
			}
		}
		return mHistoryData;
	}
	
	// Get Device Command List
	public Vector<ServiceInfo> getCommandList(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Get Device Command List, Device ID is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/data/v1.1.0/deviceCapabilities";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		// Get Device List
		String strDeviceList = JsonUtil.jsonObj2Sting(mResult.get("deviceCapabilities"));
		strDeviceList = strDeviceList.substring(1, strDeviceList.length() - 1);
		Vector<String> vDeviceList = parseStringToArray(strDeviceList);
		if (vDeviceList.size() == 0) {
			mLogPrinter.printlnAsError("Can't Find The Device.");
			return null;
		} else if (vDeviceList.size() > 1) {
			mLogPrinter.printlnAsError("Find Too Many Devices (> 2).");
			return null;
		}

		// Get Service List
		Map<String, Object> mDeviceInfo = new HashMap<String, Object>();
		mDeviceInfo = JsonUtil.jsonString2SimpleObj(vDeviceList.elementAt(0), mDeviceInfo.getClass());
		String strServiceList = JsonUtil.jsonObj2Sting(mDeviceInfo.get("serviceCapabilities"));
		strServiceList = strServiceList.substring(1, strServiceList.length() - 1);
		Vector<String> vServiceList = parseStringToArray(strServiceList);
		if (vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("No Service For Current Device.");
			return null;
		}

		Vector<ServiceInfo> vCommandList = new Vector<ServiceInfo>(10, 1);

		// Check if RawData Mode
		if (vServiceList.size() == 1) {
			Map<String, Object> mService = new HashMap<String, Object>();
			mService = JsonUtil.jsonString2SimpleObj(vServiceList.elementAt(0), mService.getClass());
			String strServiceId = mService.get("serviceId").toString();
			if (strServiceId.equals("RawData")) {
				vCommandList.addElement(new ServiceInfo("RawData", "RawData", null, null));
				return vCommandList;
			}
		}

		// Get Command List
		for (int i = 0; i < vServiceList.size(); ++i) {
			Map<String, Object> mService = new HashMap<String, Object>();
			mService = JsonUtil.jsonString2SimpleObj(vServiceList.elementAt(i), mService.getClass());
			String strServiceId = mService.get("serviceId").toString();
			String strServiceType = mService.get("serviceType").toString();
			if (strServiceId.equalsIgnoreCase("RawData") || strServiceType.equalsIgnoreCase("RawData")) {
				mLogPrinter.printlnAsError("No Command with RawData.");
				return null;
			}
			String strCommandList = JsonUtil.jsonObj2Sting(mService.get("commands"));
			strCommandList = strCommandList.substring(1, strCommandList.length() - 1);
			Vector<String> vCommand = parseStringToArray(strCommandList);
			if (vCommand.size() == 0) {
				mLogPrinter.printlnAsError("This Service ( " + strServiceType + " ) Has No Command.");
				continue;
			}
			// Get Command Type List
			Vector<CommandInfo> vCommandTypeList = new Vector<CommandInfo>(10, 1);
			for (int j = 0; j < vCommand.size(); ++j) {
				Map<String, Object> mCommand = new HashMap<String, Object>();
				mCommand = JsonUtil.jsonString2SimpleObj(vCommand.elementAt(j), mCommand.getClass());
				String strCommandName = mCommand.get("commandName").toString();
				String strParaList = JsonUtil.jsonObj2Sting(mCommand.get("paras"));
				strParaList = strParaList.substring(1, strParaList.length() - 1);
				Vector<String> vParaList = parseStringToArray(strParaList);
				if (vParaList.size() == 0) {
					mLogPrinter.printlnAsError("This Command ( " + strCommandName + " ) Has No Item.");
					continue;
				}
				// Get Para List
				Vector<String> vParaNameList = new Vector<String>(10, 1);
				for (int k = 0; k < vParaList.size(); ++k) {
					Map<String, Object> mPara = new HashMap<String, Object>();
					mPara = JsonUtil.jsonString2SimpleObj(vParaList.elementAt(k), mPara.getClass());
					vParaNameList.addElement(mPara.get("paraName").toString());
				}
				vCommandTypeList.addElement(new CommandInfo(strCommandName, vParaNameList));
			}
			vCommandList.addElement(new ServiceInfo(strServiceId, strServiceType, null, vCommandTypeList));
		}
		return vCommandList;
	}

	// Post Async Command with RawData
	public boolean postAsyncCommandWithRawData(String strDeviceId, String strData, String strTimeout) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strData) || !checkParam(strTimeout)) {
			mLogPrinter.printlnAsError("Post Aysnc Command with RawData, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/devices/" + strDeviceId + "/commands";
		// Param
		ObjectNode oValueParam = JsonUtil.convertObject2ObjectNode("{\"" + "rawData" + "\":\"" + strData + "\"}");
		Map<String, Object> mParamCommand = new HashMap<String, Object>();
		mParamCommand.put("serviceId", "RawData");
		mParamCommand.put("method", "RawData");
		mParamCommand.put("paras", oValueParam);
		mParamCommand.put("hasMore", 0);
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("command", mParamCommand);
		mParam.put("expireTime", strTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		// Parse result
		return parseResultString(strResult);
	}

	// Post Asnyc Command
	public boolean postAsyncCommand(String strDeviceId, String strServiceId, String strCommand, String strItem, String strValue, String strTimeout) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strCommand)
				|| !checkParam(strItem) || !checkParam(strValue) || !checkParam(strTimeout)) {
			mLogPrinter.printlnAsError("Post Aysnc Command, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/devices/" + strDeviceId + "/commands";
		// Param
		ObjectNode oValueParam = JsonUtil.convertObject2ObjectNode("{\"" + strItem + "\":\"" + strValue + "\"}");
		Map<String, Object> mParamCommand = new HashMap<String, Object>();
		mParamCommand.put("serviceId", strServiceId);
		mParamCommand.put("method", strCommand);
		mParamCommand.put("paras", oValueParam);
		mParamCommand.put("hasMore", 0);
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("command", mParamCommand);
		mParam.put("expireTime", strTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		// Parse result
		return parseResultString(strResult);
	}

	// Query Asnyc Command
	public Vector<CommandDetail> queryAsyncCommand(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Post Aysnc Command, Some Param is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/queryCmd";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, mParam, mMapHeader);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}

		if ((Integer)mResult.get("totalCount") == 0) {
			return null;
		}

		// Get Command List
		String strCmdList = JsonUtil.jsonObj2Sting(mResult.get("list"));
		strCmdList = strCmdList.substring(1, strCmdList.length() - 1);
		Vector<String> vCmdList = parseStringToArray(strCmdList);

		Vector<CommandDetail> vReturnCmdList = new Vector<CommandDetail>(10, 1);

		for (int i = 0; i < vCmdList.size(); ++i) {
			String strCommandDetail = vCmdList.elementAt(i);
			Map<String, Object> mCommandDetail = new HashMap<String, Object>();
			mCommandDetail = JsonUtil.jsonString2SimpleObj(strCommandDetail, mCommandDetail.getClass());

			String strCommandId = mCommandDetail.get("commandId").toString();
			Integer nTimeout = (Integer)mCommandDetail.get("expireTime");

			// Command Detail
			Map<String, Object> mCommandKey = new HashMap<String, Object>();
			mCommandKey = JsonUtil.jsonString2SimpleObj(JsonUtil.jsonObj2Sting(mCommandDetail.get("command")), mCommandKey.getClass());

			// Result (Current Status)
			Map<String, Object> mCmdResult = new HashMap<String, Object>();
			mCmdResult = JsonUtil.jsonString2SimpleObj(JsonUtil.jsonObj2Sting(mCommandDetail.get("result")), mCmdResult.getClass());
			String strServiceId = mCommandKey.get("serviceId").toString();
			String strMethod = mCommandKey.get("method").toString();
			String strParas = JsonUtil.jsonObj2Sting(mCommandKey.get("paras"));
			String strResultCode = mCmdResult.get("resultCode").toString();
			vReturnCmdList.addElement(new CommandDetail(strCommandId, strServiceId, strMethod, nTimeout, strParas, strResultCode));
		}

		return vReturnCmdList;
	}

	// Delete Async Command
	public boolean deleteAsyncCommand(String strDeviceId) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId)) {
			mLogPrinter.printlnAsError("Delete Aysnc Command, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/cancelCmd";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("deviceId", strDeviceId);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	// Send Command For Agent
	boolean sendCommand(String strDeviceId, String strServiceId, String strMode, String strFrom, String strMethod, String strKey, String strValue) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strMode) 
				|| !checkParam(strFrom) || !checkParam(strMode) || !checkParam(strKey) || !checkParam(strValue)) {
			mLogPrinter.printlnAsError("Send Command, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/signaltrans/v1.1.0/devices/" + strDeviceId + "/services/" + strServiceId + "/sendCommand";
		// Param
		Map<String, Object> mParamHeader = new HashMap<String, Object>();
		mParamHeader.put("mode", strMode);
		mParamHeader.put("from", strFrom);
		mParamHeader.put("method", strMethod);
		mParamHeader.put("callbackURL", "http://10.1.1.1:8080/ab/cd/ef");
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("header", mParamHeader);
		Map<String, Object> mBody = new HashMap<String, Object>();
		mBody.put(strKey, strValue);
		mParam.put("body", mBody);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		if (strResult == null || strResult.length() == 0) {
			return true;
		}
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return false;
		} else {
			return true;
		}
	}

	// Create Rule
	public String createRule(String strDeviceId, String strRuleName, String strAuthor,
							String strServiceId, String strProperty,
							String strOperation, String strValue,
							String strActionType, String strAddress,
							String strTitle, String strContent) throws Exception {
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strRuleName) || !checkParam(strAuthor) || !checkParam(strServiceId)
				|| !checkParam(strProperty) || !checkParam(strOperation) || !checkParam(strValue) || !checkParam(strActionType)
				|| !checkParam(strAddress) || !checkParam(strTitle) || !checkParam(strContent)) {
			mLogPrinter.printlnAsError("Create Rule, Some Param is NULL.");
			return null;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/rule/v1.2.0/rules";
		// Device Info
		Map<String, Object> mDeviceInfo = new HashMap<String, Object>();
		mDeviceInfo.put("deviceId", strDeviceId);
		mDeviceInfo.put("path", strServiceId + "/" + strProperty);
		// Condition List
		Map<String, Object> mCondition = new HashMap<String, Object>();
		mCondition.put("type", "DEVICE_DATA");
		mCondition.put("deviceInfo", mDeviceInfo);
		mCondition.put("operator", strOperation);
		mCondition.put("value", strValue);
		Object[] aConditionList = new Object[1];
		aConditionList[0] = mCondition;
		// Action List
		Map<String, Object> mAction = new HashMap<String, Object>();
		mAction.put("content", strContent);
		mAction.put("subject", strTitle);
		if (strActionType.equalsIgnoreCase("SMS")) {
			mAction.put("type", "SMS");
			mAction.put("msisdn", strAddress);
		} else if (strActionType.equalsIgnoreCase("EMAIL")) {
			mAction.put("type", "EMAIL");
			mAction.put("email", strAddress);
		}
		Object[] aActionList = new Object[1];
		aActionList[0] = mAction;
		// Param
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("appKey", mStrAppId);
		mParam.put("name", strRuleName);
		mParam.put("author", strAuthor);
		mParam.put("conditions", aConditionList);
		mParam.put("actions", aActionList);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return null;
		}
		return mResult.get("ruleId").toString();
	}

	// Update Rule
	public boolean updateRule(String strRuleId, String strDeviceId,
								String strRuleName, String strAuthor,
								String strServiceId, String strProperty,
								String strOperation, String strValue,
								String strActionType, String strAddress,
								String strTitle, String strContent) throws Exception {
		// Check Param
		if (!checkParam(strRuleId) || !checkParam(strDeviceId) || !checkParam(strRuleName) || !checkParam(strAuthor)
				|| !checkParam(strServiceId) || !checkParam(strProperty) || !checkParam(strOperation) || !checkParam(strValue)
				|| !checkParam(strActionType) || !checkParam(strAddress) || !checkParam(strTitle) || !checkParam(strContent)) {
			mLogPrinter.printlnAsError("Update Rule, Some Param is NULL.");
			return false;
		}
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}
		while (strRuleId.endsWith(" ")) {
			strRuleId = strRuleId.substring(0, strRuleId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/rule/v1.2.0/rules";
		// Device Info
		Map<String, Object> mDeviceInfo = new HashMap<String, Object>();
		mDeviceInfo.put("deviceId", strDeviceId);
		mDeviceInfo.put("path", strServiceId + "/" + strProperty);
		// Condition List
		Map<String, Object> mCondition = new HashMap<String, Object>();
		mCondition.put("type", "DEVICE_DATA");
		mCondition.put("deviceInfo", mDeviceInfo);
		mCondition.put("operator", strOperation);
		mCondition.put("value", strValue);
		Object[] aConditionList = new Object[1];
		aConditionList[0] = mCondition;
		// Action List
		Map<String, Object> mAction = new HashMap<String, Object>();
		mAction.put("content", strContent);
		mAction.put("subject", strTitle);
		if (strActionType.equalsIgnoreCase("SMS")) {
			mAction.put("type", "SMS");
			mAction.put("msisdn", strAddress);
		} else if (strActionType.equalsIgnoreCase("EMAIL")) {
			mAction.put("type", "EMAIL");
			mAction.put("email", strAddress);
		}
		Object[] aActionList = new Object[1];
		aActionList[0] = mAction;
		// Param
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("ruleId", strRuleId);
		mParam.put("appKey", mStrAppId);
		mParam.put("name", strRuleName);
		mParam.put("author", strAuthor);
		mParam.put("conditions", aConditionList);
		mParam.put("actions", aActionList);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	// Query Rule
	public Vector<RuleInfo> queryRule(String strAuthor, String strRuleName) throws Exception {
		// Check Param
		if (!checkParam(strAuthor)) {
			mLogPrinter.printlnAsError("Query Rule, Author is NULL.");
			return null;
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/rule/v1.2.0/rules?name=" + strRuleName + "&author=" + strAuthor;
		// Send Request
		String strResult = mObjHttpsUtil.doGetWithParasForString(strUrl, null, mMapHeader);

		// Parse Result
		strResult = strResult.substring(1, strResult.length() - 1);
		if (strResult.length() == 0) {
			mLogPrinter.printlnAsError("No Rule As Needed.");
			return null;
		}
		Vector<String> vRuleList = parseStringToArray(strResult);

		Vector<RuleInfo> vReturnRuleList = new Vector<RuleInfo>(10, 1);
		for (int i = 0; i != vRuleList.size(); ++i) {
			// Rule Info
			Map<String, Object> mRule = new HashMap<String, Object>();
			mRule = JsonUtil.jsonString2SimpleObj(vRuleList.elementAt(i), mRule.getClass());
			String strRuleId = mRule.get("ruleId").toString();
			String strNewRuleName = mRule.get("name").toString();
			String strNewAuthor = mRule.get("author").toString();
			String strStatus = mRule.get("status").toString();
			vReturnRuleList.addElement(new RuleInfo(strRuleId, strNewRuleName, strNewAuthor, strStatus));
		}

		return vReturnRuleList;
	}

	// Modify Rule Status
	public boolean modifyRuleStatus(String strRuleId, String strStatus) throws Exception {
		// Check Param
		if (!checkParam(strRuleId) || !checkParam(strStatus)) {
			mLogPrinter.printlnAsError("Modify Rule Status, Some Param is NULL.");
			return false;
		}
		while (strRuleId.endsWith(" ")) {
			strRuleId = strRuleId.substring(0, strRuleId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/rule/v1.2.0/rules/" + strRuleId + "/status/" + strStatus;
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("ruleId", strRuleId);
		mParam.put("status", strStatus);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPutJsonForString(strUrl, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	// Delete Rule
	public boolean deleteRule(String strRuleId) throws Exception {
		// Check Param
		if (!checkParam(strRuleId)) {
			mLogPrinter.printlnAsError("Delete Rule, Rule ID is NULL.");
			return false;
		}
		while (strRuleId.endsWith(" ")) {
			strRuleId = strRuleId.substring(0, strRuleId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/rule/v1.2.0/rules/" + strRuleId;
		// Send Request
		String strResult = mObjHttpsUtil.doDeleteForString(strUrl, mMapHeader);

		// Parse Result
		return parseResultString(strResult);
	}

	// Subscribe Notify
	public boolean subscribe(String strNotifyType, String strCallbackUrl) throws Exception {
		// Check Param
		if (!checkParam(strNotifyType) || !checkParam(strCallbackUrl)) {
			mLogPrinter.printlnAsError("Subscribe, Some Param is NULL.");
			return false;
		}

		// Request URL
		String strUrlSubscribe = mStrBaseUrl + "iocm/app/sub/v1.1.0/subscribe";
		// Param
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("notifyType", strNotifyType);
		mParam.put("callbackurl", strCallbackUrl);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrlSubscribe, mMapHeader, strRequest);

		// Parse Result
		return parseResultString(strResult);
	}

	/***** Private Function *****/
	// Param Check
	boolean checkParam(String strParam) {
		if (strParam == null || strParam.length() == 0) {
			return false;
		}
		return true;
	}

	// Error handling
	void errorHandle(Map<String, Object> mResult) throws Exception {
		String strErrCode = mResult.get("error_code").toString();
		String strErrDesc = mResult.get("error_desc").toString();
		mLogPrinter.printlnAsError("Error Code: " + strErrCode + ", Reason: " + strErrDesc);
	}

	// Parse Result String
	boolean parseResultString(String strResult) throws Exception {
		if (strResult == null || strResult.length() == 0) {
			return true;
		}

		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return false;
		} else {
			return true;
		}
	}

	// Parse Array
	Vector<String> parseStringToArray(String strSource) {
		// Check
		if (strSource == null || strSource.length() == 0) {
			return null;
		}
		Vector<String> vList = new Vector<String>(10, 1);
		int nLeftBrace = 0;
		int nCurPartStart = 0;
		for (int i = 0; i < strSource.length(); ++i) {
			if (strSource.charAt(i) == '{') {
				++nLeftBrace;
			} else if (strSource.charAt(i) == '}') {
				--nLeftBrace;
				if (nLeftBrace == 0) {
					String strCurPart = strSource.substring(nCurPartStart, i + 1);
					vList.addElement(strCurPart);
					nCurPartStart = i + 2;
				}
			}
		}
		return vList;
	}

	// Get Property as Service Type
	Vector<String> getPropertyList(Vector<ServiceInfo> vServiceList, String strServiceId) {
		for (int i = 0; i < vServiceList.size(); ++i) {
			if (strServiceId.equalsIgnoreCase(vServiceList.elementAt(i).mStrServiceId)) {
				return vServiceList.elementAt(i).mVecProperty;
			}
		}
		return null;
	}

	// Get Service ID
	String getServiceId(Vector<ServiceInfo> vServiceList, String strServiceType) {
		for (int i = 0; i < vServiceList.size(); ++i) {
			if (strServiceType.equalsIgnoreCase(vServiceList.elementAt(i).mStrServiceType)) {
				return vServiceList.elementAt(i).mStrServiceId;
			}
		}
		return null;
	}

	// Get Time From Time Stamp
	String getTime(String strTimestamp) {
		String strHour = strTimestamp.substring(9, 11);
		String strMinute = strTimestamp.substring(11, 13);
		String strSecond = strTimestamp.substring(13, 15);
		return strHour + ":" + strMinute + ":" + strSecond;
	}

	// Get Date From Time Stamp
	String getDate(String strTimestamp) {
		String strYear = strTimestamp.substring(0, 4);
		String strMonth = strTimestamp.substring(4, 6);
		String strDate = strTimestamp.substring(6, 8);
		return strYear + "/" + strMonth + "/" + strDate;
	}
}
