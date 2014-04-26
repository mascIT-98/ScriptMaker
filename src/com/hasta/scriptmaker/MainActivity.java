package com.hasta.scriptmaker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText value=null;
	private EditText path=null;
    private Button btn=null;
    
    //Create view, set edittext and button on clicklistener
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn=(Button)findViewById(R.id.send);
        value=(EditText)findViewById(R.id.write);
        path=(EditText)findViewById(R.id.path);
	
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	makescript();
            }
        });
}
	//Inizialize inflater menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch(item.getItemId())
		    {
		    case R.id.delete:
		    	Utils.mountSystemRW();
		    	Utils.mRunAsSU("rm /system/etc/init.d/script");
		        break;
		    case R.id.info:
		    	ShowToast("ScriptMaker 1.1");
		        break;
		    case R.id.guide:
		    	new AlertDialog.Builder(this)
		        .setTitle("How to use?")
		        .setMessage("Insert a value to your echo script. For example you want to set the deepest sleep state to 4, so write into the first editText 4 and into the second the path that your deepest sleep level config file is located to. Simply, isn't it?")
		        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) { 
		                dialog.dismiss();
		            }
		         })
		         .show();
		        break;
		    
		    }
		    return true;
		}

	public void ShowToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
	
	/*Create a script into etc/init.d folder only if it isn't still there, else it will append the text, writing 
	 to the next line without overriding.
	*/
	public void makescript(){
		Utils.mountSystemRW();
		Utils.mRunAsSU("cp -f "+"/system/etc/init.d/script"+" "+Environment.getExternalStorageDirectory()+"/script", "rm "+"/system/etc/init.d/script");
    	File f = new File(Environment.getExternalStorageDirectory().toString(), "script");
    	File file = new File(path.getText().toString());
        try {
        if(!f.exists()) {
             f.createNewFile();
             BufferedWriter start = new BufferedWriter(new FileWriter(f, true));
             start.write("#!system/bin/sh");
             start.close();
        }

        StringBuilder text = new StringBuilder(); // build the string
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f)); //Buffered reader used to read the file
        while ((line = br.readLine()) != null) { // if not empty continue
            text.append(line);
            text.append('\n');
            
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(f, true)); //true means that it appends the text
        if(file.exists()){
        	    	  Utils.mRunAsSU("echo "+value.getText().toString()+" > "+ path.getText().toString());
        	            ShowToast("Value applied.");                         
        	            output.write("\n"+"echo "+value.getText().toString()+" > "+path.getText().toString());
        	            output.close();
        	            Utils.mRunAsSU("cp -f "+Environment.getExternalStorageDirectory()+"/script"+" /system/etc/init.d/script", "rm "+Environment.getExternalStorageDirectory()+"/script");
        	            Utils.mSetFilePerm("/system/etc/init.d/script", 777);
        	            ShowToast("Script created successfully!");
        	            br.close();
        	}      
        else {
        	ShowToast("Invalid path.");
        
	}
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }   

}
