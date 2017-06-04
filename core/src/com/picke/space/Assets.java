package com.picke.space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class Assets {
	
	SpriteBatch batch;
	public boolean done;
	AssetManager manager;
	TextureAtlas items, loadingA;

	public BitmapFont font;
	
	public Texture bkT, fontT, whiteT, blackT;

	TextureAtlas smokeA, new_evolutionD, tutorialA;

	public TextureRegion loadingBackgroundR, blackR, loadingBarR, whiteR, ratePromptR, bkR, menuR, poopR, countdownBarR, countdownBkR, countdownCrateR, countdownStripeR, moypediaBkR, moypediaBuyR, moypediaCloseR,
	moypediaShadowR, moypediaUnknownR, settingsBkR, settingsLinksR, settingsMusicR, settingsNotificationsR, settingsSoundR, UIR, arrowR, categoryHighlightR;
	;

	public TextureRegion[]
			evolutionR = new TextureRegion[30],
			coinR = new TextureRegion[20],
			titleR = new TextureRegion[6],
			pediaR = new TextureRegion[30]
	;

	SkeletonData smokeD, new_evolutionA, tutorialD;

	public Sound coinS, evolveS, fartS, impactS, new_evolutionS, swoshS;
	
	public Music music, themeMusic;
	
	public Assets(SpriteBatch b){
		batch = b;
		manager = new AssetManager();
		
		loadingA = new TextureAtlas("loading/items.atlas");
		loadingBackgroundR = loadingA.findRegion("background");
		loadingBarR = loadingA.findRegion("bar");
		
		manager.load("font.png", Texture.class);
		manager.load("white.png", Texture.class);
		manager.load("black.png", Texture.class);
		manager.load("items.atlas", TextureAtlas.class);
		manager.load("new_evolution/skeleton.atlas", TextureAtlas.class);
		manager.load("evolution/skeleton.atlas", TextureAtlas.class);
		manager.load("tutorial/skeleton.atlas", TextureAtlas.class);

		manager.load("sounds/coin.mp3", Sound.class);
		manager.load("sounds/evolve.mp3", Sound.class);
		manager.load("sounds/fart.mp3", Sound.class);
		manager.load("sounds/impact.mp3", Sound.class);
		manager.load("sounds/new_evolution.mp3", Sound.class);
		manager.load("sounds/swosh.mp3", Sound.class);
//		manager.load("music/music0.mp3", Music.class);

//		"sounds4/rim.mp3", Sound.class);
	}

	public void update(){
		drawLoading();
		if(manager.update()){
			createTextures();
			disposeLoading();
			done = true;
		}
	}
	
	void drawLoading(){
		batch.begin();
		batch.disableBlending();
		batch.draw(loadingBackgroundR, 0, 0);
		batch.enableBlending();
		
		batch.draw(loadingBarR, 56.2f, 203.4f, w(loadingBarR)*manager.getProgress(), h(loadingBarR));
		batch.end();
	}
	
	void createTextures(){
		fontT = manager.get("font.png", Texture.class);
		fontT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion fontR = new TextureRegion(fontT, 512, 512);
		font = new BitmapFont(Gdx.files.internal("font.fnt"), fontR, false);
		
		/* Create white overlay */
		whiteT = manager.get("white.png", Texture.class);
		whiteR = new TextureRegion(whiteT, 2, 2);

		blackT = manager.get("black.png", Texture.class);
		blackR = new TextureRegion(blackT, 2, 2);

		items = manager.get("items.atlas", TextureAtlas.class);

		smokeA = manager.get("evolution/skeleton.atlas", TextureAtlas.class);
		smokeD = createSkeletonData(smokeA, "evolution", 1);

		new_evolutionD = manager.get("new_evolution/skeleton.atlas", TextureAtlas.class);
		new_evolutionA = createSkeletonData(new_evolutionD, "new_evolution", 1);

		tutorialA = manager.get("tutorial/skeleton.atlas", TextureAtlas.class);
		tutorialD = createSkeletonData(tutorialA, "tutorial", 1);

		countdownBarR = items.findRegion("countdownBar");
		countdownBkR = items.findRegion("countdownBk");
		countdownCrateR = items.findRegion("countdownCrate");
		countdownStripeR = items.findRegion("countdownStripe");
		moypediaBkR = items.findRegion("moypediaBk");
		moypediaBuyR = items.findRegion("moypediaBuy");
		moypediaCloseR = items.findRegion("moypediaClose");
		moypediaShadowR = items.findRegion("moypediaShadow");
		moypediaUnknownR = items.findRegion("moypediaUnknown");
		settingsBkR = items.findRegion("settingsBk");
		settingsLinksR = items.findRegion("settingsLinks");
		settingsMusicR = items.findRegion("settingsMusic");
		settingsNotificationsR = items.findRegion("settingsNotifications");
		settingsSoundR = items.findRegion("settingsSound");
		categoryHighlightR = items.findRegion("categoryHighlight");
		UIR = items.findRegion("UI");
		poopR = items.findRegion("poop");
		arrowR = items.findRegion("arrow");

		loadArray(evolutionR, "evolution");
		loadArray(titleR, "title");
		loadArray(coinR, "coin");
		loadArray(pediaR, "pedia");

		coinS = manager.get("sounds/coin.mp3", Sound.class);
		evolveS = manager.get("sounds/evolve.mp3", Sound.class);
		fartS = manager.get("sounds/fart.mp3", Sound.class);
		impactS = manager.get("sounds/impact.mp3", Sound.class);
		new_evolutionS = manager.get("sounds/new_evolution.mp3", Sound.class);
		swoshS = manager.get("sounds/swosh.mp3", Sound.class);

		loadBackground(0);
	}

	public void loadBackground(int index){
		if(bkT != null) bkT.dispose();
		bkT = new Texture("backgrounds/bk"+index+".png");
		bkR = new TextureRegion(bkT, 480, 800);
	}

	SkeletonData getSkeletonData(String name, float scale){
		TextureAtlas atlas = manager.get(name+"/skeleton.atlas", TextureAtlas.class);
		SkeletonJson json = new SkeletonJson(atlas);
		json.setScale(scale);
		return json.readSkeletonData(Gdx.files.internal(name+"/skeleton.json"));
	}
	
	public SkeletonData createSkeletonData(TextureAtlas atlas, String name, float scale){
		SkeletonJson json = new SkeletonJson(atlas);
		json.setScale(scale);
		return json.readSkeletonData(Gdx.files.internal(name+"/skeleton.json"));
	}
	
	public void setThemeMusic(){
		music = themeMusic;
	}
	
	public void disposeMusic(){
		if(music != null && music != themeMusic) music.dispose();
	}

	
	void loadArray(TextureRegion[] array, String name){
		for(int i = 0; i < array.length; i++)
			array[i] = items.findRegion(name+i);
	}
	
	public float w(TextureRegion t){
		return t.getRegionWidth();
	}
	
	public float h(TextureRegion t){
		return t.getRegionHeight();
	}
	
	public void dispose(){
		manager.clear();
	}
	
	void disposeLoading(){
		loadingA.dispose();
	}
}

