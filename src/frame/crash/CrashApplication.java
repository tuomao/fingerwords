package frame.crash;

import android.app.Application;

public class CrashApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(getApplicationContext());
		// 发�?以前没发送的报告(可�?)
		crashHandler.sendPreviousReportsToServer();
	}
}