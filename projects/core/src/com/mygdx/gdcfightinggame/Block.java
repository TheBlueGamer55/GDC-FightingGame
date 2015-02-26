package com.mygdx.gdcfightinggame;

/*
 * This class will represent the attacks of each player. A Block object will act as a "projectile" when
 * a character attacks, but with its own unique properties based on the attack. These properties will already
 * be initialized on the attack methods, so the code in the Block class will not be dependent on the different
 * characters.
 */

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

public class Block extends Rectangle{

	private static final long serialVersionUID = 1L;
	
	public float velX, velY;
	public float accelX, accelY;

	//Constants which may be adjusted later
	public final float frictionX = 0.4f;
	public final float frictionY = 0.4f;

	public final float maxSpeedX = 3.0f;
	public final float maxSpeedY = 6.0f;

	public boolean isActive;
	private Gameplay level;

	public Block(float x, float y, float width, float height, Gameplay level){
		super(x, y, width, height);
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		isActive = true;
		this.level = level;
	}

	public void render(Graphics g){
		g.drawRect(this.x, this.y, this.width, this.height);
	}

	public void update(float delta){
		if(isActive){
			limitSpeed(false, true);
			move();
			this.setX(x);
			this.setY(y);
		}
	}

	public void move(){
		moveX();
		moveY();
	}

	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the block hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(isColliding(solid, x + velX, y) && solid != this){
				while(!isColliding(solid, x + Math.signum(velX), y)){
					x += Math.signum(velX);
				}
				velX = 0;
			}
		}
		x += velX;
		velX += accelX;
	}

	/*
	 * Move vertically in the direction of the y-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the block hits the solid.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(isColliding(solid, x, y + velY) && solid != this){
				while(!isColliding(solid, x, y + Math.signum(velY))){
					y += Math.signum(velY);
				}
				velY = 0;
			}
		}
		y += velY;
		velY += accelY;
	}

	/*
	 * Limits the speed of the block to a set maximum
	 */
	private void limitSpeed(boolean horizontal, boolean vertical){
		//If horizontal speed should be limited
		if(horizontal){
			if(Math.abs(velX) > maxSpeedX){
				velX = maxSpeedX * Math.signum(velX);
			}
		}
		//If vertical speed should be limited
		if(vertical){
			if(Math.abs(velY) > maxSpeedY){
				velY = maxSpeedY * Math.signum(velY);
			}
		}
	}

	/*
	 * Checks if there is a collision if the block was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		if(x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y){
			return true;
		}
		return false;
	}

	/*
	 * Helper method for checking whether there is a Rectangle if the block moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}
	
}
