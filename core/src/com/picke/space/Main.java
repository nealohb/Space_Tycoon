package com.picke.space;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.picke.utils.RedirectManager;
import com.picke.utils.Tools;
import com.picke.utils.TweenableAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class Main extends ApplicationAdapter {
	public SpriteBatch b;
	Texture img;
	public Assets a;
	public Game g;
	public RedirectManager redirectManager;
	public com.picke.utils.Communicator com;
	public Preferences prefs;
	public TweenManager tweenManager;
	
	/**
	 * AD SETTINGS
	 */
	protected static final float ADS_INITIAL_CD = 120;
	protected static final float ADS_CD = 220;
	
	/**
	 * TODO DEBUG TOOLS
	 */
	public static final boolean INFINITE_WEALTH = false;
	public static final boolean 	   TEST_ADS = true;
	public static final boolean    PRINT_COORDS = true;
	public static final boolean       REDIRECTS = false;
	
	ShapeRenderer adRenderer;
	
	static final int LOADING = 0;
	static final int GAME = 2;
	
	public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
	public float MUSIC_VOLUME = DEFAULT_MUSIC_VOLUME;
	
	int screen;
	
	float inputDelay, timeSinceResume;
	public float x, y;
			
	public boolean justTouched, isTouched, initializationDone, portrait = true;
	
	public Main(com.picke.utils.Communicator com){
		this.com = com;
	}

	
	@Override
	public void create () {
		prefs = Gdx.app.getPreferences("chicken");
		b = new SpriteBatch();
		b.getProjectionMatrix().setToOrtho2D(0, 0, 480, 800);
		a = new Assets(b);
		
		Tween.registerAccessor(com.picke.utils.Tweenable.class, new TweenableAccessor());
		tweenManager = new TweenManager();
		adRenderer = new ShapeRenderer();
		g = new Game(this);
		redirectManager = new RedirectManager(this);
		
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
	}

	@Override
	public void render () {
		float delta = Math.min(0.04f, Gdx.graphics.getDeltaTime());
		x = Gdx.input.getX()*480f/Gdx.graphics.getWidth();
		y = (Gdx.graphics.getHeight()-Gdx.input.getY())*800f/Gdx.graphics.getHeight();
		justTouched = Gdx.input.justTouched();
		isTouched = Gdx.input.isTouched();
		
		if(PRINT_COORDS && justTouched)
			System.out.println("x: "+(int)x+" y: "+(int)y);
		
		tweenManager.update(delta);
		inputDelay -= delta;
			
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(a.music != null){ 
			if(g.musicOn && a.music.getVolume() < MUSIC_VOLUME){
				a.music.setVolume(a.music.getVolume()+delta/4f);
				if(a.music.getVolume() > MUSIC_VOLUME)
					a.music.setVolume(MUSIC_VOLUME);
			}
			
			boolean activeMusicPlaying = a.music.isPlaying();
			
			if(g.musicOn && !activeMusicPlaying)
				a.music.play();

			if(!g.musicOn && activeMusicPlaying)
				a.music.pause();
		}
		
		if(screen != LOADING) g.autoSaveTimer -= delta;
		g.adTimer -= delta;
		
		switch(screen){
		case LOADING:
			a.update();
			if(a.done){
				postConstruct();
				screen = GAME;
			}
			break;
		case GAME:
			if(REDIRECTS) redirectManager.update(delta);
			if(!redirectManager.active)
				g.update(delta);
			g.draw();
			g.transition.draw();
			drawDebug();
			redirectManager.draw();
			timeSinceResume += delta;
			break;
		}
	}
	
	public void drawTexture(TextureRegion text, float x, float y){
		drawTexture(text, x, y, false, false, 1, 0);
	}
	
	public void drawTexture(TextureRegion text, float x, float y, float scale, float rotation){
		drawTexture(text, x, y, false, false, scale, rotation);
	}
	
	public void drawTexture(TextureRegion text, float x, float y, boolean flipX, boolean flipY, float scale, float rotation){
		b.draw(text, x - a.w(text)/2f, y - a.h(text)/2f,
				a.w(text)/2f, a.h(text)/2f,
				a.w(text), a.h(text),
				scale * (flipX ? -1 : 1), scale * (flipY ? -1 : 1), rotation);
	}
	
	void drawDebug(){
		if(!TEST_ADS) return;
		adRenderer.getProjectionMatrix().setToOrtho2D(0, 0, (portrait ? 480 : 800), (portrait ? 800 : 480));
		adRenderer.updateMatrices();
		adRenderer.begin(ShapeType.Filled);
		adRenderer.setColor(Color.RED);
		adRenderer.rect(0, (portrait ? 710 : 430), (portrait ? 480 : 800), (portrait ? 90 : 50));
		adRenderer.end();
	}
	
	void postConstruct(){
		initializationDone = true;
		com.postConstruct(g.removedAds);
		g.postConstruct();
	}
	
	@Override
	public void resume(){
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		
		com.cancelNotification();
		
		if(!initializationDone) return;

		redirectManager.downloader.onResume();
	}
	
	@Override
	public void resize(int width, int height){
		Tools.setScaling(portrait);
		if(g != null && portrait && timeSinceResume > 10) g.showInterstitial();
	}
	
	@Override
	public void pause(){
		if(!initializationDone) return;
		
		timeSinceResume = 0;
		redirectManager.downloader.onPause();
		g.saveData();
		prefs.flush(); // The last thing we do when the app loses focus is to save all our data
	}
	
	@Override
	public void dispose(){
		a.dispose();
	}
}
