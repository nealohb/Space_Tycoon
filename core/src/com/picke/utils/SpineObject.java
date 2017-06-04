package com.picke.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.picke.space.BaseClass;
import com.picke.space.Game;

import java.util.Iterator;

public class SpineObject extends BaseClass {
	Skeleton skel;
	private AnimationState state;
	private SpineListener listener;
	private float size;
	
	/**
	 * 
	 * @param g Game
	 * @param d	SkeletonData
	 * @param posX X
	 * @param posY Y
	 */
	public SpineObject(Game g, SkeletonData d, float posX, float posY, String startAnimation, float animationMixTime, String skin){
		super(g);
		skel = new Skeleton(d);
		skel.setPosition(posX, posY);
		
		AnimationStateData stateData = new AnimationStateData(d);
		if(animationMixTime > 0) stateData.setDefaultMix(animationMixTime);
		state = new AnimationState(stateData);
		if(startAnimation != null) state.setAnimation(0, startAnimation, true);
		
		setSkin(skin);
	}
	
	public SpineObject(Game g, SkeletonData d, String startAnimation, float animationMixTime, String skin){
		super(g);
		skel = new Skeleton(d);
		
		AnimationStateData stateData = new AnimationStateData(d);
		if(animationMixTime > 0) stateData.setDefaultMix(animationMixTime);
		state = new AnimationState(stateData);
		if(startAnimation != null) state.setAnimation(0, startAnimation, true);
		
		setSkin(skin);
	}
	
	public SpineObject(Game g, SkeletonData d){
		super(g);
		skel = new Skeleton(d);
		state = new AnimationState(new AnimationStateData(d));
	}
	
	public SpineObject(Game g, SkeletonData d, String startAnimation){
		super(g);
		skel = new Skeleton(d);
		state = new AnimationState(new AnimationStateData(d));
		state.setAnimation(0, startAnimation, true);
	}
	
	public void setSkin(String skin){
		if(skin != null){
			skel.setSkin(skin);
			skel.setSlotsToSetupPose();
		}
	}
	
	
	public void clearAnimations(){
		skel.setToSetupPose();
		state.clearTracks();
	}
	
	public void setListener(SpineListener list){
		this.listener = list;
		
		state.addListener(new AnimationStateListener(){

			@Override
			public void event(int trackIndex, Event event) {
				listener.onEvent(event.getData().getName());
			}

			@Override
			public void complete(int trackIndex, int loopCount) {
			}

			@Override
			public void start(int trackIndex) {
			}

			@Override
			public void end(int trackIndex) {
			}
			
		});
	}
	
	public Skeleton getSkel(){
		return skel;
	}
	
	public float getAnimationDuration(String animation){
		Iterator<Animation> it = skel.getData().getAnimations().iterator();
		while(it.hasNext()){
			Animation a = it.next();
			if(a.getName().equals(animation))
				return a.getDuration();
		}
		return 0;
	}
	
	public void setSize(float size){
		this.size = size;
		skel.findBone("root").setScale(size);
	}
	
	public float getSize() {
		return skel.findBone("root").getScaleX();
	}
	
	public void setX(float x){
		skel.setX(x);
	}
	
	public void setY(float y){
		skel.setY(y);
	}
	
	public float getX(){
		return skel.getX();
	}
	
	public float getY(){
		return skel.getY();
	}
	
	public void setPosition(float x, float y){
		skel.setPosition(x, y);
	}
	
	public void setFlipX(boolean flipX){
		if(skel.getFlipX() != flipX)
			skel.setFlipX(flipX);
	}
	
	public boolean getFlipX(){
		return skel.getFlipX();
	}
	
	public void setAnimation(String animation, boolean loop){
		if(active() && state.getCurrent(0).getAnimation().getName().equals(animation)) return;
		state.setAnimation(0, animation, loop);
	}
	
	public boolean active(){
		return state != null && state.getCurrent(0) != null;
	}
	
	public boolean isComplete(){
		return !active() || state.getCurrent(0).isComplete();
	}
	
	public boolean animationActive(String animation){
		return state.getCurrent(0) != null && state.getCurrent(0).getAnimation().getName().equals(animation);
	}
	
	public void addAnimation(String animation, boolean loop){
		state.addAnimation(0, animation, loop, 0);
	}
	
	public void update(float delta){
		state.update(delta);
	}
	
	public void draw(){
		state.apply(skel);
		skel.updateWorldTransform();
		g.renderer.draw(b, skel);
	}
	
	public void render(float delta){
		state.update(delta);
		state.apply(skel);
		skel.updateWorldTransform();
		g.renderer.draw(b, skel);
	}
	
	public void draw(PolygonSpriteBatch p, float delta){
		state.update(delta);
		state.apply(skel);
		skel.updateWorldTransform();
		g.renderer.draw(p, skel);
	}

	public void setRotation(float angle) {
		skel.findBone("root").setRotation(angle);
	}
	
	public void setRotation(float angle, String bone) {
		if(skel.findBone(bone) != null) skel.findBone(bone).setRotation(angle);
	}

	public float getRotation() {
		return skel.findBone("root").getRotation();
	}

	public float getRotation(String bone) {
		if(skel.findBone(bone) == null) return 0;
		return skel.findBone(bone).getRotation();
	}
	
	public void setAttachment(String slot, String attachmentName) {
		skel.findSlot(slot).setAttachment(skel.getAttachment(slot, attachmentName));
	}
	
	public void setSlotColor(String slot, float[] color){
		skel.findSlot(slot).getColor().set(color[0]/255f, color[1]/255f, color[2]/255f, 1);
	}
	
	public void setSlotColor(String slot, Color color){
		skel.findSlot(slot).getColor().set(Color.WHITE);
	}
}
