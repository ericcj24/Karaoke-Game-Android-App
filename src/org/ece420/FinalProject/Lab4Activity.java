package org.ece420.FinalProject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;
import java.security.InvalidParameterException;
import java.util.Locale;

import org.ece420.FinalProject.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.media.MediaPlayer.TrackInfo;
import android.media.TimedText;


public class Lab4Activity extends Activity {
	private final static String TAG="Lab4Activity";
	
	
	// -------- timed text --------
	private static Handler handler = new Handler();
	private TextView txtDisplay;
	// -------- timed text --------
	
	
	int frequency = 8000; //8000
	int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	int 	blockSize = 256;					// block size in samples
	int		blockSizeInBytes = 2*blockSize;		// block size in bytes
	boolean on = false;							// process data as long as this is true
	boolean playing = false;
	
	int pitch;
	int score = 0;
	int counter=0;
	double[] pitchref = new double[1312];
	int temp22 = 0;
	int loopt = 0;
	
	RecordAudio recordTask;
	MediaPlayer track;
	ScrollTextView scrolltext;
	
	ImageView	imageView;
	Bitmap 		bitmap;
	Canvas 		canvas;
	Paint 		paint;
	LinearLayout layout;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

    	Button mainMenuButton1 = null;
    	Button mainMenuButton2 = null;
    	mainMenuButton1=(Button)findViewById(R.id.btn1);
        mainMenuButton2=(Button)findViewById(R.id.btn2);
        mainMenuButton1.setOnClickListener(listener);
        mainMenuButton2.setOnClickListener(listener);
        
        imageView = (ImageView)this.findViewById(R.id.imageView1);
    	bitmap = Bitmap.createBitmap((int)256,(int)384,Bitmap.Config.ARGB_8888);
    	canvas = new Canvas(bitmap);
		canvas.drawColor(Color.BLACK);
    	paint = new Paint();
    	paint.setColor(Color.GREEN);
    	paint.setStyle(Paint.Style.FILL);
    	imageView.setImageBitmap(bitmap);
    	
        Log.i(TAG, "Lab4Activity-->onCreate");
        
        
        InputStream is = this.getResources().openRawResource(R.raw.ref1);
        InputStreamReader isr = new InputStreamReader(is);  
        BufferedReader br = new BufferedReader(isr); 
        
        int idx = 0;
        try {
        	String line = null;
    		while ((line = br.readLine()) != null) {
    			int idxt = 4*idx;
    			// \\s+ means any number of whitespaces between tokens
    		    String [] tokens = line.split("\\s+");
    		    String var_1 = tokens[0];
    		    String var_2 = tokens[1];
    		    String var_3 = tokens[2];
    		    String var_4 = tokens[3];
    		    pitchref[idxt] = Double.valueOf(var_1);
    		    pitchref[idxt+1] = Double.valueOf(var_2);
    		    pitchref[idxt+2] = Double.valueOf(var_3);
    		    pitchref[idxt+3] = Double.valueOf(var_4);
    		    //Log.i(TAG, var_1);
    		    idx = idx + 1;
    		}
    		isr.close();
    		is.close();
    		br.close();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
        
        
    }
    
