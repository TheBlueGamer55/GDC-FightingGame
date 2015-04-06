package com.mygdx.gdcfightinggame;

import java.util.Vector;

public class ButtonChain {//This contains ALL OF the active button presses for the player
	
	public Vector<ButtonPress> chain;//This contains all of the ButtonPresses
	
	public float lifespan;//How long a button press lasts before being deleted.
	
	
	public void addPress(char input){
		
		chain.add(new ButtonPress(input));
	}
	
	public boolean detectCombo(char[] input){
		for(int i = 0; i < chain.size(); i++){
			
			if(chain.elementAt(i).keyValue == input[0]){
				
				
				for(int j = 1; j<input.length; j++){
					if(chain.elementAt(i+j).keyValue != input[j]){//If the wrong key was pressed
						break;//This means that the combo doesn't exist at i
					}
					
					if( (j==input.length-1) && chain.elementAt(i+j).keyValue == input[j] ){
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
			
			chain.elementAt(i).update(delta);
			
			
			if(chain.elementAt(i).timeAlive > lifespan){//If it is dead, kill it.
				chain.remove(i);
			}	
		}
	}
	
	
	public ButtonChain(float lifespan){
		
		this.lifespan = lifespan;
		
	}
	

}
