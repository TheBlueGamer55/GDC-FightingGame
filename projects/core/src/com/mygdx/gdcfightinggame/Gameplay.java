package com.mygdx.gdcfightinggame;

import java.util.ArrayList;
import java.util.Random;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Gameplay implements GameScreen{

	public static int ID = 2;
	public Player don, knight;
	
	public static Random random = new Random();
	
	public static int donWinCount = 0, knightWinCount = 0;

	public ArrayList<Block> solids;

	public static Sprite bgDesert;
	public static Sprite bgPeaks;
	public static Sprite bgCurrent;

	public Sound desertTheme;
	public Sound roundOverSfx;

	public boolean roundStarting;
	public float roundTimer = 0;
	public float maxRoundTimer = 2f;

	public boolean roundEnding;
	public float roundEndTimer = 0;
	public float maxRoundEndTimer = 3f;
	
	public boolean donWin, knightWin;
	
	public final int donStartX = 240;
	public final int donStartY = 320;
	public final int knightStartX = 320;
	public final int knightStartY = 320;

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
		roundOverSfx = Gdx.audio.newSound(Gdx.files.internal("end_round.wav"));
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void postTransitionIn(Transition t){
		desertTheme.stop(); //make sure we aren't playing the same song multiple times at once
		desertTheme.loop(0.25f);
		roundStarting = true;
	}

	@Override
	public void postTransitionOut(Transition t){
		//Stop all songs from playing after an entire match ends
		desertTheme.stop();
		roundStarting = false;
		roundEnding = false;
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
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(don);
		multiplexer.addProcessor(knight);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawSprite(bgCurrent, 0, 0);
		g.setColor(Color.WHITE);
		g.drawString("P1 Wins: " + donWinCount, 16, gc.getHeight() - 36);
		g.drawString("P2 Wins: " + knightWinCount, gc.getWidth() - 86, gc.getHeight() - 36);
		renderSolids(g); 
		don.render(g);
		knight.render(g);
		if(roundStarting){
			g.drawString("Round starting in " + (int)Math.ceil(maxRoundTimer - roundTimer), 256, 170);
			don.facingRight = true;
			don.facingLeft = false;
			knight.facingLeft = true;
			knight.facingRight = false;
		}
		if(roundEnding){
			if(donWin){
				g.drawString("Player 1 has won the round!", 226, 170); 
			}
			else if(knightWin){
				g.drawString("Player 2 has won the round!", 226, 170); 
			}
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		if(!roundStarting && !roundEnding){ //In the middle of a round
			updateSolids(delta);
			don.update(delta);
			knight.update(delta);
			
			if(don.health <= 0){
				roundEnding = true;
				knightWin = true;
				knightWinCount++;
				roundOverSfx.play();
			}
			else if(knight.health <= 0){
				roundEnding = true;
				donWin = true;
				donWinCount++;
				roundOverSfx.play();
			}

			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
				sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
			}
		}
		else{
			if(roundStarting){
				roundTimer += delta;
				if(roundTimer >= maxRoundTimer){
					roundTimer = 0;
					roundStarting = false;
				}
			}
			else if(roundEnding){
				roundEndTimer += delta;
				if(roundEndTimer >= maxRoundEndTimer){
					restartPlayers();
					roundEndTimer = 0;
					roundEnding = false;
					roundStarting = true;
					donWin = false;
					knightWin = false;
				}
			}
		}
	}

	public void renderSolids(Graphics g){
		for(int i = 0; i < solids.size(); i++){ //TODO only render projectiles
			solids.get(i).render(g);
		}
	}

	public void updateSolids(float delta){
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).update(delta);
		}
	}
	
	public void restartPlayers(){
		don.x = donStartX;
		don.y = donStartY;
		don.health = don.maxHealth;
		don.knockbackTimer = 0;
		don.knockbackVelX = 0;
		don.knockbackVelY = 0;
		don.isKnockedBack = false;
		
		knight.x = knightStartX;
		knight.y = knightStartY;
		knight.health = knight.maxHealth;
		knight.knockbackTimer = 0;
		knight.knockbackVelX = 0;
		knight.knockbackVelY = 0;
		knight.isKnockedBack = false;
	}

}
