package com.picke.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.picke.space.BaseClass;
import com.picke.space.Game;

public class Coin extends BaseClass {
	
	public Vector2 pos = new Vector2(), vel = new Vector2();
	boolean shrink = true, fade = true;
	public boolean active;
	float coinTime, delta, gravity = 9, yOffset;
	public float alpha = 1.5f, scale = 1;
	int coinFrame;
	
	public Coin(Game g, float x, float y, float scale, boolean shrink){
		super(g);
		this.scale = scale;
		this.shrink = shrink;
		pos.set(x, y);
		setRandomVelocity();
	}
	
	public Coin(Game g, float x, float y, float velX, float velY, boolean fade){
		super(g);
		this.fade = fade;
		pos.set(x, y);
		vel.x = velX;
		vel.y = velY;
	}
	
	public Coin(Game g, float x, float y){
		super(g);
		pos.set(x, y);
		setRandomVelocity();	
	}
	
	/**
	 * Static coin, used for menus etc
	 * @param g
	 * @param x
	 * @param y
	 * @param scale
	 */
	public Coin(Game g, float x, float y, float scale){
		super(g);
		this.scale = scale;
		pos.set(x, y);	
	}
	
	private void setRandomVelocity() {
		vel.x = MathUtils.random(30f, 170f)*(MathUtils.randomBoolean() ? 1 : -1);
		vel.y = MathUtils.random(220f, 320f);
	}
	
	public void update(float delta, float yOffset){
		this.yOffset = yOffset;
		update(delta);
	}

	public void update(float delta){
		this.delta = delta;
		vel.y -= gravity*delta*50;
		
		float velLimit = 600;
		if(vel.y < - velLimit)
			vel.y = -velLimit;
		
		if(fade){
			alpha -= delta;
			if(alpha < 0){
				alpha = 0;
			}
		}
		
		if(shrink)
			scale -= delta * scale;
		
		pos.x += vel.x * delta * scale;
		pos.y += vel.y * delta * scale;
		
		updateAnimation();
	}
	
	public void updateAnimation(){
		coinTime += delta;
		if(coinTime >= Misc.COIN_CD){
			coinTime = 0;
			coinFrame++;
			if(coinFrame > 19)
				coinFrame = 0;
		}
	}
	
	public void render(float xOffset, float delta){
		render(xOffset, delta, 1);
	}
	
	public void render(float xOffset, float delta, float scl){
		this.delta = delta;
		updateAnimation();
		m.drawTexture(a.coinR[coinFrame], pos.x+xOffset, pos.y, false, false, scale*scl, 0);
	}
	
	public void draw(){
		b.setColor(1, 1, 1, MathUtils.clamp(alpha, 0, 1));
		b.draw(a.coinR[coinFrame], pos.x - a.w(a.coinR[coinFrame])/2f, pos.y - a.h(a.coinR[coinFrame])/2f + yOffset, 
				a.w(a.coinR[coinFrame])/2, a.h(a.coinR[coinFrame])/2, 
				a.w(a.coinR[coinFrame]), a.h(a.coinR[coinFrame]), 
				Math.max(scale, 0), Math.max(scale, 0), 0);
		b.setColor(1, 1, 1, 1);
	}
}
