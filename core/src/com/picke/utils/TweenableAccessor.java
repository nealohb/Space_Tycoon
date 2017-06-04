package com.picke.utils;

import aurelienribon.tweenengine.TweenAccessor;

public class TweenableAccessor implements TweenAccessor<Tweenable> {
	
	@Override
	public int getValues(Tweenable target, int tweenType, float[] returnValues) {
		returnValues[0] = target.getValue();
		return 1;
	}

	@Override
	public void setValues(Tweenable target, int tweenType, float[] newValues) {
		target.setValue(newValues[0]);
	}


}
