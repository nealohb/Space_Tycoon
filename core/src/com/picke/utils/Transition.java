package com.picke.utils;

import com.picke.space.BaseClass;
import com.picke.space.Game;

public class Transition extends BaseClass {
	TransitionListener listener;
	float delta, transitionAlpha, transitionTarAlpha, blackoutT, speed, blackoutDuration;
	int callbackReference;
	
	public Transition(Game g) {
		super(g);
		
		/* Default values */
		speed = 1; 
		blackoutDuration = 0;
	}
	
	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	public void setBlackoutDuration(float time){
		blackoutDuration = time;
	}
	
	public void addListener(TransitionListener listener){
		this.listener = listener;
	}
	
	public boolean active(){
		return transitionAlpha > 0 || transitionTarAlpha > 0;
	}
	
	public void update(float delta){
		this.delta = delta;
		
		updateTransitions();
	}
	
	public boolean started(){
		return transitionTarAlpha == 1;
	}
	
	public void start(int callbackReference){
		this.callbackReference = callbackReference;
		blackoutT = 0;
		transitionTarAlpha = 1;
	}
	
	public float getAlpha(){
		return transitionAlpha;
	}
	
	void updateTransitions(){
		if(transitionAlpha < transitionTarAlpha){
			transitionAlpha += delta*speed;
			if(transitionAlpha >= transitionTarAlpha){ // pitch black, dispose old screen
				transitionAlpha = transitionTarAlpha;
				
			}
		}
		else if(transitionAlpha > transitionTarAlpha){
			transitionAlpha -= delta*speed;
			if(transitionAlpha <= transitionTarAlpha){
				transitionAlpha = transitionTarAlpha;
			}
		}
		
		if(transitionAlpha == 1){
			blackoutT += delta;
			
			if(blackoutT >= blackoutDuration && transitionTarAlpha == 1){ // make sure that we can't make two transitionDone callbacks (this could happen if the delta value was 0)
				transitionTarAlpha = 0;
				listener.transitionDone(callbackReference);
			}
		}
	}
	
	public void draw(){
		if(transitionAlpha <= 0) return;
		
		b.getProjectionMatrix().setToOrtho2D(0, 0, (m.portrait ? 480 : 800), (m.portrait ? 800 : 480));
		b.begin();
		b.setColor(1, 1, 1, transitionAlpha);
		b.draw(a.blackR, 0, 0, (m.portrait ? 480 : 800), (m.portrait ? 800 : 480));
		b.setColor(1, 1, 1, 1);
		b.end();
	}
}
