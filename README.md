# ShiPinJK
### 1、登录页面提示
为了能让登录注册按钮在父布局中居中显示，可以用
 <LinearLayout
            android:id="@+id/btnLayout"
            android:layout_margin="40px"
            android:layout_below="@+id/etLayout"
            android:background="#FFFFFF"
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <TextView
                android:layout_width="0dip"
                android:layout_height="wrap_content"
                android:layout_weight="1" />
            <Button
                android:id="@+id/reg_rewrite"
                android:text="@string/reg_rewrite"
                android:layout_margin="2pt"
                android:layout_width="50pt"
                android:layout_height="wrap_content" />
            <Button
                android:id="@+id/login_btn_reg"
                android:text="@string/login_btn_reg"
                android:layout_margin="2pt"
                android:layout_width="50pt"
                android:layout_height="wrap_content" />
            <TextView
                android:layout_width="0dip"
                android:layout_height="wrap_content"
                android:layout_weight="1" />
        </LinearLayout>
 两个空的TextView控件夹着两个按钮，调用android:layout_weight="1"。这个是按照权重比来分配的布局。
### 2、SharedPreferences的使用
SharedPreferences可以用来保存键值对数据，将保存的数据持久化到手机中
        //保存
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);//初始，以user命名的数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();//清空数据
        editor.putString("uid", data.get(0).get("uid"));
        editor.putString("uname", data.get(0).get("uname"));
        editor.commit();
        //读取
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//
        String uid = preferences.getString("uid", "");//获取key是uid的数据
        Integer id = Integer.parseInt(uid);
### 3、Handler机制中，Serializable序列化信息并线程通过Message发送信息给Handler
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.setData(bundle);//将bundle绑定到message中
        bundle.putSerializable("res",data);//data为数据，通过Serializable序列换放到bundle中
        handler.sendMessage(message);
        //获取
         ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
因为data的属性是ArrayList<Map<String, String>>，所以要强制转换一下
### 4、HttpClient的网络请求
       HttpClient client = new DefaultHttpClient();//获取httpclient对象
       HttpGet request = new HttpGet(url);//Get请求就用HttpGet（将发送信息放到url中），Post请求就用HttpPost，但是还要加上Entity，将数据放到Http头部
       HttpResponse response = null;//用于接收返回的数据
       response = client.execute(request);
       if(response.getStatusLine().getStatusCode() == 200){//当没有数据的时候是消息阻塞的，如果有消息先验证服务器返回的状态码，200：正常，404：没有找到资源，500：服务器异常。
         String resultStr = EntityUtils.toString(response.getEntity(),"UTF-8");//汉子要转字符集
           //解析操作
        }
### 5、JSONObject解析json字符串
        ArrayList<Map<String,String>> data = new ArrayList<Map<String, String>>();//用于放置Map对象的数组
        Map<String,String> jsonMap = new HashMap<String, String>();//一个键值对
        JSONObject jsonObject = new JSONObject(resultStr);//将json字符串转为jsonobject对象
        Iterator<String> tempKeys = jsonObject.keys();//获取所有的key
        while(tempKeys.hasNext()){//遍历每一个key，将key与value放入Map中
                String key = tempKeys.next();
                jsonMap.put(key,temp.getString(key));//获得数组里面的values值
         }
         data.add(jsonMap);
### 6、下拉菜单的使用Spinner
           <Spinner
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/SpinnerLdapConfig"/>
            
             protected Spinner spinner;
             private Cursor cursor;//游标
             spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
             ....查询数据库将查处的数据放入到游标中Cursor
              //第一个当前类名.第二个要显示的布局文件,第三个信息,第四个哪一个属性用来显示,第五个各键值的值要显示的位置
           SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                   android.R.layout.simple_spinner_item,
                   cursor,
                   new String[] { "name" },
                   new int[] { android.R.id.text1 });
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//定义样式
           spinner.setAdapter(adapter);//显示数据库导的数据
           spinner.refreshDrawableState();//刷新Android的列表视图
