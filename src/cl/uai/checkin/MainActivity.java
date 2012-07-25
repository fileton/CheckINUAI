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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {
	
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
	
	EditText idEdit;
	ProgressBar spinner;
	Button irAAdmin;
	
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
					es();
					dialog.cancel();
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
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
				mensaje = "ÀQuieres confirmar la asistencia a " + nombre_clase + " a las " + hora_clase + "?";
				alert("1", nombre, mensaje);
				}
				
			}
			else
			{mensaje = "No tienes clases ahora.";
			alert("0", nombre, mensaje);}
			
			
		}
		
	}
	
	//funcion para cuando verificar esta bien
		public void es(){
			sql_checkins checkin = new sql_checkins(this);
			checkin.open();
			checkin.checkIn(id_clase);
			checkin.close();
			
			alert("0", nombre, "Se ha confirmado la clase.");
			
			if(isOnline()){
				new SubirClases().execute();
			}
		}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        readQr();
        
        Button CheckInButton = (Button) findViewById(R.id.button1);
        Button Scan = (Button) findViewById(R.id.bScan);
        irAAdmin = (Button) findViewById(R.id.bGotoAdmin);
        
        //new sendClasesLoop().execute();
        
        //cuando hace click
		CheckInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				idEdit = (EditText)findViewById(R.id.editText1);
		        id= idEdit.getText().toString();
		        startVerificar();
		        
			}
		}); 
		
		Scan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				readQr();
			}
		});
		
		irAAdmin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent("cl.uai.checkin.ADMIN"));
			}
		});
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
 	   if (requestCode == 0) {
 	      if (resultCode == RESULT_OK) {
 	         String contents = intent.getStringExtra("SCAN_RESULT");
 	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
 	         	id= contents;
 			    startVerificar();
 	      } else if (resultCode == RESULT_CANCELED) {
 	         
 	      }
 	   }
 	}
    
    public void readQr(){
    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
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
	    		   subir_checkin(id_clase);
	    		   sql.guardarSubida(id_clase);
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
			catch (ClientProtocolException e) {} 
			catch (IOException e) {} 
		}
	 }
}
