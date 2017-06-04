package com.picke.utils;

public class Language {

	public static String NAMING, EXIT, YES, NO;
	public static String[] NOTIFICATION = new String[3];
	
	public static void setLanguage(String language){
		if(language.contains("pt")){
			NAMING = "O que você gostaria de nomear seu animal de estimação?";
			EXIT = "Gostaria de sair do jogo?";
			YES = "sim";
			NO = "nenhuma";
		}
		else if(language.contains("es")){
			NAMING = "¿Qué te gustaría que el nombre de su mascota?";
			EXIT = "¿Quieres salir del juego?";
			YES = "sí";
			NO = "no";
		}
		else if(language.contains("ru")){
			NAMING = "Что бы вы хотели назвать своего питомца?";
			EXIT = "Хотите, чтобы выйти из игры?";
			YES = "Да";
			NO = "Нет";
			
		}
		else if(language.contains("pl")){
			NAMING = "Co chcesz nazwać zwierzaka?";
			EXIT = "Jeżeli chcesz wyjść z gry?";
			YES = "Tak";
			NO = "Nie";
		}
		else{
			NAMING = "What would you like to name your pet?";
			EXIT = "Would you like to exit the game?";
			YES = "Yes";
			NO = "No";
		}
	}
}
