package com.mygdx.gdcfightinggame;

import java.util.ArrayList;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Gameplay implements GameScreen{

	public static int ID = 2;
	public Player don, knight;

	public ArrayList<Block> solids;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){

	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void postTransitionIn(Transition t){
		
	}

	@Override
	public void postTransitionOut(Transition t){

	}

	@Override
	public void preTransitionIn(Transition t){
		solids = new ArrayList<Block>();
		solids.add(new Block(0, 400, 640, 16, this)); //TODO test code to spawn a platform for player testing
		don = new Player(320, 32, this);
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawString("Gameplay menu", 320, 240);
		don.render(g);
		renderSolids(g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		don.update(delta);
		updateSolids(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
		}
	}

	public void renderSolids(Graphics g){
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).render(g);
		}
	}
	
	public void updateSolids(float delta){
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).update(delta);
		}
	}

}