    public void onStart() {
    	super.onStart();
    	track = MediaPlayer.create(Lab4Activity.this, R.raw.alphabetsong);
    	track.setOnCompletionListener(completionlistener);
    	
    	txtDisplay = (TextView) findViewById(R.id.txtDisplay);
    	
// -----------------------  display timed text -----------------------
    	try{
    		Log.i(TAG, "first"+String.valueOf((track.getTrackInfo().length)));
    		
    		track.addTimedTextSource(getSubtitleFile(R.raw.lyricssrt),MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
    		
    		Log.i(TAG, "second"+String.valueOf((track.getTrackInfo().length)));
    		
    		int textTrackIndex = findTrackIndexFor(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, track.getTrackInfo());
    	        if (textTrackIndex >= 0) {
    	        	
    	        	track.selectTrack(textTrackIndex);
    	        	
    	            Log.i(TAG, "textTrackIndex --> "+String.valueOf(textTrackIndex));
    	            
    	        } else {
    	            Log.w(TAG, "Cannot find text track!");
    	        }
    	        
    	        track.setOnTimedTextListener(timedtextlistener);
    	        track.start();
    	        
    	   }catch (Exception e) {
   	        e.printStackTrace();
    	   }
    	/* catch(IllegalArgumentException e){
       		e.printStackTrace();
       	}
       	catch(IllegalStateException e){
       		e.printStackTrace();
       	}*/
    	
    	
    			
// ---------------------- end display timed text end -----------------------
    	
    	InputStream lyrics = getResources().openRawResource(R.raw.lyrics);
    	
        StringBuilder inputStringBuilder = new StringBuilder();
        InputStreamReader reader;
		try {
			reader = new InputStreamReader(lyrics, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
	        String line = bufferedReader.readLine();
	        while(line != null){
	            inputStringBuilder.append(line);
	            inputStringBuilder.append('\n');
	            line = bufferedReader.readLine();
	        } 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lyricsString = inputStringBuilder.toString();
	    TextView tv = (TextView)Lab4Activity.this.findViewById(R.id.lyrics);
	    tv.setText(lyricsString);

// --------------------------------------------------------------------------------    
	    InputStream lyrics1 = getResources().openRawResource(R.raw.scrolllyrics);
        StringBuilder inputStringBuilder1 = new StringBuilder();
        InputStreamReader reader1;
		try {
			reader1 = new InputStreamReader(lyrics1, "UTF-8");
			BufferedReader bufferedReader1 = new BufferedReader(reader1);
	        String line1 = bufferedReader1.readLine();
	        while(line1 != null){
	            inputStringBuilder1.append(line1);
	            inputStringBuilder1.append('\n');
	            line1 = bufferedReader1.readLine();
	        } 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lyricsString1 = inputStringBuilder1.toString();
	    
	    scrolltext = (ScrollTextView)Lab4Activity.this.findViewById(R.id.scrolltext);

	    scrolltext.setText(lyricsString1);
        
        scrolltext.setTextColor(Color.WHITE);
        
        scrolltext.startScroll();
    	
    	Log.i(TAG, "Lab4Activity-->onStart");
    }
    
    public void onResume(){
    	super.onResume();

    	
    	on = true;
    	recordTask = new RecordAudio();
    	recordTask.execute();
		
    	
		track.start();
		
		scrolltext.resumeScroll();
		
		TextView tv = (TextView)Lab4Activity.this.findViewById(R.id.disscore);
    	tv.setText("score: "+String.valueOf(score));
    	
    	Log.i(TAG, "Lab4Activity-->onResume");
    }
    
    public void onPause(){
    	super.onPause();
    	on = false;
    	if (track.isPlaying()){
    		track.pause();
    	}
    	
    	scrolltext.pauseScroll();
    	
    	Log.i(TAG, "Lab4Activity-->onPause");
    }
    
    public void onStop() {
    	super.onStop();
    	// stop playing
    	track.stop();
    	track.release();
    	// stop recording
    	on = false;
    	recordTask.cancel(true);
    	
    	Log.i(TAG, "Lab4Activity-->onStop "+ String.valueOf(counter) + " " + String.valueOf(loopt));
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	Log.i(TAG, "Lab4Activity-->onDestroy");
    }
 // ___-------________-------________-------________-------________-------________-------
   
   
// ----------------- AudioRecord -----------------    
	private class RecordAudio extends AsyncTask<Void, DoubleBuffer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
			AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
					frequency,
					channelConfiguration,
					audioEncoding,
					bufferSize);

			ByteBuffer buffer = ByteBuffer.allocateDirect(blockSize*(Short.SIZE/Byte.SIZE));
			DoubleBuffer processed = ByteBuffer.allocateDirect(blockSize*(Double.SIZE/Byte.SIZE))
					.order(ByteOrder.nativeOrder()).asDoubleBuffer();

			try{
				audioRecord.startRecording();
			}
			catch(IllegalStateException e) {
				Log.e("Recording failed", e.toString());
			}
    		
    		while(on) {
    			// read block of MIC data
    			int bufferReadResult = audioRecord.read(buffer, blockSizeInBytes);

    			// convert buffer to short so we don't have to deal with Endianness in C
    			ShortBuffer sb = ((ByteBuffer)buffer.rewind()).order(ByteOrder.nativeOrder()).asShortBuffer();
 

    			/************ NATIVE DATA/SIGNAL PROCESSING TASK *************/
    			pitch = process(sb, processed, bufferReadResult/Short.SIZE*Byte.SIZE);
    			// update screen with processed results
    			publishProgress(processed);
    			
    			//score
    			//compute the score after !<pitch comparison>!
    			if (Math.abs(pitch-(int)pitchref[counter])<10 && pitch != 0){ //&& pitch-(int)pitchref[counter]>0){
    				score = score + 1;
    			}
    			
    			counter = counter + 1;
    			if (counter>1131){
    				counter=0;
    				loopt = loopt+1;
    			}
    		}
    		try{
    			audioRecord.stop();
    		}
    		catch(IllegalStateException e) {
    			Log.e("Stop failed", e.toString());
    		}
    		    		
    		return null;
    	}
    	
    	protected void onProgressUpdate(DoubleBuffer... newDisplayUpdate) {
    		// emulate a scrolling window
    		Rect srcRect = new Rect(0, -(-1), bitmap.getWidth(), bitmap.getHeight());
    		Rect destRect = new Rect(srcRect);
    		destRect.offset(0, -1);
    		canvas.drawBitmap(bitmap, srcRect, destRect, null);
    		
    		// display new data in right-most column
    		for(int i=0;i<newDisplayUpdate[0].capacity();i++) {
    			// map value, which is between 0.0 and 1.0, to an RGB color
    			int[] rgb = colorMap(newDisplayUpdate[0].get());

    			// set color with constant alpha
    	    	paint.setColor(Color.argb(255, rgb[0], rgb[1], rgb[2]));
    	    	
    	    	// paint right-most column with frequency corresponding to row i
      	    	canvas.drawRect(i, 382, i+1, 383, paint);
    		}
    		newDisplayUpdate[0].rewind();
    		imageView.invalidate();
    		
    		TextView tv = (TextView)Lab4Activity.this.findViewById(R.id.dispitch);
			tv.setText("pitch: "+String.valueOf(pitch));
			
			TextView tv1 = (TextView)Lab4Activity.this.findViewById(R.id.disscore);
			tv1.setText("score: "+String.valueOf(score));
    	}    	
	}
    
	
    
    private OnClickListener listener = new OnClickListener()
    {
    	public void onClick(View v){
    		Button btn=(Button)v;
    		switch (btn.getId()){
    		case R.id.btn2:
    			Intent myIntent = new Intent(Lab4Activity.this,Lab4ActivityStart.class);
        		startActivity(myIntent);
    			break;
    			
    		case R.id.btn1:
    			Intent myIntent1 = new Intent(Lab4Activity.this,Lab4ActivityPause.class);
    			myIntent1.putExtra("score", score);
        		startActivity(myIntent1);
    			break;
    		}
    	}
    };
    private OnCompletionListener completionlistener = new OnCompletionListener() 
    {
    	public void onCompletion(MediaPlayer player){

			 Intent myIntent2 = new Intent(Lab4Activity.this,Lab4ActivityFinished.class);
			 myIntent2.putExtra("score", score);
			 startActivity(myIntent2);
			 Log.i(TAG, "Lab4Activity-->SongComplete");
    	}
	};
	
	
	
	private OnTimedTextListener timedtextlistener = new OnTimedTextListener()
	{
		@Override
		public void onTimedText(final MediaPlayer mp, final TimedText text) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Lab4Activity-->OnTimedTextListener");
			
			if (text != null) {
		        handler.post(new Runnable() {
		            @Override
		            public void run() {
		            	
		                //int seconds = mp.getCurrentPosition() / 1000;
		                
		                txtDisplay.setText(text.getText());
		            }
		        });
		    }
		}	
	};
	
// -----------------------  display timed text -----------------------
	private int findTrackIndexFor(int mediaTrackType, TrackInfo[] trackInfo) {
	    int index = -1;
	    for (int i = 0; i < trackInfo.length; i++) {
	        if (trackInfo[i].getTrackType() == mediaTrackType) {
	            return i;
	        }
	    }
	    return index;
	}

