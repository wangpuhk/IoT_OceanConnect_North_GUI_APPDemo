package Demo;

public abstract class ManagerModule {
	// App Task
	AppTask mAppTask;
	// Log Printer;
	LogPrinter mLogPrinter;
	// Main Win
	MainWin mWin;

	// Constructor
	ManagerModule(AppTask app, LogPrinter log) {
		mAppTask = app;
		mLogPrinter = log;
		mWin = null;
	}

	// Constructor
	ManagerModule(AppTask app, LogPrinter log, MainWin win) {
		mAppTask = app;
		mLogPrinter = log;
		mWin = win;
	}
}
