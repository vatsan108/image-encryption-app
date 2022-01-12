package com.android.receiver;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "dayanand.shine@gmail.com",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.app_name)

public class Myappcrash extends Application {

	 @Override
	  public void onCreate() {
	    // The following line triggers the initialization of ACRA
	    super.onCreate();
	    ACRA.init(this);
	  }
}
