# ShiPinJK
### SurfaceView 介绍 
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
