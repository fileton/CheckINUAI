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
	

	public void startVerificar(){
		
			String mensaje = null;
			
			Sql_horarios db = new Sql_horarios(this);
			db.open();
			if(id_clase!=null)
				{
					nombre_clase = db.getNombreDeClase(id_clase);
					hora_clase = db.getHoraDeClase(id_clase);
				}
			db.close();
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
        
        Button ActualizarButton = (Button) findViewById(R.id.bActualizar);
		
		ActualizarButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actualizarDatos();
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
    
    public boolean getEstadoPorIdProfesor(String id_clase){
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
	   int[] clasesId = sql.buscarHorarios(search.getText().toString());
	   sql.close();
	   
	   String[] nombre;
	   nombre = new String[clasesId.length];
	   
	   String[] nombreProfesor;
	   nombreProfesor = new String[clasesId.length];
	   
	   Button[] btnTag;
	   btnTag = new Button[clasesId.length];
	
	   int i = 0;
	   int j = 0;
	   while(i < clasesId.length) {
		   if(j == 8)
		   {break;}
		   
		   if(getEstadoPorIdProfesor(clasesId[i] + "")){
			   clasesId[i] = 0;
		   }
		   
		   if(clasesId[i] != 0){
		   
	       row = new LinearLayout(this);
	       btnTag[i] = new Button(this);
	       btnTag[i].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	       
	       sql = new Sql_horarios(MainActivity.this);
		   sql.open();
		   nombre[i] = sql.getNombreDeClase(clasesId[i] + "");
		   nombreProfesor[i] = sql.getNombreProfesor(clasesId[i] + "");
		   sql.close();
	       btnTag[i].setText(nombre[i] + " - " + nombreProfesor[i]);
	       btnTag[i].setTextSize(20);
	       btnTag[i].setId(clasesId[i]);
	       
	       btnTag[i].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				id_clase = Integer.toString(v.getId());
		        startVerificar();
		        EditText search = (EditText)findViewById(R.id.editText1);
				search.setText("");
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
				postValues.add(new BasicNameValuePair("id_clase", id_clase));             
		 
				httpPost.setEntity(new UrlEncodedFormEntity(postValues));          
		 
				//Hace la petici—n         
				httpClient.execute(httpPost);
			} 
			catch (ClientProtocolException e) {networkError=true;} 
			catch (IOException e) {networkError=true;} 
		}
	 }
    
    
    //actualizar datos
    public void actualizarDatos(){
	    new GuardarDatos().execute();
    }
    
    class GuardarDatos extends AsyncTask<String, String, Void>
	{
	    InputStream is = null ;
	    String result = "";
	    protected void onPreExecute() {
	       
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
						Log.e("log_tag", "Error converting result "+e.toString());
					}
			
		// ambil data dari Json database
					try {
						Sql_horarios delete = new Sql_horarios(MainActivity.this);
						delete.open();
						delete.deleteDatabase();
						delete.close();
						
						JSONArray Jarray = new JSONArray(result);
						for(int i=0;i<Jarray.length();i++)
						{
						JSONObject Jasonobject = null;
						Jasonobject = Jarray.getJSONObject(i);

						//get an output on the screen
						int id = Jasonobject.getInt("ramo_id");
						String id_profe = Jasonobject.getString("profesor_rut");
						String nombre = Jasonobject.getString("ramo_nombre");
						String hora = Jasonobject.getString("modulo_hora_inicio");
						String nombre_profe = Jasonobject.getString("profesor_nombre");
						
						//guardar al sql
						boolean didItWork = true;
						try{
						
						Sql_horarios entry = new Sql_horarios(MainActivity.this);
						entry.open();
						entry.creatyEntry(id, id_profe, nombre, hora, nombre_profe);
						Log.e("Clase Guardada", id + " " + nombre + " " + hora + " " + nombre_profe);
						entry.close();
						}catch (Exception e){
							didItWork = false;
						}finally{
							if(didItWork){
								
							}
						}
						//se termina de guardar
						
						}
					} catch (Exception e) {
						Log.e("log_tag", "Error parsing data "+e.toString());
					}
		
		
				return null;

			}
		protected void onPostExecute(Void v) {
			configurarTabla();
		}
	  }
}
