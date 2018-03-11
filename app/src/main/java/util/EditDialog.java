package util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gsr_pc.myproject0324.R;
import com.example.gsr_pc.myproject0324.ZhuActivity;



/**
 *
 *实现带editext的AlertDialog
 *按照此例子，可以实现任意格式的AlertDialog
 */
public class EditDialog extends AlertDialog implements DialogInterface.OnClickListener{//设置对话框
//
	public final static String aa = "ZhuActivity";
	 private String text = "";
	 private EditText edit;
	 private OnDataSetListener mCallback;
	 private LinearLayout layout;
	/**
	 *构造一个回调接口
	 */

	public interface OnDataSetListener{
		void onDataSet(String text);
	}
	public EditDialog(Context context, String title, String value, OnDataSetListener callback) {
			super(context);

	        mCallback = callback;
	        TextView label = new TextView(context);//设置文本框
	        label.setText("hint");
	        edit = new EditText(context);//设置编辑框
	        edit.setText(value);
	        edit.setPadding(30, 0, 0, 0);
		//自定义对话框样式
	        layout = new LinearLayout(context);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(400,50);
	        param2.leftMargin=30;
	        layout.addView(edit, param2);

	        setView(layout);//给对话框设置自定义样式

	        setTitle(title);
	        setButton(context.getText(R.string.btn_ok), this);
	        setButton2(context.getText(R.string.btn_cancle), (OnClickListener) null);
	}

	public void onClick(DialogInterface dialog, int which) {

		text = edit.getText().toString();//连接名

		Log.d(ZhuActivity.TAGaa, "U click text=" + text);
		if (mCallback != null)
			mCallback.onDataSet(text);

	}

}
