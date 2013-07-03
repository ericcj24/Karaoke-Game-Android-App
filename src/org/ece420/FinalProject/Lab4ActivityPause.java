package org.ece420.FinalProject;


import org.ece420.FinalProject.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Lab4ActivityPause extends Activity {
	private final static String TAG="Lab4Activity";
	
	private Button mainMenuButton1 = null;
	private Button mainMenuButton2 = null;
	int score;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
        mainMenuButton1=(Button)findViewById(R.id.btn1);
        mainMenuButton2=(Button)findViewById(R.id.btn2);
        mainMenuButton1.setOnClickListener(listener);
        mainMenuButton2.setOnClickListener(listener);
        
        score = getIntent().getIntExtra("score", -1);
        TextView tv = (TextView)Lab4ActivityPause.this.findViewById(R.id.pausescore);
		tv.setText("Score: "+String.valueOf(score));
        
        Log.i(TAG, "Lab4ActivityPause-->onCreate");
    }
    
    private OnClickListener listener = new OnClickListener()
    {
    	public void onClick(View v){
    		Button btn=(Button)v;
    		switch (btn.getId()){
    		case R.id.btn1:
    			Intent myIntent = new Intent(Lab4ActivityPause.this,Lab4ActivityStart.class);
        		startActivity(myIntent);
    			break;
    			
    		case R.id.btn2:
    			Intent myIntent1 = new Intent(Lab4ActivityPause.this,Lab4ActivityFinished.class);
    			myIntent1.putExtra("score", score);
    			startActivity(myIntent1);
    			break;
    		}
    	}
    };
    
    public void onStart(){
    	super.onStart();
    	Log.i(TAG, "Lab4ActivityPause-->onStart");
    }
    public void onStop(){
    	super.onStop();
    	Log.i(TAG, "Lab4ActivityPause-->onStop");
    }
    public void onDestroy(){
    	super.onDestroy();
    	Log.i(TAG, "Lab4ActivityPause-->onDestroy");
    }
    
}