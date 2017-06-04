package com.picke.space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.picke.utils.Coin;
import com.picke.utils.ConfirmInterface;
import com.picke.utils.Language;
import com.picke.utils.Links;
import com.picke.utils.Misc;
import com.picke.utils.SpineObject;
import com.picke.utils.Tools;
import com.picke.utils.Transition;
import com.picke.utils.TransitionListener;

import java.util.Comparator;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import static com.badlogic.gdx.Gdx.gl;
import static com.picke.space.Game.CATEGORY.CLOSED;
import static com.picke.space.Game.CATEGORY.EVOLUTION;
import static com.picke.space.Game.CATEGORY.MOYPEDIA;
import static com.picke.space.Game.CATEGORY.SETTINGS;

public class Game implements TransitionListener {
	public SpriteBatch b;
	public Assets a;
	public com.picke.space.Main m;

	public Preferences prefs;
	public com.picke.utils.Communicator com;
	public Transition transition;

	SpineObject evolution, tutorial;
	
	public float delta, x, y, prevCatMenuPos, autoSaveTimer = 30;
	public float adTimer = Main.ADS_INITIAL_CD;

	private float evolutionMenuPos, moypediaMenuPos;

	public int coins;
	float coinT, timePlayed, income;

	public int world;


	
	public static final int RIGHT = 0;
	public static final int LEFT = 1;

	public boolean soundOn, removedAds;
	boolean rated, menuTweenActive, shownInstructions, isTouched, justTouched, showRatePrompt, musicOn, notificationsOn, twoHoursRated;
	
	public SkeletonRenderer renderer;

	com.picke.utils.Tweenable menuTween;

	Sort sorter;

	Pet petBeingDragged;

	enum CATEGORY {EVOLUTION, MOYPEDIA, SETTINGS, CLOSED};

	CATEGORY category = CLOSED;

	public static final int WORLDS = 6;
	int coinF, worldsUnlocked = WORLDS;

	float spawnCD, menuPos, flingVel, sortT, yOffset = 0;

	int evolutionsUnlocked, petToPurchase;

	com.picke.utils.Tweenable petopediaTween, promptTween, promptTutorial;
	
	Circle promptAccept = new Circle(240, 272, 55);
	Circle promptDeny = new Circle(387, 444, 35);
	
	Circle closeSettingsCirc = new Circle(374, 512, 40);
	
	GestureDetector detector;
	
	Circle soundCirc = new Circle(142, 393, 35);
	Circle musicCirc = new Circle(240, 393, 35);
	Circle notificationsCirc = new Circle(332, 393, 35);
	
	Circle fbCirc = new Circle(142, 293, 40);
	Circle rateCirc = new Circle(236, 293, 40);

	float TITLE_GAP = 100;
	Circle changeLeft = new Circle(240-TITLE_GAP, 655, 40);
	Circle changeRight = new Circle(240+TITLE_GAP, 655, 40);

	Rectangle evolutionRect = new Rectangle(160, 0, 110, 59);
	Rectangle moypediaRect = new Rectangle(270, 0, 110, 59);
	Rectangle settingsRect = new Rectangle(380, 0, 110, 59);
	Rectangle closeMenuRect = new Rectangle(430, 560, 50, 50);

	public Array<Pet> pets = new Array<Pet>();
	Array<Poop> poops = new Array<Poop>();
	
	public Array<Coin> coinArray = new Array<Coin>();
	
	Game(com.picke.space.Main m){
		this.m = m;
		this.a = m.a;
		this.b = m.b;
		this.com = m.com;
		this.prefs = m.prefs;
		renderer = new SkeletonRenderer();
		menuTween = new com.picke.utils.Tweenable();
		
		transition = new Transition(this);
		transition.addListener(this);
		transition.setSpeed(5);
		transition.setBlackoutDuration(0.1f);
		
		loadData();
	}
	
