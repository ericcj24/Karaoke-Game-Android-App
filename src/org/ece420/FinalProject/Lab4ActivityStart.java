package org.ece420.FinalProject;


import org.ece420.FinalProject.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Lab4ActivityStart extends Activity {
	private final static String TAG="Lab4Activity";
	private Button mainMenuButton1 = null;
	private Button mainMenuButton2 = null;
	private Button mainMenuButton3 = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainMenuButton1=(Button)findViewById(R.id.btn1);
        mainMenuButton2=(Button)findViewById(R.id.btn2);
        mainMenuButton3=(Button)findViewById(R.id.btn3);
        mainMenuButton1.setOnClickListener(listener);
        mainMenuButton2.setOnClickListener(listener);
        mainMenuButton3.setOnClickListener(listener);
    }
    
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Lab4ActivityStart-->onPause");
    }
    
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Lab4ActivityStart-->onStop");
    }
    
    private OnClickListener listener = new OnClickListener()
    {
    	public void onClick(View v){
    		Button btn=(Button)v;
    		switch (btn.getId()){
    		case R.id.btn1:
    			Intent myIntent = new Intent(Lab4ActivityStart.this,Lab4Activity.class);
        		startActivity(myIntent);
    			break;
    			
    		case R.id.btn2:
    			Toast.makeText(Lab4ActivityStart.this, "hello there", Toast.LENGTH_LONG).show();
    			break;
    			
    		case R.id.btn3:
    			Toast.makeText(Lab4ActivityStart.this, R.string.Author, Toast.LENGTH_LONG).show();
    			break;
    		}
    	}
    };
    
}