	private String getSubtitleFile(int resId) {
	    String fileName = getResources().getResourceEntryName(resId);
	    File subtitleFile = getFileStreamPath(fileName);
	    if (subtitleFile.exists()) {
	        Log.d(TAG, "Subtitle already exists");
	        return subtitleFile.getAbsolutePath();
	    }
	    Log.d(TAG, "Subtitle does not exists, copy it from res/raw");

	    // Copy the file from the res/raw folder to your app folder on the
	    // device
	    InputStream inputStream = null;
	    OutputStream outputStream = null;
	    try {
	        inputStream = getResources().openRawResource(resId);
	        outputStream = new FileOutputStream(subtitleFile, false);
	        copyFile(inputStream, outputStream);
	        return subtitleFile.getAbsolutePath();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        closeStreams(inputStream, outputStream);
	    }
	    return "";
	}

	private void copyFile(InputStream inputStream, OutputStream outputStream)
	        throws IOException {
	    final int BUFFER_SIZE = 1024;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int length = -1;
	    while ((length = inputStream.read(buffer)) != -1) {
	        outputStream.write(buffer, 0, length);
	    }
	}

	// A handy method I use to close all the streams
	private void closeStreams(Closeable... closeables) {
	    if (closeables != null) {
	        for (Closeable stream : closeables) {
	            if (stream != null) {
	                try {
	                    stream.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	}

	/*//@Override
	public void onTimedText(final MediaPlayer mp, final TimedText text) {
		 if (text != null) {
		        handler.post(new Runnable() {
		            @Override
		            public void run() {
		                int seconds = mp.getCurrentPosition() / 1000;

		                txtDisplay.setText("[" + secondsToDuration(seconds) + "] "
		                        + text.getText());
		            }
		        });
		    }
	}

	// To display the seconds in the duration format 00:00:00
	public String secondsToDuration(int seconds) {
	    return String.format("%02d:%02d:%02d", seconds / 3600,
	            (seconds % 3600) / 60, (seconds % 60), Locale.US);
	}*/
// -----------------------  display timed text end -----------------------
	
    public int[] colorMap(double value) {
    	// implements a simple linear RYGCB colormap
        if(value <= 0.25) {
            return new int[]{0, (int)(4*value*255), (int)255};
        } else if(value <= 0.5) {
        	return new int[]{0, (int)255, (int)((1-4*(value-0.25))*255)};
        } else if(value <= 0.75) {
            return new int[]{(int)(4*(value-0.5)*255), (int)255, 0};
        } else {
            return new int[]{(int)255, (int)((1-4*(value-0.75))*255), 0};
        }
    }
    
    static {
        System.loadLibrary("process");
    }
        
    public static native int process(ShortBuffer inbuf, DoubleBuffer outbuf, int N);
}