	void postConstruct(){
		
		// display a rate our game prompt if our user has been playing for more than 30 mins and still hasn't rated the app. Also the gift can't be active
		if(timePlayed >= 60*30 && !rated){
			rated = true;
			showRatePrompt = true;
		}
		
		// second rate prompt after two hours
		if(timePlayed >= 60*60*2 && !twoHoursRated){
			showRatePrompt = true;
			twoHoursRated = true;
		}

		petopediaTween = new com.picke.utils.Tweenable();
		promptTween = new com.picke.utils.Tweenable();
		promptTutorial = new com.picke.utils.Tweenable();
		sorter = new Sort();

		evolution = new SpineObject(this, a.new_evolutionA);
		evolution.setPosition(240, 400);

		tutorial = new SpineObject(this, a.tutorialD);
		tutorial.setPosition(240, 400);
		tutorial.setAnimation("tutorial0", true);
		
		/**
		 * TODO Temporary while creating rooms
		 */
		detector = new GestureDetector(adapter);
		Gdx.input.setInputProcessor(detector);
	}

	void loadData(){
		if(!prefs.contains("soundOn")){
			soundOn = true;
			musicOn = true;
			notificationsOn = true;
			coins = 530;
		}
		else{
			rated = prefs.getBoolean("rated");
			twoHoursRated = prefs.getBoolean("twoHoursRated");
			soundOn = prefs.getBoolean("soundOn");
			musicOn = prefs.getBoolean("musicOn");
			notificationsOn = prefs.getBoolean("notificationsOn");
			coins = prefs.getInteger("coins");
			removedAds = prefs.getBoolean("removedAds");
			timePlayed = prefs.getFloat("timePlayed");
		}

		shownInstructions = prefs.getBoolean("shownInstructions");
		evolutionsUnlocked = prefs.getInteger("evolutionsUnlocked");
		for(int i = 0; i < prefs.getInteger("petSize"); i++){
			Pet p = new Pet(this);
			int level = prefs.getInteger("petLevel"+i);
			float posX = prefs.getFloat("petX"+i);
			float posY = prefs.getFloat("petY"+i);
			p.setSavedValues(level, posX, posY);
			pets.add(p);
		}
		
		/**
		 * Debug
		 */
		if(Main.INFINITE_WEALTH){
			coins = 999999;
		}
	}

	void update(float delta){
		this.delta = delta;
		justTouched = m.justTouched;
		isTouched = m.isTouched;
		x = m.x;
		y = m.y;

		transition.update(delta);

		timePlayed += delta;

		if(showRatePrompt){
			updateRatePrompt();
			return;
		}

		updateCurrencyVisuals();

		if(evolution.active()){
			if(evolution.animationActive("idle") && justTouched) evolution.clearAnimations();
			return;
		}

		updateSwipe();

		for(int i = pets.size-1; i >= 0; i--){
			Pet p = pets.get(i);
			p.update(delta);
			if(p.inActive){
				pets.removeIndex(i);
			}
		}

		if(justTouched){
			if(changeLeft.contains(x, y))
				changeWorld(LEFT);
			else if(changeRight.contains(x, y))
				changeWorld(RIGHT);
			else if(evolutionRect.contains(x, y)){
				toggleMenu(EVOLUTION);
			}
			else if(moypediaRect.contains(x, y)){
				toggleMenu(MOYPEDIA);
			}
			else if(settingsRect.contains(x, y)){
				toggleMenu(SETTINGS);
			}
			else if(menuTween.getValue() == MENU_DST && closeMenuRect.contains(x, y)){
				toggleMenu(category);
			}
		}


		for(int i = poops.size-1; i >= 0; i--){
			Poop p = poops.get(i);
			p.update(delta);
			if(p.alpha <= 0)
				poops.removeIndex(i);
		}


		updateDrawOrder();

		spawnPets();

		handleExitButton();

	}

	private void changeWorld(int reference) {
		transition.start(reference);
	}

