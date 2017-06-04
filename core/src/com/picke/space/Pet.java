package com.picke.space;


import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.picke.utils.DragableListener;
import com.picke.utils.DragableObject;
import com.picke.utils.SpineObject;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

public class Pet extends BaseClass{
    DragableObject dragable;
    boolean inActive, falling, isTouched, justTouched;
    float x, y, minX, delta, maxX, minY, maxY, touchedT, width, height, dropY, velocityY, angle, angleT;
    int level;
    com.picke.utils.Tweenable tween;
    SpineObject spine;
    Circle bounds = new Circle();
    static final float GRAVITY = 15;

    Pet(Game g){
        super(g);
        setSize();
        setSpine();
        bounds.x = MathUtils.random(minX, maxX);
        bounds.y = 900;

        tween = new com.picke.utils.Tweenable();
        falling = true;
        dropY = MathUtils.random(minY, maxY);
        dragable = new DragableObject(g, bounds.x, bounds.y);
        dragable.setListener(listener);
    }
    void setSpine(){
            spine = new SpineObject(g, a.smokeD);
            spine.setListener(new com.picke.utils.SpineListener(){
                @Override
                public void onEvent(String event) {
                    //					System.out.println("?");
                    if(event.equals("change")){
                        level++;
                        setSize();
                        setAnimation();

                        if(level > g.evolutionsUnlocked){
                            g.evolutionsUnlocked = level;
                            if(g.evolutionsUnlocked > 0){
								g.playSound(a.new_evolutionS, 1);
                                g.evolution.setAnimation("evolve", false);
                                g.evolution.addAnimation("idle", true);
                            }
                        }
                    }
                }
            });
    }
    void setSavedValues(int level, float posX, float posY) {
        this.level = level;
        setAnimation();
        falling = false;
        bounds.x = posX;
        bounds.y = posY;
    }
    void setAnimation(){
        if(level < 1){
            m.tweenManager.killTarget(tween);
            tween.setValue(0);
            Tween.to(tween, 0, MathUtils.random(0.5f, 0.7f))
                    .target(MathUtils.random(-.1f, .1f))
                    .delay(MathUtils.random(2, 4))
                    .repeatYoyo(Tween.INFINITY, 0)
                    .ease((MathUtils.randomBoolean() ? TweenEquations.easeOutBack : TweenEquations.easeInBack))
                    .start(m.tweenManager);
        }
        else{
            m.tweenManager.killTarget(tween);
            tween.setValue(0);
            Tween.to(tween, 0, MathUtils.random(0.4f, 0.8f))
                    .target(MathUtils.random(0.8f, 1.2f))
                    .repeatYoyo(Tween.INFINITY, 0)
                    .ease(TweenEquations.easeOutBack)
                    .start(m.tweenManager);
        }
    }
    void setSize(){
        width = a.w(a.evolutionR[level]);
        height = a.h(a.evolutionR[level]);
        minX = width/2f;
        maxX = 480-width/2f;
        minY = 97+height/2f;
        maxY = 457-height/2f;
        bounds.radius = Math.max(width, height)/2f+10; // some extra size so it's not impossible to grab on small devices
    }
    void update(float delta){
        this.delta = delta;
        x = m.x;
        y = m.y;
        isTouched = m.isTouched;
        justTouched = m.justTouched;

        float dragY = y, petopediaY = 170;

        if(g.petopediaTween.getValue() > 0 && dragY < petopediaY)
            dragY = petopediaY;
        else if(dragY > maxY) // -(height/2f+yOffset) TODO If we want to add offset to pet evolve, add these back (subtract maxY)
            dragY = maxY; // -(height/2f+yOffset)

        dragable.update(delta, x, dragY, isTouched); //  + height/2f+yOffset  (add to dragY)

        if(dragable.draging()){
            bounds.x = dragable.getX();
            bounds.y = dragable.getY();
        }

        if(falling){
            if(velocityY > -600) // limits fall speed
                velocityY -= delta*30*GRAVITY;
            bounds.y += delta*velocityY;
            if(bounds.y < dropY){
                bounds.y = dropY;
                falling = false;
//					g.playSound(a.evolve_impactS, 1);
                setAnimation();
                spine.setPosition(bounds.x, bounds.y-20);
                spine.setAnimation("fallSmoke", false);
            }
        }
        else if(!dragable.draging() && level >= 1){ // move them slimey bowsacks!
            move();
        }
    }
    private void move() {
        float spd = 0.8f-tween.getValue();
        if(spd < 0) spd = 0;

        bounds.x += delta*60*spd*MathUtils.cosDeg(angle);
        bounds.y += delta*60*spd*MathUtils.sinDeg(angle);

        bounds.x = MathUtils.clamp(bounds.x, minX, maxX);
        bounds.y = MathUtils.clamp(bounds.y, minY, maxY);

        angleT -= delta;
        if(angleT < 0){
            angleT = MathUtils.random(5f, 12f);
            angle = MathUtils.random(360);
        }
    }


    DragableListener listener = new DragableListener(){
        @Override
        public void releasedObject(float x, float y) {
            g.petBeingDragged = null;
            checkForEvolution(x, y);
        }
    };

    void checkForEvolution(float x, float y){
        for(Pet p : g.pets){
            if(p != this && p.bounds.contains(x, y) && p.level == level && !p.falling && level < a.evolutionR.length-1){
                spine.clearAnimations();
                spine.setPosition(bounds.x, bounds.y);
                spine.setAnimation("levelUp", false);
//					g.playSound(a.evolveS, 1);
                p.inActive = true;
                break;
            }
        }
    }

    void draw(){
        if(level < 1){ // crate
            b.draw(a.evolutionR[level], bounds.x - width/2f, bounds.y - height/2f,
                    width/2f, 0,
                    width, height,
                    1, 1+tween.getValue(), 1);
        }
        else{
            b.draw(a.evolutionR[level], bounds.x - width/2f, bounds.y - height/2f,
                    width/2f, 0,
                    width, height,
                    0.8f+(1-tween.getValue())*0.2f, 0.4f+tween.getValue()*0.4f, 0);
        }

        if(spine.active()) spine.render(delta);
    }
}
