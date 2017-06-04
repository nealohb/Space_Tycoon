package com.picke.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.picke.space.BaseClass;
import com.picke.space.Main;

public class RedirectManager extends BaseClass {

	public RedirectDownloader downloader;
	
	public static final boolean PORTRAIT = true;
	
	public boolean active;
	
	private boolean redirectReady, hasShownRedirect;
	
	float redirectT = 1.5f;
	
	Rectangle customAdRect = new Rectangle();
	Circle customAdClose = new Circle();
	
	public RedirectManager(Main m){
		super(m);
		downloader = new RedirectDownloader(m, this);
		if(PORTRAIT){
			customAdRect.set(64, 100, 414-64, 692-100);
			customAdClose.set(397, 681, 40);
		}
		else{
			customAdRect.set(91, 62, 690 - 91, 418 - 62);
			customAdClose.set(674, 402, 40);
		}
	}
	
	public void update(float delta){
		if(redirectT > 0){
			redirectT -= delta;
			if(redirectReady && !hasShownRedirect && redirectT <= 0)
				showRedirect();
		}
		
		if(redirectReady && active && Gdx.input.justTouched()){
			if(customAdRect.contains(m.x, m.y) && !customAdClose.contains(m.x, m.y)){
				prefs.putBoolean(downloader.packageName, true);
				active = false;
				com.openURL("market://details?id="+downloader.packageName);
			}
			else if(customAdClose.contains(m.x, m.y)){
				active = false;
			}
		}
	}
	
	public void showRedirect(){
		active = true;
		hasShownRedirect = true;
	}
	
	public void draw(){
		if(!active || !redirectReady) return;
		
		b.begin();
		b.setColor(1, 1, 1, 0.7f);
		b.draw(a.blackR, 0, 0, PORTRAIT ? 480 : 800, PORTRAIT ? 800 : 480);
		b.setColor(1, 1, 1, 1);
		b.draw(downloader.customAdR, PORTRAIT ? 40 : 90, PORTRAIT ? 88 : 28, downloader.customAdR.getRegionWidth()*1.6f, downloader.customAdR.getRegionHeight()*1.6f);
		b.end();
	}

	public void setRedirectReady(boolean ready) {
		redirectReady = ready;
	}
}