	private void spawnPets() {
		if(noobPetCount() < 10) spawnCD -= delta;
		if(spawnCD < 0){
			spawnCD = MathUtils.random(2.8f, 3.6f); // give it random numbers so that the tweens dont sync lol
			pets.add(new Pet(this));
		}
	}

	public void dropPoop(float posX, float posY) {
		poops.add(new Poop(this, posX, posY));
		playSound(a.fartS, 1);
	}

	void updateRatePrompt(){
		if(m.justTouched){
			if(promptAccept.contains(x, y)){
				twoHoursRated = true;
				showRatePrompt = false;
				com.openURL(Links.gameMarket);
			}
			else if(promptDeny.contains(x, y)){
				showRatePrompt = false;
			}
		}
	}
	
	
	public void saveData(){

		prefs.putBoolean("soundOn", soundOn);
		prefs.putBoolean("rated", rated);
		prefs.putInteger("coins", coins);
		prefs.putBoolean("twoHoursRated", twoHoursRated);
		prefs.putBoolean("removedAds", removedAds);
		prefs.putBoolean("musicOn", musicOn);
		prefs.putBoolean("notificationsOn", notificationsOn);
		prefs.putFloat("timePlayed", timePlayed);

		prefs.putBoolean("shownInstructions", shownInstructions);
		prefs.putInteger("evolutionsUnlocked", evolutionsUnlocked);
		prefs.putInteger("petSize", pets.size);
		for(int i = 0; i < pets.size; i++){
			Pet p = pets.get(i);
			prefs.putInteger("petLevel"+i, p.level);
			prefs.putFloat("petX"+i, p.bounds.x);
			prefs.putFloat("petY"+i, (p.falling ? p.dropY : p.bounds.y));
		}
	}
	
	public void showInterstitial(){
		if(removedAds) return;
		
		if(adTimer < 0){
			com.showInterstitial();
			adTimer = Main.ADS_CD;
		}
	}
	
	void draw(){

		b.begin();

		b.disableBlending();
		b.draw(a.bkR, 0, 0);
		b.enableBlending();

		for(Poop p : poops)
			p.draw();

		for(Pet p : pets)
			if(p != petBeingDragged && !p.falling)
				p.draw();

		for(Pet p : pets)
			if(p != petBeingDragged && p.falling)
				p.draw();

		if(petBeingDragged != null) petBeingDragged.draw();


		// UI
		drawUI();


		// Title
		m.drawTexture(a.titleR[world], 240, changeLeft.y);
		m.drawTexture(a.arrowR, changeLeft.x, changeLeft.y, true, false, 1, 0);
		m.drawTexture(a.arrowR, changeRight.x, changeRight.y);

		if(evolution.active()){
			b.setColor(1, 1, 1, 0.7f);
			b.draw(a.blackR, 0, 0, 480, 800);
			b.setColor(Color.WHITE);
			evolution.render(delta);
			m.drawTexture(a.evolutionR[evolutionsUnlocked], 240, 400, false, false, evolution.getSkel().findBone("Text_Evo").getScaleY(), 0);
		}
		
		if(showRatePrompt){
			b.setColor(1, 1, 1, 0.7f);
			b.draw(a.blackR, 0, 0, 480, 800);
			b.setColor(1, 1, 1, 1);
			b.draw(a.ratePromptR, 240 - a.w(a.ratePromptR)/2, 220);
		}
		b.end();

		transition.draw();
	}

