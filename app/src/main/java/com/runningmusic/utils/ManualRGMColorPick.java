package com.runningmusic.utils;

import java.util.ArrayList;

import android.R.integer;
import android.graphics.Color;

public class ManualRGMColorPick {

	public static ArrayList<Integer> colorPanelList = new ArrayList<Integer>();
	
	public ManualRGMColorPick() {
		for (int i = 0; i < 32; i++) {
			colorPanelList.add(Color.argb(255, 255, 0+8*i, 0));
		}
		colorPanelList.add(Color.argb(255, 255, 255, 0));
		for (int i = 0; i < 32; i++) {
			colorPanelList.add(Color.argb(255, 255-8*i, 255, 0));
		}
		colorPanelList.add(Color.argb(255, 0, 255, 0));
	}
	
	public static int getColorByValue(double start, double end, double value, int length, int iMax) {
		int ratio=0;
		if (value < iMax) {
			ratio = (int)(value*64/iMax);
		} else if (value == iMax) {
			return Color.RED;
		} else if (value > iMax && value <= length) {
			ratio = (int)(64-(value-iMax)/(length-iMax)*64);
		}

		if (ratio < 0) {
			ratio = 0;
		} else if (ratio >= 64) {
			ratio = 63;
		}
		ratio = 63-ratio;
		return colorPanelList.get(ratio);
	}
	
	public static int getColorBySpeed(double min, double max, double speed) {
		int ratio=0;
		
		ratio = (int)((speed-min) * 64/(max-min));
		if (ratio < 0) {
			ratio = 0;
		} else if (ratio >= 64) {
			ratio = 63;
		}
		ratio = 63-ratio;
		return colorPanelList.get(ratio);
		
	}
	
	public static int getColorBySpeedAuto(double min, double max, double speed) {
		int ratio=0;
		
		ratio = (int)((speed-min) * 64/(max-min));
		if (ratio < 0) {
			ratio = 0;
		} else if (ratio >= 64) {
			ratio = 63;
		}
		return colorPanelList.get(ratio);
		
	}
}
