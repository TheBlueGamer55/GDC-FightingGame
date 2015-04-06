package com.mygdx.gdcfightinggame;

import java.util.*;

public class ButtonChain {//This contains ALL OF the active button presses for the player
	
	public ArrayList<ButtonPress> chain;//This contains all of the ButtonPresses
	
	public float lifespan;//How long a button press lasts before being deleted.
	
	
	public void addPress(char input){
		
		chain.add(new ButtonPress(input));
	}
	
	public boolean detectCombo(char[] input){
		for(int i = 0; i < chain.size(); i++){
			
			if(chain.get(i).keyValue == input[0]){
				
				
				for(int j = 1; j<input.length; j++){
					if(chain.get(i+j).keyValue != input[j]){//If the wrong key was pressed
						break;//This means that the combo doesn't exist at i
					}
					
					if( (j==input.length-1) && chain.get(i+j).keyValue == input[j] ){
						chain.clear();//Can't have multiple simultaneous combos, so clear out the rest of the array.
						return true;
						
						
					}
					
				}
			}
		}
		
		return false;
	}
	
	public void update(float delta){
		for(int i = 0; i < chain.size(); i++){//Update all ButtonPress instances in the Vector
			
			chain.get(i).update(delta);
			
			
			if(chain.get(i).timeAlive > lifespan){//If it is dead, kill it.
				chain.remove(i);
			}	
		}
	}
	
	
	public ButtonChain(float lifespan){
		chain = new ArrayList<ButtonPress>();
		this.lifespan = lifespan;
		
	}
	

}
