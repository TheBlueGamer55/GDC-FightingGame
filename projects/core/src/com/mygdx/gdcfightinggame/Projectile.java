package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.graphics.Graphics;

public class Projectile extends Block{

	private static final long serialVersionUID = 1L;
	
	public float timer; //used to control how long a move exists
	public float maxTimer = 0.2f; //the default value is 0.4 seconds
	/* TODO lasts as long as the animation lasts
	 * Each attack has one hitbox. Since attacks are instant, we only need one static hitbox per attack.
	 */
	
	public float damageAmount = 5;//TODO this should differ based on the attack type.
	
	public Player parent;

	public Projectile(float x, float y, float velX, float velY, float accelX, float accelY, float width, float height, Gameplay level, Player parent) {
		super(x, y, velX, velY, accelX, accelY, width, height, level);
		this.parent = parent;
		//TODO make sure this is everything we need for the constructor
		this.type = "Projectile";
		timer = 0.0f;
	}
	
	@Override
	public void render(Graphics g){
		//TODO Leave this method empty unless we want something specific drawn later on.
		super.render(g);
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
