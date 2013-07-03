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

public class Lab4ActivityFinished extends Activity {
	private final static String TAG="Lab4Activity";

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Lab4ActivityFinished-->onCreate");
        setContentView(R.layout.main4);
        Button mainMenuButton1 = null;
        mainMenuButton1=(Button)findViewById(R.id.btn1);
        mainMenuButton1.setOnClickListener(listener);

        int score = getIntent().getIntExtra("score", -1);
        TextView tv = (TextView)Lab4ActivityFinished.this.findViewById(R.id.disfinalscore);
		tv.setText("Final Score: "+String.valueOf(score));

    }
    
    private OnClickListener listener = new OnClickListener()
    {
    	public void onClick(View v){
    		Button btn=(Button)v;
    		switch (btn.getId()){
    		case R.id.btn1:
    			Intent myIntent = new Intent(Lab4ActivityFinished.this,Lab4ActivityStart.class);
        		startActivity(myIntent);
    			break;
    			
    		//case R.id.btn2:
    			//Toast.makeText(Lab4ActivityFinished.this, "hello there", Toast.LENGTH_LONG).show();
    			//break;
    		}
    	}
    };
    
}