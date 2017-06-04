package com.picke.utils;

public interface Communicator {
	public void openURL(String url);
	public void showInterstitial();
	public void confirm(ConfirmInterface confirmInterface, String question);
	public String getLanguage();
	public void setOrientation(boolean portrait);
	public void postConstruct(boolean removedAds);
	public void showBanners(boolean show);
	
	public String getPackage();
	
	public void showToast(String message, boolean longDuration);
	public void setNotification(String title, String text, int minutes, int type);
	public void cancelNotification();
	public void sendAnalyticsData(String category, String info);
	
	public void share(String title, String subject, String message, String imagePath);
}
