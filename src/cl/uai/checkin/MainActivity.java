package cl.uai.checkin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.uai.checkin.Admin.SubirClases;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity{
	
	private static final String[] String = null;
	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb=null;
	
	//las id del profe y de la clase y etc
	String id;
	String id_clase;
	String nombre = null;
	String nombre_clase = null;
	String hora_clase = null;
	
	boolean networkError;
	
	LinearLayout row;
	
	EditText idEdit;
	ProgressBar spinner;
	Button irAAdmin;
	Button irAPaint;
	
	final Context context = this;
	
	//Alert para verificar
	public void alert(String tipo, String titulo, String texto){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(titulo);
		alertDialogBuilder
			.setMessage(texto);
		
		if(tipo == "1"){
			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Si",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					//respuesta es si
					dialog.cancel();
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					//respuesta es no
					dialog.cancel();
				}
			});
		}
		else if(tipo=="2"){
				Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
		}
		else{
			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			  });
		}
			if(tipo != "2"){
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();}
	}
	
	public boolean isOnline(){
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
		    return true;
		} else {
		    return false;
		} 
	}
	
	private String getNombre(String id2) {
		long l = 0;
		try {
	         l = Long.parseLong(id2);
	      } catch (NumberFormatException nfe) {
	      }
		sql nombre = new sql(this);
		nombre.open();
		String returned = nombre.getName(l);
		nombre.close();
		
		return returned;
	}
	

	public void startVerificar(){
		
		nombre = getNombre(id);
		if(nombre == null)
			{alert("0", "Error", "No se encontro al profesor");}
		else{
			String mensaje = null;
			
			Sql_horarios db = new Sql_horarios(this);
			db.open();
			id_clase = db.getClase_id(id);
			if(id_clase!=null)
				{
					nombre_clase = db.getNombreDeClase(id_clase);
					hora_clase = db.getHoraDeClase(id_clase);
				}
			db.close();
			
			if(id_clase!=null)
			{
				sql_checkins sqlestado = new sql_checkins(this);
				sqlestado.open();
				String estado = sqlestado.getEstadoDeClase(id_clase);
				sqlestado.close();
				if(estado != null)
				{alert("0", nombre, "La clase " + nombre_clase + " ya fue confirmada.");}
				else{
				//ir a confirmar la clase
				Intent intent=new Intent("cl.uai.checkin.CHECKIN");
				intent.putExtra("nombre",nombre);
				intent.putExtra("nombre_clase",nombre_clase);
				intent.putExtra("id_clase",id_clase);
				intent.putExtra("id_profe",id);
				intent.putExtra("hora_clase",hora_clase);
				startActivity(intent);
				}
				
			}
			else
			{mensaje = "No tienes clases ahora.";
			alert("0", nombre, mensaje);}
			
			
		}
		
	}
	
    @Override
	protected void onPostResume() {
    	if(isOnline()){
			new SubirClases().execute();
		}
		super.onPostResume();
		configurarTabla();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button CheckInButton = (Button) findViewById(R.id.button1);
        
        //cuando hace click
		CheckInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				idEdit = (EditText)findViewById(R.id.editText1);
		        id= idEdit.getText().toString();
		        startVerificar();
		        
			}
		});  
		
		
		EditText TextView = (EditText)findViewById(R.id.editText1);
		TextView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				configurarTabla();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {}
		});
    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Admin");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
        	startActivity(new Intent("cl.uai.checkin.ADMIN"));
            return true;
        }
        return false;
    }
    public boolean getEstadoPorIdProfesor(String id_profesor){
    	
    	Sql_horarios db = new Sql_horarios(this);
		db.open();
		id_clase = db.getClase_id(id_profesor);
		if(id_clase == null)
			{return true;}
		db.close();
		
		sql_checkins sqlestado = new sql_checkins(this);
		sqlestado.open();
		String estado = sqlestado.getEstadoDeClase(id_clase);
		sqlestado.close();
		if(estado != null)
		{return true;}
    	return false;
    }
    
   public void configurarTabla()
   {
	   LinearLayout layout = (LinearLayout) findViewById(R.id.tableLayout);
	   layout.setOrientation(LinearLayout.VERTICAL);
	   
	   layout.removeAllViewsInLayout();

	   Sql_horarios sql = new Sql_horarios(MainActivity.this);
	   sql.open();
	   EditText search = (EditText)findViewById(R.id.editText1);
	   int[] profesoresId = sql.buscarHorarios(search.getText().toString());
	   sql.close();
	   
	   String[] nombre;
	   nombre = new String[profesoresId.length];
	   
	   Button[] btnTag;
	   btnTag = new Button[profesoresId.length];
	
	   int i = 0;
	   int j = 0;
	   while(i < profesoresId.length) {
		   if(j == 7)
		   {break;}
		   
		   if(getEstadoPorIdProfesor(profesoresId[i] + "")){
			   profesoresId[i] = 0;
		   }
		   
		   if(profesoresId[i] != 0){
		   
	       row = new LinearLayout(this);
	       btnTag[i] = new Button(this);
	       btnTag[i].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	       
	       sql sql2 = new sql(MainActivity.this);
	       sql2.open();
		   nombre[i] = sql2.getName(profesoresId[i]);
		   sql2.close();
	       btnTag[i].setText(nombre[i]);
	       btnTag[i].setId(profesoresId[i]);
	       
	       btnTag[i].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        id= Integer.toString(v.getId());
		        startVerificar();
			}
	       });
	       
	       row.addView(btnTag[i]);
	       layout.addView(row);
	       
	       j++;
		   }
	       i++;
	   }
   }
    
    
    class SubirClases extends AsyncTask<String, String, Void>
	{
	private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
	    protected void onPreExecute() {
	     }
	       @Override
		protected Void doInBackground(String... params) {
	    	   sql_checkins sql = new sql_checkins(MainActivity.this);
	    	   sql.open();
	    	   String[] array = sql.getCheckInsNoSubidos();
	    	   sql.close();
	    	   for(String id_clase: array){
	    		   sql.open();
	    		   networkError=false;
	    		   subir_checkin(id_clase);
	    		   if(!networkError){
	    		   sql.guardarSubida(id_clase);}
	    		   sql.close();
	    	   }
	    	   
			return null;
			}
		protected void onPostExecute(Void v) {
		}
		protected void subir_checkin(String id_clase){
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://lopezjullian.com/checkinuai/confirm.php");
			
			try {         
				//A–ade las variables a enviar por post         
				List<NameValuePair> postValues = new ArrayList<NameValuePair>(1);         
				postValues.add(new BasicNameValuePair("id", id_clase));             
		 
				httpPost.setEntity(new UrlEncodedFormEntity(postValues));          
		 
				//Hace la petici—n         
				httpClient.execute(httpPost);              
			} 
			catch (ClientProtocolException e) {networkError=true;} 
			catch (IOException e) {networkError=true;} 
		}
	 }
}
