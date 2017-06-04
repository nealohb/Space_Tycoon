package com.picke.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;

public class Tools {
	/**
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return Distance between two points 
	 */
	public static float dst(float x0, float y0, float x1, float y1){
		return (float) Math.sqrt((x0-x1)*(x0-x1)+(y0-y1)*(y0-y1));
	}

	/**
	 * 
	 * @param startX
	 * @param startY
	 * @param targetX
	 * @param targetY
	 * @return
	 */
	public static float getAngle(float startX, float startY, float targetX, float targetY) {
		float angle = MathUtils.atan2(targetY - startY, targetX - startX)*MathUtils.radiansToDegrees;
		if(angle < 0)
			angle += 360;
		return angle;
	}
	
	public static void loadArray(TextureAtlas atlas, TextureRegion[] array, String name){
		for(int i = 0; i < array.length; i++)
			array[i] = atlas.findRegion(name+i);
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String getFormatedTime(int time){
		int seconds = (int) (time) % 60 ;
		int minutes = (int) (((time) / (60)) % 60);
		int hours   = (int) (((time) / (60*60)) % 24);
		int days = (int) (((time) / (60*60*24)));
		String tString = seconds+"s";
		if(time >= 60){
			if(days > 0){
				if(hours > 0)
					tString = days+"d "+hours+"h";
				else
					tString = days+"d";
			}
			else if(hours > 0){
				if(minutes > 0)
					tString = hours+"h "+minutes+"m";
				else
					tString = hours+"h";
			}
			else{
				if(seconds > 0)
					tString = minutes+"m "+seconds+"s";
				else
					tString = minutes+"m";
			}
		}
		return tString;
	}

	
	/**
	 * 
	 * @param imagePath
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param portrait
	 */
	public static boolean captureScreen(String imagePath, int startX, int startY, int width, int height, boolean portrait){
		int widthToCapture = (int) (width*Sx);
		int heightToCapture = (int) (height*Sy);
		int startXToCapture = (int) (startX*Sx);
		int startYToCapture = (int) (startY*Sy);
		
		float scale = 0.8f;
		int downScaledWidth = (int) (width*scale);
		int downScaledHeight = (int) (height*scale);
		
		Pixmap p = ScreenUtils.getFrameBufferPixmap(startXToCapture, startYToCapture, widthToCapture, heightToCapture);
		Pixmap downScaled = new Pixmap(downScaledWidth, downScaledHeight, Format.RGB565);
		downScaled.drawPixmap(p, 0, 0, widthToCapture, heightToCapture, 0, 0, downScaledWidth, downScaledHeight);
		PNG png = new PNG();
		png.setCompression(Deflater.BEST_COMPRESSION);
		png.setFlipY(true);
		try{
			png.write(Gdx.files.external(imagePath), downScaled);
			return true;
		}
		catch(IOException e){
			return false;
		}
		catch(GdxRuntimeException e){
			return false;
		}
	}
	
	/**
	 * 
	 * @param array
	 * @param value
	 * @return
	 */
	public static boolean arrayContainsValue(int[] array, int value){
		for(int i = 0; i < array.length; i++)
			if(array[i] == value)
				return true;
		return false;
	}
	
	public static final int NOT_FOUND = -1; 
	public static int getPositionInArray(int[] array, int value){
		for(int i = 0; i < array.length; i++)
			if(array[i] == value)
				return i;
		return NOT_FOUND;
	}
	
	public static float getTextureScale(TextureRegion texture, float maxWidth, float maxHeight) {
		float sclW = 1, sclH = 1, w = texture.getRegionWidth(), h = texture.getRegionHeight();
		if(w > maxWidth) sclW = maxWidth/w;
		if(h > maxHeight) sclH = maxHeight/h;
		return Math.min(sclW, sclH);
	}
	
	public static String getFormatedPriceText(Double value) {
    	String text;
    	double twoDecimals = Math.round(value * 100.0) / 100.0;
		if(twoDecimals >= 1000)
			text = Double.toString(Math.round(twoDecimals/1000.0 * 10.0) / 10.0)+"k";
		else if(twoDecimals >= 10)
			text = Integer.toString(MathUtils.round((float) twoDecimals));
		else
			text = Double.toString(twoDecimals);
		return text;
	}
	
	/**
	 * 
	 * @param array
	 * @param value
	 * @return
	 */
	public static boolean arrayContainsValue(float[] array, float value){
		for(int i = 0; i < array.length; i++)
			if(array[i] == value)
				return true;
		return false;
	}
	
	public static boolean arrayContainsValue(String[] array, String value){
		for(int i = 0; i < array.length; i++)
			if(array[i].equals(value))
				return true;
		return false;
	}
	
	public static float Sx, Sy;
	public static void setScaling(boolean portrait) {
		 Sx = Gdx.graphics.getWidth()/(portrait ? 480f : 800f);
		 Sy = Gdx.graphics.getHeight()/(portrait ? 800f : 480f);
	}

	public static void setupLoadingScreen() {
	}
	
	public static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value};
    }
    
    public static int bytesToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    
    public static short bytesToShort(byte[] bytes) {
    	return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    
    public static byte[] shortToBytes(short value) {
    	return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }
}
