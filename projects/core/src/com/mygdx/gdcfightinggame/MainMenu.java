package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class MainMenu implements GameScreen, InputProcessor{

	public static int ID = 1;
	
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	//Used to determine which menu the game is on
	public static boolean characterSelect = false;
	public static boolean stageSelect = false;
	public static boolean startSelect = true;
	public static boolean readyToStart = false;
	public static boolean isPlaying = false;

	public float startTimer = 0;
	public float maxStartTimer = 0.25f;

	public Sound sfxStart; //TODO add later
	public Sound sfxMove;

	public Sprite mainMenu;
	public Sprite stageMenu;
	public Sprite currentMenu;

	public Sprite stageHighlight;
	//Coordinates when selecting a stage
	public int stageHighlightXLeft = 16;
	public int stageHighlightYLeft = 83;
	public int stageHighlightXRight = 315;
	public int stageHighlightYRight = 83;

	public int stageChoice = 1; //default stage is desert
	
	public BitmapFont font;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		mainMenu = new Sprite(new Texture(Gdx.files.internal("main_menu.png"))); //TODO temporary main menu
		mainMenu.setOrigin(0, 0);
		mainMenu.flip(false, true);

		stageMenu = new Sprite(new Texture(Gdx.files.internal("stage_menu.png")));
		stageMenu.setOrigin(0, 0);
		stageMenu.flip(false, true);

		stageHighlight = new Sprite(new Texture(Gdx.files.internal("stage_highlight.png")));
		stageHighlight.setOrigin(0, 0);
		stageHighlight.flip(false, true);

		currentMenu = mainMenu;

		sfxStart = Gdx.audio.newSound(Gdx.files.internal("hard_select.wav"));
		sfxMove = Gdx.audio.newSound(Gdx.files.internal("select03.wav"));
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel-UniCode.ttf"));
		font = generator.generateFont(24, MainMenu.FONT_CHARACTERS, true);
		generator.dispose();

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		isPlaying = true;
	}

	@Override
	public void preTransitionIn(Transition t){
		isPlaying = false;
		characterSelect = false;
		stageSelect = false;
		startSelect = true;
		readyToStart = false;
		currentMenu = mainMenu;
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.setFont(font);
		g.setBackgroundColor(Color.RED);
		g.drawSprite(currentMenu, 0, 0);
		if(stageSelect){
			if(stageChoice == 1){
				g.drawSprite(stageHighlight, stageHighlightXLeft, stageHighlightYLeft);
			}
			else if(stageChoice == 2){
				g.drawSprite(stageHighlight, stageHighlightXRight, stageHighlightYRight);
			}
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		//This input code needs to be in the update function to have access to ScreenManager
		if(readyToStart){
			startTimer += delta;
			if(startTimer >= maxStartTimer){
				startTimer = 0;
				readyToStart = false;
				if(stageChoice == 1){
					Gameplay.bgCurrent = Gameplay.bgDesert;
				}
				else if(stageChoice == 2){
					Gameplay.bgCurrent = Gameplay.bgPeaks;
				}
				else{ //This should never run
					System.out.println("Error: Variable stageChoice's value is: " + stageChoice);
				}
				sm.enterGameScreen(Gameplay.ID, new FadeOutTransition(), new FadeInTransition());
			}
		}
	}

	/*
	 * =================================Input Methods==========================================
	 */

	@Override
	public boolean keyDown(int keycode) {
		if(!isPlaying){ //Hacky fix for stopping input handling when in the Gameplay state
			if(startSelect){
				if(keycode == Keys.ENTER){
					startSelect = false;
					stageSelect = true;
					currentMenu = stageMenu;
					sfxStart.play();
				}
				if(keycode == Keys.ESCAPE){
					Gdx.app.exit();
				}
			}
			else if(stageSelect){
				if(keycode == Keys.LEFT){
					stageChoice = 1;
					sfxMove.play();
				}
				else if(keycode == Keys.RIGHT){
					stageChoice = 2;
					sfxMove.play();
				}
				if(keycode == Keys.ENTER){
					readyToStart = true;
					sfxStart.play();
				}
				if(keycode == Keys.ESCAPE){
					stageSelect = false;
					startSelect = true;
					currentMenu = mainMenu;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