### 7、SQLite使用
    定义一个DatabaseHelper类用于对数据进行操作。在class中定义一个静态static class Helper（在Java世界里，经常被提到静态这个概念，static作为静态成员变量和成员函数的修饰符，意味着它为该类的所有实例所共享，也就是说当某个类的实例修改了该静态成员变量，其修改值为该类的其它所有实例所见。最近一个项目里频繁用到static修饰的内部类，再读了一下《Effective Java》才明白为什么会用static来修饰一个内部类也就是本文的中心——静态类。），extends SQLiteOpenHelper，重写：
    public void onCreate(SQLiteDatabase db) {//当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。
    和Helper自己的构造函数
在DatabaseHelper中就可以共享这个静态类。
     protected SQLiteDatabase db;
      public DatabaseHelper(Context context) {//构造函数
        this.context = context;
        db = new Helper(context).getWritableDatabase();//获取数据库连接
      }
         
### 8、AlertDialog对话框使用
new AlertDialog.Builder(MyMenuActivity.this).setTitle("确定删除吗？").setMessage("确定删除吗？" + name + "吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//点击确认按钮
                                                           public void onClick(DialogInterface dialog, int which) {
                                                              try {
                                                                  DatabaseHelper//对保存数据的操作
                                                                          .delete(MyMenuActivity.this, id);
                                                                  fillDataWithCursor();
                                                                  ActivtyUtil.openToast(MyMenuActivity.this,
                                                                          "删除成功!");
                                                              } catch (Exception e) {
                                                                  Log.e(TAGaa, e.getMessage(), e);
                                                                  ActivtyUtil.openToast(MyMenuActivity.this, e
                                                                          .getMessage());
                                                              }
                                                          }
                                                      })
                           .setNegativeButton("取消",new DialogInterface.OnClickListener() {//点击取消按钮
                                                        public void onClick(DialogInterface dialog, int which) {
                                                          }
                                                      }).show();
### 9、实现带editext的AlertDialog
自定义类public class EditDialog extends AlertDialog implements DialogInterface.OnClickListener
### 10、SurfaceView 介绍 
在布局文件里定义了 SurfaceView，用于显示摄像头的画面。 
SurfaceView 是用来画图（显示图像）的，SurfaceHolder 是一个接口，
用来控制 SurfaceView 的。 
SurfaceView 继承了 View 类。这个视图里内嵌了一个专门用于绘制的
Surface。你可以控制这个 Surface 的格式和尺寸。Surfaceview 控制这个
Surface 的绘制位置。（在 Android 中的 Surface 就是一个用来画图形
（graphics）或图像（image）的地方。Surface 是用通过 SurfaceView 才能展
示其中的内容。） 
SurfaceView 可以直接从内存或者 DMA 等硬件接口取得图像数据,是
个非常重要的绘图容器。它的特性是：可以在主线程之外的线程中向屏幕
绘图上。这样可以避免画图任务繁重的时候造成主线程阻塞，从而提高了
程序的反应速度。在游戏开发中多用到 SurfaceView，游戏中的背景、人物、
动画等等在画布 canvas 中画出。 
在新建一个 SurfaceView 时要通过 getHolder（）方法得到 SurfaceHolder，
并实现 surfaceHolder.addCallback（）接口，重写里面的 surfaceChanged（）
（意义：当 surface 的任何结构（格式或大小）发生改变，这个方法就立即
被调用。应该在此刻更新 surface。这个方法至少会被调用一次）、
surfaceCreated（）（意义：当 surface 第一次创建的时候，这个方法就会被
立即调用。这个方法的实现可以完成 surface 创建后的一些初始化工作）、
surfaceDestroyed（）（意义：在一个 surface 被销毁前，这个方法会被调用。
在这个调用返回后，你再也不应该去访问 surface 了）三个方法。 
#### 在surfaceCreated方法中开启捕获视频流的线程，并将surface对象传到获取视频流的地方，利用paint和canvas将视频流照片画到surface中
### Socket连接与Bitmap解码
   Socket msocket = new Socket();
   
   msocket.bind(null);//绑定一个名字
   
			/*调用bind()函数之后，为socket()函数创建的套接字关联一个相应地址，发送到这个地址的数据可以通过该套接字读取与使用。*/
   
   
			msocket.setSoTimeout(SOCKET_TIMEOUT);//超过10秒后
   
			msocket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);//address是ip地址，port是端口号
   
   
   在使用Socket来连接服务器时最简单的方式就是直接使用IP和端口，但Socket类中的connect方法并未提供这种方式，而是使用SocketAddress类来向connect方法传递服务器的IP和端口。
