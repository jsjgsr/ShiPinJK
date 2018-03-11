package core;

import android.graphics.Canvas;



public interface CameraSource {

	static final String LOG_TAG = "camera";

	boolean open();

	void close();

	
	int getWidth();

	
	int getHeight();

	
	boolean capture(Canvas canvas);
	
	boolean saveImage(String savePath, String fileName);
	
}
