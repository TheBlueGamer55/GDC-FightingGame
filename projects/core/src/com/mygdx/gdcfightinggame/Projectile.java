package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Projectile extends Block{

	private static final long serialVersionUID = 1L;
	
	public float timer; //used to control how long a move exists
	public float maxTimer = 0.2f; 
	
	public float damageAmount = 2;
	
	public Player parent;
	
	public Sprite left, right, current;

	public Projectile(float x, float y, float velX, float velY, float accelX, float accelY, float width, float height, Gameplay level, Player parent) {
		super(x, y, velX, velY, accelX, accelY, width, height, level);
		this.parent = parent;
		this.type = "Projectile";
		timer = 0.0f;
		left = new Sprite(new Texture(Gdx.files.internal("sword_slash_left.png")));
		right = new Sprite(new Texture(Gdx.files.internal("sword_slash_right.png")));
		parent.adjustSprite(left, right);
		if(velX > 0){
			current = right;
		}
		else{
			current = left;
		}
	}
	
	@Override
	public void render(Graphics g){
		g.drawSprite(current, x, y);
	}
	
	@Override
	public void update(float delta){
		if(isActive){
			limitSpeed(false, true);
			move();
			
			//Attacks last for a limited time
			timer += delta;
			if(timer >= maxTimer){
				timer = 0;
				level.solids.remove(this);
			}
			
			this.setX(x);
			this.setY(y);
		}
	}

}