SocketAddress只是个抽象类，它除了有一个默认的构造方法外，其它的方法都是abstract的，因此，我们必须使用SocketAddress的子类来建立SocketAddress对象，也就是唯一的子类InetSocketAddress
			//obtain the bitmap
   
			InputStream inputStream =msocket.getInputStream();
   
   ///....................设置btye缓冲数据池的操作
   
   int readnum = inputStream.read(smallbuffer);//将输入流读入到smallbuffer中。readnum是获取的个数。
   
   System.arraycopy(smallbuffer, 0, mybuffer, num, readnum);//将smallbuffer复制到mybuffer中。mybuffer大小为14500540。与服务器转发时的大小一样
   //arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
			// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组； destPos:目的数组放置的起始位置； length:复制的长度。
   
   Bitmap mybitmap = BitmapFactory.decodeByteArray(mybuffer, 0, size);//获取网络图片、、可以在后面加上mybitmap.rec。。。。0：解码开始的位置，size：arraybyte的长度
   
### 11、对于Bitmap的使用回收 
　1) 要及时回收Bitmap的内存

　　Bitmap类有一个方法recycle()，从方法名可以看出意思是回收。这里就有疑问了，Android系统有自己的垃圾回收机制，可以不定期的回收掉不使用的内存空间，当然也包括Bitmap的空间。那为什么还需要这个方法呢?

　　Bitmap类的构造方法都是私有的，所以开发者不能直接new出一个Bitmap对象，只能通过BitmapFactory类的各种静态方法来实例化一个Bitmap。仔细查看BitmapFactory的源代码可以看到，生成Bitmap对象最终都是通过JNI调用方式实现的。所以，加载Bitmap到内存里以后，是包含两部分内存区域的。简单的说，一部分是Java部分的，一部分是C部分的。这个Bitmap对象是由Java部分分配的，不用的时候系统就会自动回收了，但是那个对应的C可用的内存区域，虚拟机是不能直接回收的，这个只能调用底层的功能释放。所以需要调用recycle()方法来释放C部分的内存。从Bitmap类的源代码也可以看到，recycle()方法里也的确是调用了JNI方法了的。

　　那如果不调用recycle()，是否就一定存在内存泄露呢?也不是的。Android的每个应用都运行在独立的进程里，有着独立的内存，如果整个进程被应用本身或者系统杀死了，内存也就都被释放掉了，当然也包括C部分的内存。

　　Android对于进程的管理是非常复杂的。简单的说，Android系统的进程分为几个级别，系统会在内存不足的情况下杀死一些低优先级的进程，以提供给其它进程充足的内存空间。在实际项目开发过程中，有的开发者会在退出程序的时候使用Process.killProcess(Process.myPid())的方式将自己的进程杀死，但是有的应用仅仅会使用调用Activity.finish()方法的方式关闭掉所有的Activity。

　　经验分享：

　　Android手机的用户，根据习惯不同，可能会有两种方式退出整个应用程序：一种是按Home键直接退到桌面;另一种是从应用程序的退出按钮或者按Back键退出程序。那么从系统的角度来说，这两种方式有什么区别呢?按Home键，应用程序并没有被关闭，而是成为了后台应用程序。按Back键，一般来说，应用程序关闭了，但是进程并没有被杀死，而是成为了空进程(程序本身对退出做了特殊处理的不考虑在内)。

　　Android系统已经做了大量进程管理的工作，这些已经可以满足用户的需求。个人建议，应用程序在退出应用的时候不需要手动杀死自己所在的进程。对于应用程序本身的进程管理，交给Android系统来处理就可以了。应用程序需要做的，是尽量做好程序本身的内存管理工作。
 一般来说，如果能够获得Bitmap对象的引用，就需要及时的调用Bitmap的recycle()方法来释放Bitmap占用的内存空间，而不要等Android系统来进行释放。

　　下面是释放Bitmap的示例代码片段。

　　// 先判断是否已经回收

　　if(bitmap != null && !bitmap.isRecycled()){

　　// 回收并且置为null

　　bitmap.recycle();

　　bitmap = null;

　　}

　　System.gc();

　　从上面的代码可以看到，bitmap.recycle()方法用于回收该Bitmap所占用的内存，接着将bitmap置空，最后使用System.gc()调用一下系统的垃圾回收器进行回收，可以通知垃圾回收器尽快进行回收。这里需要注意的是，调用System.gc()并不能保证立即开始进行回收过程，而只是为了加快回收的到来。
  2) 捕获异常

　　因为Bitmap是吃内存大户，为了避免应用在分配Bitmap内存的时候出现OutOfMemory异常以后Crash掉，需要特别注意实例化Bitmap部分的代码。通常，在实例化Bitmap的代码中，一定要对OutOfMemory异常进行捕获。

　　以下是代码示例。

