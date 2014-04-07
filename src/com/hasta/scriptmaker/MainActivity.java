package com.hasta.scriptmaker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
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
		    
		    }
		    return true;
		}

	public void ShowToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
	
	public void makescript(){
		Utils.mountSystemRW();
    	File logFile = new File(Environment.getRootDirectory().toString(), "/etc/init.d/script");
    	File file = new File(path.getText().toString());
        try {
        if(!logFile.exists()) {
             logFile.createNewFile();
             BufferedWriter start = new BufferedWriter(new FileWriter(logFile, true));
             start.write("#!system/bin/sh");
             start.close();
        }

        StringBuilder text = new StringBuilder(); // build the string
        String line;
        BufferedReader br = new BufferedReader(new FileReader(logFile)); //Buffered reader used to read the file
        while ((line = br.readLine()) != null) { // if not empty continue
            text.append(line);
            text.append('\n');
            
        }
        
        BufferedWriter output = new BufferedWriter(new FileWriter(logFile, true));
        if(file.exists()){
        	Utils.mRunAsSU("echo "+value.getText().toString()+" > "+ path.getText().toString());
            ShowToast("Value applied.");                         
            output.write("\n"+"echo "+value.getText().toString()+" > "+path.getText().toString());
            output.close();
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
