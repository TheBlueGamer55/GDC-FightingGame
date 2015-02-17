package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.game.ScreenBasedGame;

public class SwordFightingGame extends ScreenBasedGame{
	
	@Override
    public void initialise(){
        this.addScreen(new MainMenu());
        this.addScreen(new Gameplay());
    }

    @Override
    public int getInitialScreenId(){
        return MainMenu.ID;
    }
	
}
