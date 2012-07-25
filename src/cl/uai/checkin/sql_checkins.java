package cl.uai.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql_checkins {
	public static final String KEY_ROWID = "id";
	public static final String KEY_ESTADO = "estado";
	public static final String KEY_SUBIDO = "subido";
	
	public static final String DATABASE_NAME = "CheckInUai_CheckIns_DB";
	public static final String DATABASE_TABLE = "Checkins";
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
					KEY_ESTADO + " TEXT NOT NULL, " + 
					KEY_SUBIDO + " TEXT NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public sql_checkins(Context c){
		ourContext = c;
	}
	
	public sql_checkins open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}

	public long checkIn(String id_clase) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id_clase);
		cv.put(KEY_ESTADO, "Si");
		cv.put(KEY_SUBIDO, "No");
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}
	
	public String[] getCheckInsNoSubidos() {
		String[] columns = new String[]{ KEY_ROWID, KEY_SUBIDO};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_SUBIDO + "= \"No\"", null, null, null, null);
		String result[] = new String[c.getCount()];
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int i = 0;
		if(c != null && !c.isAfterLast()){
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			result[i] = c.getString(iRow);
			i++;
		}
		}
		c.close();
		
		return result;
	}
	
	public void guardarSubida(String id_clase){
		String exe = "Update " + DATABASE_TABLE + " Set " + KEY_SUBIDO + " = \"Si\" WHERE " + KEY_ROWID + "=" + id_clase;
		ourDatabase.execSQL(exe);
	}
	
	public String getEstadoDeClase(String l){
		String[] columns = new String[]{ KEY_ROWID, KEY_ESTADO};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + l, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String estado = c.getString(1);
			c.close();
			return estado;
		}
		c.close();
		return null;
	}
	
	public void deleteDatabase(){
		ourDatabase.delete(DATABASE_TABLE, null, null);
	}
}
