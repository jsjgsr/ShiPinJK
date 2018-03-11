package com.example.gsr_pc.myproject0324;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;

import config.CamMonitorParameter;
import core.CameraSource;
import core.SocketCamera;


public class CamMonitorView extends SurfaceView implements SurfaceHolder.Callback {

	/** The thread that actually draws the image stream */
    private CamMonitorThread thread;
    public static final String TAG = "CamMonitorView";
    private CamMonitorParameter cmPara;
	/**
     * The constructor called from the main ServerAct activity
     * 
     * @param context 
     * @param attrs 
     */

	public CamMonitorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();//获得SurfaceHolder对象
        holder.addCallback(this);

        thread = new CamMonitorThread(holder);//执行线程进行图像的加载

        Log.d(TAG, "@@@ done creating view!");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {//视图创建的时候执行线程
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.closeCameraSource();
		boolean retry = true;
	     while (retry) {
	    	 try {
	    		 thread.join();
	             retry = false;
				} catch (InterruptedException e) {
	         }
	    }
	}

	class CamMonitorThread extends Thread{
		/** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        private boolean mRun = false;
        private CameraSource cs;
        private Canvas c = null;//定义画布
		public CamMonitorThread(SurfaceHolder surfaceHolder) {
			super();
			mSurfaceHolder = surfaceHolder;
		}
        public void setRunning(boolean b) {
            mRun = b;
            if (mRun == false) {
            }
        }
        public void run() {
            while (mRun) {
                try {
                    c = mSurfaceHolder.lockCanvas(null);//获得canvas对象获得Canvas对象并锁定画布
					int p = Integer.parseInt(cmPara.getPort1());
                    captureImage(cmPara.getIp(), p, mCanvasWidth, mCanvasHeight);
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变，将图形显示。
                        c = null;
                    }
                }// end finally block
                
            }
        }
//		public synchronized void closeThread() {
//			try {
//				notify();
//				interrupt();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
		}
		private boolean captureImage(String ip, int port, int width, int height){
			cs = new SocketCamera(ip, port, width, height, true);
	        cs.capture(c); //capture the frame onto the canvas载入图像！！！
	        return true;
		}
//		public boolean LuZhi(){
//			String now = String.valueOf(System.currentTimeMillis());
//			File destDir = new File("sdcard/myfloder" + now);
//			System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			if (!destDir.exists()) {
//				destDir.mkdirs();
//			}
//			if(cs == null){
//				return false;
//			}
//			while(true){
//				System.out.print("############################################################################################@##");
//				cs.saveImage(cmPara.getLocal_dir()+ "/myfloder" + now, now + ".JPEG");
//			}
//
//		}
		public boolean saveImage(){
			String now = String.valueOf(System.currentTimeMillis());
			if(cs == null){
				return false;
			}
			cs.saveImage(cmPara.getLocal_dir(), now + ".JPEG");
			return true;
		}
		
		public void closeCameraSource(){
			
			cs.close();
	    }

		public CameraSource getCameraSource() {
			return cs;
		}
		
		
	}

	public CamMonitorParameter getCmPara() {
		return cmPara;
	}

	public void setCmPara(CamMonitorParameter cmPara) {
		this.cmPara = cmPara;
	}

	public void setRunning(boolean b) {
        this.thread.setRunning(b);
    }

	public CamMonitorThread getThread() {
		return thread;
	}
	
	
	

}