　　Bitmap bitmap = null;

　　try {

　　// 实例化Bitmap

　　bitmap = BitmapFactory.decodeFile(path);

　　} catch (OutOfMemoryError e) {

　　//

　　}

　　if (bitmap == null) {

　　// 如果实例化失败 返回默认的Bitmap对象

　　return defaultBitmapMap;

　　}

　　这里对初始化Bitmap对象过程中可能发生的OutOfMemory异常进行了捕获。如果发生了OutOfMemory异常，应用不会崩溃，而是得到了一个默认的Bitmap图。

　　经验分享：

　　很多开发者会习惯性的在代码中直接捕获Exception。但是对于OutOfMemoryError来说，这样做是捕获不到的。因为OutOfMemoryError是一种Error，而不是Exception。在此仅仅做一下提醒，避免写错代码而捕获不到OutOfMemoryError。

　　3) 缓存通用的Bitmap对象

　　有时候，可能需要在一个Activity里多次用到同一张图片。比如一个Activity会展示一些用户的头像列表，而如果用户没有设置头像的话，则会显示一个默认头像，而这个头像是位于应用程序本身的资源文件中的。

　　如果有类似上面的场景，就可以对同一Bitmap进行缓存。如果不进行缓存，尽管看到的是同一张图片文件，但是使用BitmapFactory类的方法来实例化出来的Bitmap，是不同的Bitmap对象。缓存可以避免新建多个Bitmap对象，避免内存的浪费。

　　经验分享：

　　Web开发者对于缓存技术是很熟悉的。其实在Android应用开发过程中，也会经常使用缓存的技术。这里所说的缓存有两个级别，一个是硬盘缓存，一个是内存缓存。比如说，在开发网络应用过程中，可以将一些从网络上获取的数据保存到SD卡中，下次直接从SD卡读取，而不从网络中读取，从而节省网络流量。这种方式就是硬盘缓存。再比如，应用程序经常会使用同一对象，也可以放到内存中缓存起来，需要的时候直接从内存中读取。这种方式就是内存缓存。

　　4) 压缩图片

　　如果图片像素过大，使用BitmapFactory类的方法实例化Bitmap的过程中，需要大于8M的内存空间，就必定会发生OutOfMemory异常。这个时候该如何处理呢?如果有这种情况，则可以将图片缩小，以减少载入图片过程中的内存的使用，避免异常发生。

　　使用BitmapFactory.Options设置inSampleSize就可以缩小图片。属性值inSampleSize表示缩略图大小为原始图片大小的几分之一。即如果这个值为2，则取出的缩略图的宽和高都是原始图片的1/2，图片的大小就为原始大小的1/4。

　　如果知道图片的像素过大，就可以对其进行缩小。那么如何才知道图片过大呢?

　　使用BitmapFactory.Options设置inJustDecodeBounds为true后，再使用decodeFile()等方法，并不会真正的分配空间，即解码出来的Bitmap为null，但是可计算出原始图片的宽度和高度，即options.outWidth和options.outHeight。通过这两个值，就可以知道图片是否过大了。

　　BitmapFactory.Options opts = new BitmapFactory.Options();

　　// 设置inJustDecodeBounds为true

　　opts.inJustDecodeBounds = true;

　　// 使用decodeFile方法得到图片的宽和高

　　BitmapFactory.decodeFile(path, opts);

　　// 打印出图片的宽和高

　　Log.d("example", opts.outWidth + "," + opts.outHeight);

　　在实际项目中，可以利用上面的代码，先获取图片真实的宽度和高度，然后判断是否需要跑缩小。如果不需要缩小，设置inSampleSize的值为1。如果需要缩小，则动态计算并设置inSampleSize的值，对图片进行缩小。需要注意的是，在下次使用BitmapFactory的decodeFile()等方法实例化Bitmap对象前，别忘记将opts.inJustDecodeBound设置回false。否则获取的bitmap对象还是null。

　　经验分享：

　　如果程序的图片的来源都是程序包中的资源，或者是自己服务器上的图片，图片的大小是开发者可以调整的，那么一般来说，就只需要注意使用的图片不要过大，并且注意代码的质量，及时回收Bitmap对象，就能避免OutOfMemory异常的发生。

　　如果程序的图片来自外界，这个时候就特别需要注意OutOfMemory的发生。一个是如果载入的图片比较大，就需要先缩小;另一个是一定要捕获异常，避免程序Crash。
  
  ### 12、保存视频流中的一个照片
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
