package cl.uai.checkin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Admin extends Activity {

	Button DescargarDatos;
	
	public boolean isOnline(){
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
		    return true;
		} else {
		    return false;
		} 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin);
		
		DescargarDatos = (Button) findViewById(R.id.bSync);
		DescargarDatos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isOnline()){
					new GuardarDatos().execute();}
				else{
					Toast.makeText(getApplicationContext(),"No hay internet", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	class GuardarDatos extends AsyncTask<String, String, Void>
	{
	private ProgressDialog progressDialog = new ProgressDialog(Admin.this);
	    InputStream is = null ;
	    String result = "";
	    protected void onPreExecute() {
	       progressDialog.setMessage("Descargando Profesores...");
	       progressDialog.show();
	     }
	       @Override
		protected Void doInBackground(String... params) {
		  String url_select = "http://lopezjullian.com/checkinuai/descargar_profesores.php";

		  HttpClient httpClient = new DefaultHttpClient();
		  HttpPost httpPost = new HttpPost(url_select);

	          ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		    try {
			httpPost.setEntity(new UrlEncodedFormEntity(param));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			//read content
			is =  httpEntity.getContent();					

			} catch (Exception e) {
			Log.e("log_tag", "Error in http connection "+e.toString());
			}
		try {
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while((line=br.readLine())!=null)
			{
			   sb.append(line+"\n");
			}
				is.close();
				result=sb.toString();				

					} catch (Exception e) {
						// TODO: handle exception
						Log.e("log_tag", "Error converting result "+e.toString());
					}

				return null;

			}
		protected void onPostExecute(Void v) {

			// ambil data dari Json database
			try {
				sql delete = new sql(Admin.this);
				delete.open();
				delete.deleteDatabase();
				delete.close();
				
				JSONArray Jarray = new JSONArray(result);
				for(int i=0;i<Jarray.length();i++)
				{
				JSONObject Jasonobject = null;
				Jasonobject = Jarray.getJSONObject(i);

				//get an output on the screen
				int id = Jasonobject.getInt("id");
				String name = Jasonobject.getString("nombre");
				
				//guardar al sql
				boolean didItWork = true;
				try{
				
				sql entry = new sql(Admin.this);
				entry.open();
				entry.creatyEntry(id, name);
				entry.close();
				}catch (Exception e){
					didItWork = false;
				}finally{
					if(didItWork){
						
					}
				}
				//se termina de guardar
				
				}
				this.progressDialog.dismiss();
				new GuardarDatos2().execute();

			} catch (Exception e) {
				// TODO: handle exception
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
		}
	    }
	
	class GuardarDatos2 extends AsyncTask<String, String, Void>
	{
	private ProgressDialog progressDialog = new ProgressDialog(Admin.this);
	    InputStream is = null ;
	    String result = "";
	    protected void onPreExecute() {
	       progressDialog.setMessage("Descargando Clases...");
	       progressDialog.show();
	     }
	       @Override
		protected Void doInBackground(String... params) {
		  String url_select = "http://lopezjullian.com/checkinuai/descargar_clases.php";

		  HttpClient httpClient = new DefaultHttpClient();
		  HttpPost httpPost = new HttpPost(url_select);

	          ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		    try {
			httpPost.setEntity(new UrlEncodedFormEntity(param));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			//read content
			is =  httpEntity.getContent();					

			} catch (Exception e) {
			Log.e("log_tag", "Error in http connection "+e.toString());
			}
		try {
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while((line=br.readLine())!=null)
			{
			   sb.append(line+"\n");
			}
				is.close();
				result=sb.toString();				

					} catch (Exception e) {
						// TODO: handle exception
						Log.e("log_tag", "Error converting result "+e.toString());
					}

				return null;

			}
		protected void onPostExecute(Void v) {

			// ambil data dari Json database
			try {
				Sql_horarios delete = new Sql_horarios(Admin.this);
				delete.open();
				delete.deleteDatabase();
				delete.close();
				
				JSONArray Jarray = new JSONArray(result);
				for(int i=0;i<Jarray.length();i++)
				{
				JSONObject Jasonobject = null;
				Jasonobject = Jarray.getJSONObject(i);

				//get an output on the screen
				int id = Jasonobject.getInt("id");
				String id_profe = Jasonobject.getString("id_profe");
				String nombre = Jasonobject.getString("nombre");
				String hora = Jasonobject.getString("hora");
				
				//guardar al sql
				boolean didItWork = true;
				try{
				
				Sql_horarios entry = new Sql_horarios(Admin.this);
				entry.open();
				entry.creatyEntry(id, id_profe, nombre, hora);
				entry.close();
				}catch (Exception e){
					didItWork = false;
				}finally{
					if(didItWork){
						
					}
				}
				//se termina de guardar
				
				}
				this.progressDialog.dismiss();
				new SubirClases().execute();

			} catch (Exception e) {
				// TODO: handle exception
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
		}
	    }
	
	class SubirClases extends AsyncTask<String, String, Void>
	{
	private ProgressDialog progressDialog = new ProgressDialog(Admin.this);
	    protected void onPreExecute() {
	       progressDialog.setMessage("Subiendo CheckIns...");
	       progressDialog.show();
	     }
	       @Override
		protected Void doInBackground(String... params) {
	    	   sql_checkins sql = new sql_checkins(Admin.this);
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

				
				this.progressDialog.dismiss();

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