	private void drawUI() {
		b.setColor(173/255f, 172/255f, 163/255f, 1);
		b.draw(a.whiteR, 0, 0, 480, menuTween.getValue());
		b.setColor(Color.WHITE);
		m.drawTexture(a.UIR, 240, a.h(a.UIR)/2f+menuTween.getValue());
		if(menuTween.getValue() > 0) m.drawTexture(a.moypediaCloseR, 480-a.w(a.moypediaCloseR)/2f + (100-100*menuTween.getValue()/MENU_DST), 59+a.h(a.moypediaCloseR)/2f+menuTween.getValue());

		switch(category){
			case EVOLUTION:
				
				drawCategoryHighlight(162);
				break;
			case MOYPEDIA:
				b.flush();
				gl.glEnable(GL20.GL_SCISSOR_TEST);
				gl.glScissor(0, 0, (int) (480*Tools.Sx), (int) (menuTween.getValue()*Tools.Sy));
				for(int i = 0; i < a.pediaR.length; i++)
					m.drawTexture( (i < 999 ? a.pediaR[i]:a.moypediaUnknownR), 240, 496+menuPos-a.h(a.pediaR[i])/2f-108*i-500+menuTween.getValue());
				b.end();
				gl.glDisable(GL20.GL_SCISSOR_TEST);

				b.begin();
				m.drawTexture(a.moypediaShadowR, 240, a.h(a.moypediaShadowR)/2f-500+menuTween.getValue());
				drawCategoryHighlight(270);
				break;
			case SETTINGS:
				m.drawTexture(a.settingsLinksR, 240, a.h(a.settingsLinksR)/2f-500+menuTween.getValue());

				for(int i = 0; i < 3; i++)
				m.drawTexture(a.settingsBkR, 240, (176+54+108*i)-500+menuTween.getValue());

				m.drawTexture(a.settingsSoundR, 57, (176+54+108*2)-500+menuTween.getValue());
				m.drawTexture(a.settingsNotificationsR, 57, (176+54+108)-500+menuTween.getValue());
				m.drawTexture(a.settingsMusicR, 57, (176+54)-500+menuTween.getValue());
				drawCategoryHighlight(380);
				break;
		}

		drawCoins();
	}

	private void drawCategoryHighlight(int startX) {
		b.setColor(1, 1, 1, menuTween.getValue()/MENU_DST);
		m.drawTexture(a.categoryHighlightR, startX+a.w(a.categoryHighlightR)/2f, 501+a.h(a.categoryHighlightR)/2f-500+menuTween.getValue());
		b.setColor(Color.WHITE);
	}

	private void updateSettings(){
		if(m.justTouched){
//			if(closeSettingsCirc.contains(x, y))
//				toggleSettings();
			if(soundCirc.contains(x, y))
				soundOn = !soundOn;
			else if(musicCirc.contains(x, y))
				musicOn = !musicOn;
			else if(notificationsCirc.contains(x, y))
				notificationsOn = !notificationsOn;
			else if(rateCirc.contains(x, y)){
				com.openURL(Links.gameMarket);
				twoHoursRated = true;
				rated = true;
			}
			else if(fbCirc.contains(x, y))
				com.openURL(Links.facebook);
//			else if(nameCirc.contains(x, y))
//				setName(false);
		}
	}



