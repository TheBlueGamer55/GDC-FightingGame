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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Gameplay implements GameScreen{

	public static int ID = 2;
	public Player don, knight;

	public ArrayList<Block> solids;
	
	public static Sprite bgDesert;
	public static Sprite bgPeaks;
	public static Sprite bgCurrent;
	
	public Sound desertTheme;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		bgDesert = new Sprite(new Texture(Gdx.files.internal("bg_desert.png")));
		bgDesert.setOrigin(0, 0);
		bgDesert.flip(false, true);
		bgDesert.setSize(320, 240);
		bgDesert.setScale(2);
		
		bgPeaks = new Sprite(new Texture(Gdx.files.internal("bg_peaks.png")));
		bgPeaks.setOrigin(0, 0);
		bgPeaks.flip(false, true);
		bgPeaks.setSize(320, 240);
		bgPeaks.setScale(2);
		
		bgCurrent = bgDesert;
		
		desertTheme = Gdx.audio.newSound(Gdx.files.internal("Espana_Cani_Chiptune.mp3"));
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void postTransitionIn(Transition t){
		//TODO change later when level selection is added
		desertTheme.stop(); //make sure we aren't playing the same song multiple times at once
		desertTheme.loop();
	}

	@Override
	public void postTransitionOut(Transition t){
		//Stop all songs from playing after an entire match ends
		desertTheme.stop();
	}

	@Override
	public void preTransitionIn(Transition t){
		solids = new ArrayList<Block>();
		solids.add(new Block(0, 412, 0, 0, 0, 0, 640, 16, this)); //Platform for both levels
		solids.add(new Block(0, 0, 0, 0, 0, 0, 16, 480, this));
		solids.add(new Block(640 - 16, 0, 0, 0, 0, 0, 16, 480, this));
		don = new Player(240, 320, 1, this, Keys.A, Keys.D, Keys.W, Keys.Q, Keys.E);
		knight = new Player(320, 320, 2, this, Keys.J, Keys.L, Keys.I, Keys.U, Keys.O);
		knight.facingRight = false;
		knight.facingLeft = true;
		solids.add(don.hitbox);
		solids.add(knight.hitbox);
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawSprite(bgCurrent, 0, 0);
		renderSolids(g); //TODO may not be needed later on
		don.render(g);
		knight.render(g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		updateSolids(delta);
		don.update(delta);
		knight.update(delta);
		
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
