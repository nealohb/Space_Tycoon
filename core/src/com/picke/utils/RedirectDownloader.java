package com.picke.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.picke.space.BaseClass;
import com.picke.space.Main;

public class RedirectDownloader extends BaseClass {

	Texture customAdT, blackT;
	TextureRegion customAdR, blackR;
	String packageName, promoText;
	HttpRequest httpImageRequest, httpPackageRequest;
	RedirectManager manager;
	
	static final String URL_PACKAGE = "https://s3-sa-east-1.amazonaws.com/frojopromo/promo.txt";
	static final String URL_IMAGE_PORTRAIT = "https://s3-sa-east-1.amazonaws.com/frojopromo/customAd.png";
	static final String URL_IMAGE_LANDSCAPE = "https://s3-sa-east-1.amazonaws.com/frojopromo/customAdLand.png";
	
	static final String imagePathPrep = "bin/redirect/";
	static final String imagePathAppend = "_image.png";
	
	static final String imagePrefsAppend = "_image_downloaded";
	
	public RedirectDownloader(Main m, RedirectManager manager){
		super(m);
		this.manager = manager;
		
		downloadPackageName(); // First download the current package name, ie current app that is promoted. 
	}
	
	public void downloadPackageName(){
		httpPackageRequest = new HttpRequest(HttpMethods.GET);
		httpPackageRequest.setUrl(URL_PACKAGE);
		httpPackageRequest.setTimeOut(10000);
		Gdx.net.sendHttpRequest(httpPackageRequest, new TextListener());
	}
	
	public void downloadImage(){
		httpImageRequest = new HttpRequest(HttpMethods.GET);
		httpImageRequest.setUrl(RedirectManager.PORTRAIT ? URL_IMAGE_PORTRAIT : URL_IMAGE_LANDSCAPE);
		httpImageRequest.setTimeOut(10000);
		Gdx.net.sendHttpRequest(httpImageRequest, new ImageListener());
	}
	
	private class ImageListener implements HttpResponseListener{
		@Override
		public void handleHttpResponse(HttpResponse httpResponse) {
			final int statusCode = httpResponse.getStatus().getStatusCode();
			
			if (statusCode != 200) {
				Gdx.app.log("Downloader", "An error ocurred since statusCode is not OK: "+statusCode);
				return;
			}
			
			// We are not in main thread right now so we need to post to main thread for ui updates
			final byte[] rawImageBytes = httpResponse.getResult();
			Gdx.app.postRunnable(new Runnable() {
				public void run () {
					/* Create custom image */
					Pixmap pixmap = new Pixmap(rawImageBytes, 0, rawImageBytes.length);
					FileHandle file = getFileHandle(imagePathPrep+packageName+imagePathAppend);
					PixmapIO.writeCIM(file, pixmap);
					prefs.putString("redirectImageSaved", packageName+imagePrefsAppend);
					createRedirectImage(pixmap);
					pixmap.dispose();
				}
			});
		}

		@Override
		public void failed(Throwable t) {
			Gdx.app.log("Downloader", "This failed: "+t.getMessage());
		}

		@Override
		public void cancelled() {
		}
	}
	
	public void onPause(){
		if(customAdT == null) return; // if redirects aren't active or we haven't got an image, don't do anything
		
		manager.setRedirectReady(false);
		customAdT.dispose();
	}
	
	public void onResume(){
		if(prefs.contains(packageName) || m.com.getPackage().equals(packageName))
			return;
		
		FileHandle imageFile = getFileHandle(imagePathPrep+packageName+imagePathAppend);
		if(imageFile.exists() && packageName != null) 
			createRedirectImage(PixmapIO.readCIM(imageFile));
	}
	
	private void createRedirectImage(Pixmap pixmap) {
		customAdT = new Texture(pixmap);
		customAdT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		customAdR = new TextureRegion(customAdT, 0, 0, RedirectManager.PORTRAIT ? 256 : 400, RedirectManager.PORTRAIT ? 400 : 256);
		manager.setRedirectReady(true);
	}
	
	private FileHandle getFileHandle(String string) {
		return Gdx.files.local(string);
	}
	
	private void receivedPackageName() {
		if(prefs.contains(packageName) || m.com.getPackage().equals(packageName)) return; // if we have already clicked the ad, don't do anything. 
		
		FileHandle imageFile = getFileHandle(imagePathPrep+packageName+imagePathAppend);
		if(!prefs.getString("redirectImageSaved").equals(packageName+imagePrefsAppend) || !imageFile.exists()){
			downloadImage();
//			System.out.println("downloading image");
		}
		else if(imageFile.exists()){
			createRedirectImage(PixmapIO.readCIM(imageFile));
//			System.out.println("recreating image from memory");
		}
	}
	
	private class TextListener implements HttpResponseListener{
		@Override
		public void handleHttpResponse(HttpResponse httpResponse) {
			final int statusCode = httpResponse.getStatus().getStatusCode();

			if (statusCode != 200) {
				Gdx.app.log("Downloader", "An error ocurred since statusCode is not OK");
				return;
			}
			
			final String response = httpResponse.getResultAsString();
			Gdx.app.postRunnable(new Runnable() {
				public void run () {
					packageName = response;
					receivedPackageName();
				}

				
			});
		}

		@Override
		public void failed(Throwable t) {
			Gdx.app.log("Downloader", "This failed: "+t.getMessage());
		}

		@Override
		public void cancelled() {
		}
	}
}
