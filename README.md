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
