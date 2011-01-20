package com.ypsilon.app;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class TickTick {

	private Timer timer;

	Handler handler = new android.os.Handler();
	
	public void onTick() {};
	
	public TickTick() {

	}

	public void start() {
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			public void run () {
				handler.post(new Runnable() {
					public void run() {
						onTick();
					}
				});
			}
		}, 0, 1000);
	}

	public void cancel() {
		timer.cancel();
	}
	
}
