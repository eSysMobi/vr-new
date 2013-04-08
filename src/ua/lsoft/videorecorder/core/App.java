package ua.lsoft.videorecorder.core;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", formUri = "http://psi.kh.ua/test/acra.php", socketTimeout = 30000)
public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		ACRA.init(this);
	}

}
