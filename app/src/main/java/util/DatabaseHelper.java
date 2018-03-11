package util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.gsr_pc.myproject0324.R;

import java.util.ArrayList;
import java.util.List;

import config.CamMonitorParameter;


public class DatabaseHelper {

	
	static class Helper extends SQLiteOpenHelper {
		protected final static String TAG ="DatabaseHelper";
		private final static String DATABASE_NAME="CAMMONITOR_CLIENT";
		private final static int DATABASE_VERSION = 1;
		private Context context;

		public Helper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);  
			this.context = context;
		}

		
		
		public void onCreate(SQLiteDatabase db) {//当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。

			try{
				String sql =context.getString(R.string.table_sql);
				db.execSQL(sql);
				Log.i(TAG, sql);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。
			String sql = context.getString(R.string.drop_sql);
			db.execSQL(sql);
			this.onCreate(db);
			
		}
		
	}

	private Context context;
	protected SQLiteDatabase db;
	public DatabaseHelper(Context context) {
		this.context = context;
		db = new Helper(context).getWritableDatabase();//新建数据库，获取连接
	}

	
	public static CamMonitorParameter query(Context context,int id) throws Exception{
		//根据id查询出连接信息
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			String[] columns = new String[]{"uid","name","ip","port","port1","password","client_dir","connect_type"};
			Cursor cursor =	db.query("tb_cammonitor_configs", columns, whereClause, whereArgs, null, null, null);
			if(cursor.getCount() == 0){
				throw new Exception("没有找到ID"+id+"的数据");
			}
			cursor.moveToFirst();
			CamMonitorParameter param = new CamMonitorParameter();
			param.setId(id);
			param.setUid(cursor.getInt(0));//获取用户id
			param.setName(cursor.getString(1));
			param.setIp(cursor.getString(2));
			param.setPort(cursor.getInt(3));
			param.setPort1(cursor.getString(4));
			param.setPassword(cursor.getString(5));
			param.setLocal_dir(cursor.getString(6));
			return param;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}

	
	

	public static long insert(Context context,String table,ContentValues values) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();//getWritableDatabase只会返回一个\数据库SQLiteDatabase对象
			long num = db.insert(table, null, values);
			//当values参数为空或者里面没有内容的时候，
			// 我们insert是会失败的（底层数据库不允许插入一个空行），
			// 为了防止这种情况，我们要在这里指定一个列名，到时
			// 候如果发现将要插入的行为空行时，就会将你指定的这个列名的值设为null，然后再向数据库中插入。
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public static long update(Context context,String table,ContentValues values,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			long num = db.update(table, values, whereClause, whereArgs);
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	
	
	public static void testDelete(Context context){
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();
			db.execSQL("delete from tb_cammonitor_configs;");
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void delete(Context context,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();//getWritableDatabase只会返回一个数据库SQLiteDatabase对象
			String table = "tb_cammonitor_configs";  
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			db.delete(table, whereClause, whereArgs);
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void drop(Context context){
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String sql =context.getString(R.string.drop_sql);
			db.execSQL(sql);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
//	public static int getCount(Context context ,String table) throws Exception{
//		SQLiteDatabase db =  null;
//		try{
//			db = new Helper(context).getReadableDatabase();
//
//			String whereClause = " uid = ?";
//
//			String[] whereArgs = new String[] {String.valueOf(id)};
////			String[] columns = new String[]{
////					"uid","name","ip","port","username","password","client_dir","connect_type"
////			};
//
////			Cursor cursor =	db.query("tb_cammonitor_configs", columns, whereClause, whereArgs, null, null, null);
//
//			Cursor cur = db.query(table, new String[]{"_id","name"}, whereClause, whereArgs, null, null, null);
//			//query(表名，返回的列名，where语句，where字句对应的值，分组方式若为空则不分组，)
//			return cur.getCount();
//		}catch (Exception e) {
//			throw e;
//		}finally{
//			if(db!=null){
//				db.close();
//			}
//		}
//	}
	
	public Cursor loadAllName(Integer uid) throws Exception{
		try{
			String whereClause = " uid = ?";
			String[] whereArgs = new String[] {String.valueOf(uid)};
			//根据用户id查出此用户下的数据信息
			Cursor cur = db.query("tb_cammonitor_configs", new String[]{"_id","name","port","port1"}, whereClause, whereArgs, null, null, "_id DESC");
			System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}
	
	
	public void close(){
		if(this.db!=null){
			this.db.close();
		}
	}
	
	
	public static List<String> loadName(Context context) throws Exception{
		SQLiteDatabase db =  null;
		List<String> rst = new ArrayList<String>();
		try{
			db = new Helper(context).getReadableDatabase();
			Cursor cur =db.query("tb_cammonitor_configs", new String[]{"_id","name"}, null, null, null, null, null);
			cur.moveToFirst();  
			for (int i = 0; i < cur.getCount(); i++) {  
				String s = cur.getString(1);  
				rst.add(s);
				cur.moveToNext();  
			}  
			return rst;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public Cursor query(int id) throws Exception{
		try{
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			Cursor cur = db.query("tb_cammonitor_configs", new String[]{"_id","name","ip","port","port1","password","client_dir"}, whereClause, whereArgs, null, null, "_id DESC");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}
}
