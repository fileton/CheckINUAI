package cl.uai.checkin;

import cl.uai.checkin.MainActivity.SubirClases;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CheckIn extends Activity{
	
	PaintHelper drawView;
	
	String nombre;
	String nombre_clase;
	String id_clase;
	String id_profe;
	String hora_clase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
        	nombre = extras.getString("nombre");
        	nombre_clase = extras.getString("nombre_clase");
        	id_clase = extras.getString("id_clase");
        	id_profe = extras.getString("id_profe");
        	hora_clase = extras.getString("hora_clase");
        }

        drawView = new PaintHelper(this);
        setContentView(drawView);
        drawView.requestFocus();

        
        Button c = new Button(this);
        c.setText("CheckIn!");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 50, 0, 0); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(c,params);
        
        TextView h = new TextView(this);
        h.setText(hora_clase);
        h.setTextSize(12);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 10, 0, 0); //left, top, right, bottom
        getWindow().addContentView(h, params);
        
        TextView nc = new TextView(this);
        nc.setText(nombre_clase);
        nc.setTextSize(28);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 25, 0, 0); //left, top, right, bottom
        getWindow().addContentView(nc, params);
        
        TextView n = new TextView(this);
        n.setText(nombre);
        n.setTextSize(14);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 75, 0, 0); //left, top, right, bottom
        getWindow().addContentView(n, params);
        
        View line1 = new View(this);
        line1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        line1.setBackgroundColor(Color.rgb(51, 51, 51));
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.TOP;
        params.setMargins(0, 110, 0, 0); //left, top, right, bottom
        getWindow().addContentView(line1, params);
        
        TextView f = new TextView(this);
        f.setText("Firme aqu’:");
        f.setTextSize(12);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 120, 0, 0); //left, top, right, bottom
        getWindow().addContentView(f, params);
        
        View line2 = new View(this);
        line2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        line2.setBackgroundColor(Color.rgb(51, 51, 51));
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.BOTTOM;
        params.setMargins(0, 0, 0, 100); //left, top, right, bottom
        getWindow().addContentView(line2, params);
        
        c.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkIn();
			}
		});
    }
    
    public void checkIn(){
    	sql_checkins checkin = new sql_checkins(this);
		checkin.open();
		checkin.checkIn(id_clase);
		checkin.close();
		
		Toast.makeText(this, "Se ha confirmado la clase " + nombre_clase, Toast.LENGTH_LONG).show();
		finish();
    }
}
