package com.demouai;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
public class DemoCheckinUaiActivity extends Activity implements TextWatcher, OnItemClickListener, OnClickListener{
ListView list;
AutoCompleteTextView myAutoComplete;
String item2[]={
		"Alvarez","Araya","Navarro","Negrete",
		"Barros","Brown","Estrada","Enriquez"
};
String item[]={
  "Giadach", "Guzman", "Martinez", "Mena",
  "Lopez", "Lobos", "Diaz", "Diego",
  "Garcia", "Medrano", "Leyton", "Daddy"
};

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       list = (ListView)findViewById(R.id.list);
       list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,item));
      //list.setOnItemClickListener(this);
       myAutoComplete = (AutoCompleteTextView)findViewById(R.id.myautocomplete);
      
       myAutoComplete.addTextChangedListener(this);
       myAutoComplete.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item));
      
   }
/*public void OnItemClickListener(View vista){
if(vista.getId()== findViewById(R.id.list).getId())
{
	Intent i = new Intent(this, pag2Activity.class);
	startActivity(i);
}
}*/
@Override
public void afterTextChanged(Editable arg0) {
 // TODO Auto-generated method stub

}

@Override
public void beforeTextChanged(CharSequence s, int start, int count,
  int after) {
 // TODO Auto-generated method stub

}

@Override
public void onTextChanged(CharSequence s, int start, int before, int count) {
 // TODO Auto-generated method stub

}

public void onClick(View arg0) {
	// TODO Auto-generated method stub
}
@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	// TODO Auto-generated method stub
	
}
}