package com.hasta.scriptmaker;

import java.io.DataOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class  Utils {
	
	
    public static String SU_wop(String cmds) {
//     FLAG:0x2
        String out = null;
        try {
            out = new String();
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(cmds+"\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            InputStream stdout = p.getInputStream();
            byte[] buffer = new byte[4096];
            int read;
            while (true) {
                read = stdout.read(buffer);
                out += new String(buffer, 0, read);
                if (read < 4096) {
                    break;
                }
            }
        } catch (Exception e) {
            final Activity activity = new MainActivity();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Error Occured...!", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("cocoremanager", "Error executing SU command, flag:0x2");
        }
        return out.substring(0,out.length()-1);
    }
   
    public static void mountSystemRW() {
        mRunAsSU("mount -o rw,remount /system");
    }
    
    
    public static void mSetFilePerm(String path,int mode) {
		mRunAsSU("chmod "+mode+" "+path);
	}
   
    
	public static void mRunAsSU(String... cmds) {
    	Process process;
		try {
			process = Runtime.getRuntime().exec("su");
	        DataOutputStream os = new DataOutputStream(process.getOutputStream());
	        for(int i=0;i<cmds.length;i++)
	            os.writeBytes(cmds[i]+"\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
		} catch(Exception e) {
			Log.e("cocoretest", "Error executing....");
		}
    }
}

class SU extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... cmds) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for(int i=0;i<cmds.length;i++)
                os.writeBytes(cmds[i]+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {}
        return null;
    }
    
    
}

