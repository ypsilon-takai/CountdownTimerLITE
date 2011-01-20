package com.ypsilon.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

/**
 * Main window for the timer.
 * @author Ypsilon
 * @version 0.5
    
 */

public class MainWindow extends Activity {
	private TextView tvTimeView;
 
	private ViewFlipper vfSelector;
	
	private Button btStartStop;
    private Button btStartStopBig;
    private Button bt00;
    private Button bt01;
    private Button bt02;
    private Button bt10;
    private Button bt11;
    private Button bt12;
    private ToggleButton tgbImmediate;
    private ToggleButton tgbPrecall;

    /**
     * 1sec event gererator
     */
    private TickTick ticktick;

    /**
     * Sound|Speech generator.
     */
    private Caller caller;

    /**
     * Setted time value.
     * Not change during coutdown.
     * (Second)
      */
    private int setTimeVal;

    /**
     * Decrimenter.
     * (Second)
     */
    private int timeVal;

    /**
     * Setted time value in seconds for '3,2,1' call
     * (Second)
     */
    private int preTimeVal;

    /**
     * Decrimenter for '3,2,1'.
     * (Second)
     */
    private int pretime;

    /**
     * X axis value holder for flick.
     */
    private float startXPos;

    /**
     * Flick flag
     */
    private boolean flicked;
    
    /**
     * Flag for timer is running or not.
     */
    private boolean timerRunning;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTimeVal = timeVal = 60;
        preTimeVal = pretime = 5; 
        
        flicked = false;
        
        timerRunning = false;
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        Log.d( "HLGT Debug", "--> 1" );
        setContentView(R.layout.main);
        
 
        tvTimeView = (TextView)findViewById(R.id.TimeView);

        // Set time display font to 12-seg font.
        Typeface dispfont = Typeface.createFromAsset(getAssets(), "fonts/Seg12Modern.ttf");
        tvTimeView.setTypeface(dispfont);
        
        vfSelector = (ViewFlipper)findViewById(R.id.VF_switcher); 
        
        btStartStop = (Button)findViewById(R.id.Bt_Main);
        btStartStopBig = (Button)findViewById(R.id.Bt_Sub_Main);
        bt00 = (Button)findViewById(R.id.Bt_00);
        bt01 = (Button)findViewById(R.id.Bt_01);
        bt02 = (Button)findViewById(R.id.Bt_02);
        bt10 = (Button)findViewById(R.id.Bt_10);
        bt11 = (Button)findViewById(R.id.Bt_11);
        bt12 = (Button)findViewById(R.id.Bt_12);
        tgbImmediate = (ToggleButton)findViewById(R.id.TG_immediate);
        tgbPrecall = (ToggleButton)findViewById(R.id.TG_countdown);
        
        // Converter class provides format exchange functionality.
        tvTimeView.setText(Converter.formatTimeSec(timeVal));
        btStartStop.setText(Converter.formatTimeSec(timeVal));
		btStartStopBig.setText(Converter.formatTimeSec(timeVal));

        // Start or stop countdown.
        btStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if (! flicked ) {
					startOrStop();
				}
			}
			
		});

        // Flip button window.
        vfSelector.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				flipTemplate(event);
				return true;
			}
		});
        btStartStop.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				flipTemplate(event);
				return false;
			}
		});

        // Big button is not act with click.
        btStartStopBig.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Do nothing
			}
		});

        // Big button is act with long click.
        btStartStopBig.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				startOrStop();
				return true;
			}
		});

        // Flip button window when user wipe on big button.
        btStartStopBig.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				flipTemplate(event);
				return false;
			}
		});

        // Functions for number button clicked.
        bt00.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush( 10*60);
				}
			}
		});
        bt01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush( 5*60);
				}
			}
		});
        bt02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush( 3*60);
				}
			}
		});
        bt10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush( 2*60);
				}
			}
		});
        bt11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush( 1*60);
				}
			}
		});
        bt12.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (! timerRunning) {
					setTimeOnButtonPush(30);
				}
			}
		});


        // Create Caller.
        caller = new Caller(this);

        // Create TickTick and set callback.
        ticktick = new TickTick() {
			public void onTick() {
					countDown();
			}
		};	
        
    }

    /**
     * Gereric function to setup number buttons.
     * @param int Setting time.
     */
    private void setTimeOnButtonPush (int timesec) {
		setTimeVal = timeVal = timesec;
		tvTimeView.setText(Converter.formatTimeSec(timeVal));
		btStartStop.setText(Converter.formatTimeSec(timeVal));
		btStartStopBig.setText(Converter.formatTimeSec(timeVal));
        if (tgbImmediate.isChecked()) {
        	startOrStop();
        }
    	
    }

    /**
     * Count down start/stop function.
     */
    public void startOrStop () {
    	if (timerRunning) {
    		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    		timerRunning = false;
    		ticktick.cancel();
    		resetTimer();
    	} else {
    		timerRunning = true;
    		btStartStop.setBackgroundResource(R.drawable.plastic_red_button);
    		btStartStopBig.setBackgroundResource(R.drawable.plastic_red_button);
    		ticktick.start();
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
  	
    }

    /**
     * 
     */
    public void countDown () {
    	
    	if (tgbPrecall.isChecked() && pretime > 0) {
       		say(pretime);
       		tvTimeView.setText(Converter.formatTimeSec(pretime));
    		pretime--;
    	} else if ( timeVal > 0){
        	say(timeVal);
        	tvTimeView.setText(Converter.formatTimeSec(timeVal));
       		timeVal--;
    	} else if (timeVal < 0) {
			say("finished");
			ticktick.cancel();
			timerRunning = false;
			resetTimer();
    	} else {
        	say(timeVal);
        	tvTimeView.setText(Converter.formatTimeSec(timeVal));
    		timeVal--;
    	}
    	
    }
    
    private void resetTimer () {
    	timeVal = setTimeVal;
    	pretime = preTimeVal;
		tvTimeView.setText(Converter.formatTimeSec(setTimeVal));
		btStartStop.setBackgroundResource(R.drawable.plastic_button);
		btStartStopBig.setBackgroundResource(R.drawable.plastic_button);
    }
    
    public void say (int timesec) {
    	caller.say(timesec);
    }

    public void say (String string) {
    	caller.say(string);
    }
    
    private boolean flipTemplate (MotionEvent event) {
    	float endXPos;
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			flicked = false;
			startXPos = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			endXPos = event.getX();
			if (startXPos - endXPos > 30) {
				flicked = true;
				vfSelector.showNext();
			} else if (startXPos - endXPos < -30){
				flicked = true;
				vfSelector.showPrevious();
			}
			break;
		}

		return true;
    }
    
}