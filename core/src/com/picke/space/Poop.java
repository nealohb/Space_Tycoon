package com.picke.space;


import com.badlogic.gdx.math.MathUtils;
import com.picke.utils.Tools;

public class Poop extends BaseClass{
    float posX, posY, scale, deg, origX, origY, alpha, poopAngle, poopDst;
    Poop(Game g, float posX, float posY){
        super(g);
        this.posX = origX = posX;
        this.posY = origY = posY;
        alpha = 4;
        poopDst = MathUtils.random(30, 55);
        poopAngle = MathUtils.random(45, 135);
    }
    void update(float delta){
        alpha -= delta;
        deg += delta*150;
        scale = 0.9f+MathUtils.sinDeg(deg)*0.1f;
        if(Tools.dst(posX, posY, origX, origY) < poopDst){
            posX += delta*120*MathUtils.cosDeg(poopAngle);
            posY += delta*120*MathUtils.sinDeg(poopAngle);
        }
    }
    void draw(){
        float al = alpha;
        al = MathUtils.clamp(al, 0, 1);
        b.setColor(1, 1, 1, al);
        m.drawTexture(a.poopR, posX, posY, false, false, scale, 0);
        b.setColor(1, 1, 1, 1);
    }
}