	static final int MENU_DST = 500;
	public void toggleMenu(CATEGORY newCategory) {
		if(menuTweenActive) return;

		if(newCategory != category && menuTween.getValue() > 0){
			category = newCategory;
			return;
		}

		category = newCategory;

		boolean active = menuTween.getValue() > 0;
		menuTweenActive = true;
		Tween.to(menuTween, 0, .7f)
		.target(active ? 0 : MENU_DST)
		.ease(active ? TweenEquations.easeInOutQuint : TweenEquations.easeInOutQuint)
		.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if(type == TweenCallback.COMPLETE) {
					menuTweenActive = false;
					if (menuTween.getValue() == MENU_DST) {
						evolutionRect.y = moypediaRect.y = settingsRect.y = 500;
					} else {
						evolutionRect.y = moypediaRect.y = settingsRect.y = 0;
						category = CLOSED;
					}
				}
			}
		})
		.start(m.tweenManager);
	}
	
	public void drawCoins(){
		m.drawTexture(a.coinR[coinF], 24, 58+menuTween.getValue(), .7f, 0);
		a.font.getData().setScale(0.4f);
		a.font.setColor(Color.WHITE);
		a.font.draw(b, Integer.toString(coins), 67, 67+menuTween.getValue());
	}
	
	public void addCoins(int coins){
		this.coins += coins;
	}
	
	void updateCurrencyVisuals(){
		coinT += delta;
		if(coinT >= Misc.COIN_CD){
			coinT = 0;
			coinF++;
			if(coinF > 19) coinF = 0;
		}
	}
	
	@Override
	public void transitionDone(int reference) {
//		System.out.println(transition.getAlpha());
		switch(reference){
			case LEFT:
				world--;
				if(world < 0) world = worldsUnlocked-1;
				break;
			case RIGHT:
				world++;
				if(world >= worldsUnlocked) world = 0;
				break;
		}

		a.loadBackground(world);
		changeLeft.x = 240 - (a.w(a.titleR[world])/2f+31.5f);
		changeRight.x = 240 + (a.w(a.titleR[world])/2f+31.5f);
	}
	
	void autoSave(){
		if(autoSaveTimer <= 0){
			saveData();
			prefs.flush();
			autoSaveTimer = 30;
		}
	}


	GestureDetector.GestureAdapter adapter = new GestureDetector.GestureAdapter(){
		public boolean tap(float xx, float yy, int count, int button){
			if(menuTween.getValue() == 0) {
				for (Pet p : pets) {
					if (p.bounds.contains(x, y) && !p.falling && p.level >= 2) {
						dropPoop(p.bounds.x, p.bounds.y);
						break;
					}
				}
			}
			flingVel = 0;
			if(menuTween.getValue() == MENU_DST && y < 500) {
				int slot = MathUtils.floor((y - menuPos) / 108f);
				System.out.println("slot: "+slot);
			}
//				buyPet(slot);
			return false;
		}
		public boolean pan(float xx, float yy, float deltaX, float deltaY){
			if(menuTween.getValue() == 0) {
				for (int i = pets.size - 1; i >= 0; i--) {
					Pet p = pets.get(i);
					if (p.bounds.contains(x, y) && petBeingDragged == null && !p.falling) {
						p.dragable.startDraging();
						petBeingDragged = p;
						break;
					}
				}
			}
			else if(menuTween.getValue() == MENU_DST && y < 500)
				menuPos -= deltaY / Tools.Sy;
			return false;
		}
		public boolean fling(float velocityX, float velocityY, int button){
			if(menuTween.getValue() == MENU_DST && y < 500)
				flingVel = velocityY/35f/Tools.Sy;
			return false;
		}
		public boolean touchDown(float xx, float yy, int pointer, int button){
				flingVel = 0;
			return false;
		}
	};

	void updateSwipe(){
		if(flingVel > 3.5f || flingVel < -3.5f)
			menuPos -= flingVel;

		flingVel *= 0.93f;

		if(menuPos < 0){
			menuPos = 0;
			flingVel = 0;
		}
		else if(menuPos > a.pediaR.length*108 - 496){
			menuPos = a.pediaR.length*108 - 496;
			flingVel = 0;
		}
	}

	void updateDrawOrder(){
		sortT -= delta;
		if(sortT < 0) {
			sortT = .3f;
			sorter.sort(pets, new Comparator<Pet>() {
				public int compare(Pet pet0, Pet pet1) {
					return (int) ( (pet1.bounds.y-pet1.height/2f) - (pet0.bounds.y-pet0.height/2f));
				}
			});
		}
	}

	int noobPetCount(){
		int c = 0;
		for(Pet p : pets)
			if(p.level == 0)
				c++;
		return c;
	}

	private void handleExitButton() {
		if(Gdx.input.isKeyPressed(Keys.BACK) && m.inputDelay < 0){
			m.inputDelay = 0.5f;
			m.com.confirm(new ConfirmInterface(){
				@Override
				public void yes() {
					Gdx.app.exit();
				}

				@Override
				public void no() {
				}

			}, Language.EXIT);
		}
	}

	public void playSound(Sound s){
		playSound(s, 1);
	}

	public void playSound(Sound s, float vol){
		if(soundOn) s.play(vol);
	}
}
