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
	protected Gameplay level;
	public String type;
	
	public boolean hasSetPlayer = false;
	public Player player;

	public Block(float x, float y, float velX, float velY, float accelX, float accelY, float width, float height, Gameplay level){
		super(x, y, width, height);
		this.velX = velX;
		this.velY = velY;
		this.accelX = accelX;
		this.accelY = accelY;
		isActive = true;
		this.level = level;
		type = "Block";
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
	 * Move horizontally in the direction of the x-velocity vector.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				if(solid.type.equals("Projectile") && this.type.equals("Projectile")){
					level.solids.remove(this);
					level.solids.remove(solid);
					break;
				}
				else if(solid.type.equals("Hitbox") && !this.type.equals("Projectile")){
					while(!isColliding(solid, x + Math.signum(velX), y)){
						x += Math.signum(velX);
					}
				}
			}
		}
		x += velX;
		velX += accelX;
	}

	/*
	 * Move vertically in the direction of the y-velocity vector.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				if(solid.type.equals("Projectile") && this.type.equals("Projectile")){
					level.solids.remove(this);
					level.solids.remove(solid);
					break;
				}
				else if(solid.type.equals("Hitbox") && !this.type.equals("Projectile")){
					while(!isColliding(solid, x, y + Math.signum(velY))){
						y += Math.signum(velY);
					}
				}
			}
		}
		y += velY;
		velY += accelY;
	}

	/*
	 * Limits the speed of the block to a set maximum
	 */
	protected void limitSpeed(boolean horizontal, boolean vertical){
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
	public boolean isColliding(Block other, float x, float y){
		if(other == this){ //Make sure this block isn't stuck on itself
			return false;
		}
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
			Block solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Helper method that returns a block that is colliding with this block
	 */
	public Block blockExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				return solid;
			}
		}
		return null;
	}
	
	public void setParent(Player p){
		player = p;
	}
	
}
