package cl.uai.checkin;

import java.util.concurrent.CountDownLatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql {
	public static final String KEY_ROWID = "id";
	public static final String KEY_NAME = "nombre";
	
	public static final String DATABASE_NAME = "CheckInUai_DB";
	public static final String DATABASE_TABLE = "Profesores";
	public static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY, " +
					KEY_NAME + " TEXT NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public sql(Context c){
		ourContext = c;
	}
	
	public sql open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}

	public long creatyEntry(int id, String nombre) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_NAME, nombre);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}

	public String getData() {
		String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		String result = "";
		
		int iId = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_NAME);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			result = result + c.getString(iId) + " " + c.getString(iName) + "\n";
		}
		c.close();
		return result;
	}
	
	public int[] getProfesoresId(){
		String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

		int[] result;
		result = new int[c.getCount()];
		
		int iId = c.getColumnIndex(KEY_ROWID);
		
		int i = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			result[i]= c.getInt(iId);
			i++;
		}
		c.close();
		return result;
	}
	
	public String getName(long l){
		String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + l, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String name = c.getString(1);
			c.close();
			return name;
		}
		c.close();
		return null;
	}
	
	public void deleteDatabase(){
		ourDatabase.delete(DATABASE_TABLE, null, null);
	}
}
