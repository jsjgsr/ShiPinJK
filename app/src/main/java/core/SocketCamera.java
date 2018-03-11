package core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketCamera implements CameraSource {

	
	private static final int SOCKET_TIMEOUT = 500;//1000毫秒===0.5秒
	
	private final String address;
	private final int port;
	private final Rect rect;
	private final boolean isnew;
	private final Paint paint = new Paint();//画笔


	private Socket msocket = null;

	public SocketCamera(String address, int port, int width, int height, boolean isnew) {//定义全局参数
		this.address = address;
		this.port = port;
		rect = new Rect(0,0,1,1);//画矩形
		this.isnew = isnew;

		paint.setFilterBitmap(true);//用来对位图进行滤波处理
		paint.setAntiAlias(true);//防止边缘的锯齿
	}
	
	public int getWidth() {
		return rect.right;
	}
	
	public int getHeight() {
		return rect.bottom;
	}
	
	public boolean open() {
		/* nothing to do */
		return true;
	}


	public boolean capture(Canvas canvas) {
		if (canvas == null) throw new IllegalArgumentException("null canvas");
		try {
			msocket = new Socket();
			msocket.bind(null);//绑定一个名字
			/*调用bind()函数之后，为socket()函数创建的套接字关联一个相应地址，发送到这个地址的数据可以通过该套接字读取与使用。*/
			msocket.setSoTimeout(SOCKET_TIMEOUT);//超过10秒后
			msocket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);
			//obtain the bitmap
			InputStream inputStream =msocket.getInputStream();

			int size = 14500540;//1450054;//31000;//40000//图片显示与数据池的问题，必须与传来的数据大小一样
			int num = 0;
			byte[] mybuffer = new byte[size];//最终一张图片的数据大小
			byte[] smallbuffer = new byte[80920];//8092

			int readnum = inputStream.read(smallbuffer);
			System.arraycopy(smallbuffer, 0, mybuffer, num, readnum);//实现数组间的复制????????????????????????能否删掉
			//               btye 8092 ,0,btye count, 0 ,长度
			//arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
			// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组； destPos:目的数组放置的起始位置； length:复制的长度。
			/*
			*  int[] fun ={0,1,2,3,4,5,6};
			*  System.arraycopy(fun,0,fun,3,3);
			*  则结果为：{0,1,2,0,1,2,6};
			*  实现过程是这样的，先生成一个长度为length的临时数组,
			*  将fun数组中srcPos 到srcPos+length-1之间的数据拷贝到临时数组中，
			*  再执行System.arraycopy(临时数组,0,fun,3,3).*/
			num += readnum;
			while(readnum>0){//流是可以利用的
				readnum=inputStream.read(smallbuffer);
				if (readnum>0)
				{
					System.arraycopy(smallbuffer, 0, mybuffer, num, readnum);
					num+=readnum;
				}
			}
//			Bitmap bitmap = BitmapFactory.decodeStream(in);//将InputStream转换成Bitmap
			Bitmap mybitmap = BitmapFactory.decodeByteArray(mybuffer, 0, size);//获取网络图片、、可以在后面加上mybitmap.rec
			//BitmapFactory。用于从不通的数据源解析创建bitmap对象
			//先把网络图片的数据流读到内存中，然后用。方法来将图片流传化为bitmap类型 这样才能用到
			//render it to canvas, scaling if necessary
			if (rect.right == mybitmap.getWidth() && rect.bottom == mybitmap.getHeight()) {
				canvas.drawBitmap(mybitmap, 0, 0, null);//绘制图形
			} else {
				Rect myrect;
				if (isnew) {//是否自己定义画布  一直是 true
					myrect = new Rect(rect);//绘制矩形
					//高
					myrect.bottom = mybitmap.getHeight() * rect.right / mybitmap.getWidth();//得到矩形的高 right： 指定矩形框右下角的x坐标。bottom：指定矩形框右下角的y坐标
//					myrect.offset(0, (rect.bottom - myrect.bottom)/2);//添加矩形，0的左边右边的坐标，给坐标一个补偿值？？？？？？？？？？？？？？？可以删除吗
				} else {
					myrect = rect;
				}
				//dest：是图片在Canvas画布中显示的区域
				canvas.drawBitmap(mybitmap, null, myrect, paint);//绘制图形
			}


		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} finally {
			try {
				msocket.close();
			} catch (IOException e) {
				/* ignore */
			}
		}
		return true;
	}

	public void close() {
		/* nothing to do */
	}

	public boolean saveImage(String savePath, String fileName) {
		//obtain the bitmap
		try {
			msocket = new Socket();
			msocket.bind(null);//绑定一个名字
			/*调用bind()函数之后，为socket()函数创建的套接字关联一个相应地址，发送到这个地址的数据可以通过该套接字读取与使用。*/
			msocket.setSoTimeout(SOCKET_TIMEOUT);//超过10秒后
			msocket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);
			//obtain the bitmap
			InputStream in = msocket.getInputStream();

			Bitmap bitmap = BitmapFactory.decodeStream(in);
			FileOutputStream fos = new FileOutputStream(savePath + "/" + fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);//图片压缩技术，压缩率25%,压缩并保存

			if(fos != null){
				fos.close() ;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
