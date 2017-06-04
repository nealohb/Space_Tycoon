package com.picke.space;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BaseClass {
	protected Assets a;
	protected Game g;
	protected SpriteBatch b;
	protected Main m;
	protected Preferences prefs;
	protected com.picke.utils.Communicator com;
	
	protected BaseClass(Game g){
		this.g = g;
		m = g.m;
		b = g.b;
		com = g.com;
		a = g.a;
		prefs = g.prefs;
	}
	
	protected BaseClass(Main m){
		this.m = m;
		b = m.b;
		a = m.a;
		com = m.com;
		prefs = m.prefs;
	}
}
