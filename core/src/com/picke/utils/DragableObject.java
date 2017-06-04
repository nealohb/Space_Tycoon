package com.picke.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.picke.space.BaseClass;
import com.picke.space.Game;

public class DragableObject extends BaseClass {
	
	float posX, posY, origX, origY, minReturnSpeed = 50, returnSpeed = 600;
	boolean draging, atOrigin, active;

	DragableListener listener;
	
	public DragableObject(Game g, float posX, float posY){
		super(g);
		
		this.posX = origX = posX;
		this.posY = origY = posY;
	}
	
	public DragableObject(Game g){
		super(g);
	}
	
	public void setListener(DragableListener listener){
		this.listener = listener;
	}
	
	public void startDraging(){
		draging = true;
		atOrigin = false;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean atOrigin(){
		return atOrigin;
	}
	
	public void setReturnSpeed(float speed){
		returnSpeed = speed;
	}
	
	public void reset(){
		posX = origX;
		posY = origY;
		atOrigin = false;
		draging = false;
	}
	
	public boolean draging(){
		return draging;
	}
	
	public boolean active(){
		return active;
	}
	
	public void setOrig(float origX, float origY){
		atOrigin = false;
		this.origX = origX;
		this.origY = origY;
	}
	
	public void forcePosition(float posX, float posY){
		this.posX = origX = posX;
		this.posY = origY = posY;
	}
	
	public void update(float delta, float x, float y, boolean isTouched){
		if(draging){
			posX = x;
			posY = y;
		}
		else if(Tools.dst(posX, posY, origX, origY) > 1.0f){
			float dirX = origX - posX;
			float dirY = origY - posY;

			float hyp = (float) Math.sqrt(dirX*dirX + dirY*dirY);
			dirX /= hyp;
			dirY /= hyp;

			if(Tools.dst(posX, posY, origX, origY) < 15){
				posX += dirX*delta*minReturnSpeed;
				posY += dirY*delta*minReturnSpeed;
			}
			else{
				posX += dirX*delta*returnSpeed;
				posY += dirY*delta*returnSpeed;
			}
		}
		else{
			atOrigin = true;
			posX = origX;
			posY = origY;
		}
		
		if(draging && !isTouched){
			draging = false;
			if(listener != null) listener.releasedObject(x, y);
		}
	}
	
	public void draw(TextureRegion texture, float scale, float rotation){
		g.m.drawTexture(texture, posX, posY, false, false, scale, rotation);
	}

	public float getOrigX(){
		return origX;
	}
	
	public float getOrigY(){
		return origY;
	}
	
	public float getX(){
		return posX;
	}
	
	public float getY(){
		return posY;
	}

	public void setPosition(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void setMinReturnSpeed(float speed) {
		minReturnSpeed = speed;
	}
}
