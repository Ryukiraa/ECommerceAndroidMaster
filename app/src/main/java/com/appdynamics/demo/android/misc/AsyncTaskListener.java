package com.appdynamics.demo.android.misc;

public interface AsyncTaskListener {
	void cancelled();
	void onPostExecute(boolean success, boolean error,String exceptionMessage);	
}
