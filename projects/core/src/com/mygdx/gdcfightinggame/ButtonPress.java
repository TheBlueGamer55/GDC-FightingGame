package com.mygdx.gdcfightinggame;


//This class allows for the recording of button presses for the purpose of allowing button-combos.
public class ButtonPress {

	char keyValue = '\0';
	public float timeAlive = 0;//The number of milliseconds this ButtonPress has been active.
	
	public void update(float delta){
		
		timeAlive += delta;
		
	}
	
	public ButtonPress(char input){
		
		this.keyValue = input;
		this.timeAlive = 0;
		
	}
	
	
}
