package com.ypsilon.app;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Caller {

	private SoundPool spool;
	private HashMap<String, Integer> wordIdMap;
	private HashMap<Integer, Integer> numIdMap;
	
	private Converter conv;
	
	private Context parentContext;
	
	public Caller(Context cont) {

		parentContext = cont;

		conv = new Converter();

		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

		wordIdMap = new HashMap<String, Integer>();
		numIdMap = new HashMap<Integer, Integer>();

		createIdMap();

	}
	
	
	
	private void createIdMap () {
		
		for (Map.Entry<Integer, Integer> entry : conv.numToResid.entrySet()) {
			numIdMap.put(entry.getKey(), (Integer)spool.load(parentContext, entry.getValue().intValue(), 1));
		}

		for (Map.Entry<String, Integer> entry : conv.wordToResid.entrySet()) {
			wordIdMap.put(entry.getKey(), (Integer)spool.load(parentContext, entry.getValue().intValue(), 1));
		}
	}

	public void say (int num) {
		if (numIdMap.containsKey(new Integer(num))) {
			spool.play(numIdMap.get(new Integer(num)), 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}
	
	public void say (String word) {
		if (wordIdMap.containsKey(word)) {
			spool.play(wordIdMap.get(word), 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}
}
