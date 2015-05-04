package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.graphics.Graphics;

public class RelativeProjectile extends Projectile{ //Projectiles whose positions are calculated relative to their parents' centers

	float relativeX = 0;
	float relativeY = 0;

	private static final long serialVersionUID = 1L;

	public RelativeProjectile(float x, float y, float velX, float velY, float accelX, float accelY, float width, float height, Gameplay level, Player parent) {
		super(0, 0, velX, velY, accelX, accelY, width, height, level, parent);
		relativeX = x;
		relativeY = y;
		this.type = "Projectile";
		timer = 0.0f;
		this.x = (float) (parent.x + parent.hitbox.width / 2.0 + relativeX);
		this.y = (float) (parent.y + parent.hitbox.height / 2.0 + relativeY);
	}

	public void render(Graphics g){
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
					Player parent = ((Projectile)this).parent;
					Player other = ((Projectile)solid).parent;

					if(parent != other){ 
						stun(parent);
						stun(other);
					}

					level.solids.remove(this);
					level.solids.remove(solid);
					break;
				}
				else if(solid.type.equals("Hitbox")){
					if(solid != parent.hitbox){
						while(!isColliding(solid, x + Math.signum(velX), y)){
							x += Math.signum(velX);
						}
					}
				}
			}
		}
		relativeX += velX;
		velX += accelX;
		x = (float) (parent.x + parent.hitbox.width / 2.0 + relativeX); //start it at the center of the player's hitbox, then move to its relative x
	}

	/*
	 * Move vertically in the direction of the y-velocity vector.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				if(solid.type.equals("Projectile") && this.type.equals("Projectile")){
					Player parent = ((Projectile)this).parent;
					Player other = ((Projectile)solid).parent;

					if(parent != other){
						stun(parent);
						stun(other);
					}

					level.solids.remove(this);
					level.solids.remove(solid);
					break;
				}
				else if(solid.type.equals("Hitbox")){
					if(solid != parent.hitbox){
						while(!isColliding(solid, x, y + Math.signum(velY))){
							y += Math.signum(velY);
						}
					}
				}
			}
		}
		relativeY += velY;
		velY += accelY;
		y = (float) (parent.y + parent.hitbox.height / 2.0 + relativeY); //start it at the center of the player's hitbox, then move to its relative x
	}

	/*
	 * Applies a stun and knockback to a player
	 * TODO adjust stun until it works properly
	 */
	public void stun(Player p){
		if(!p.isStunned && !p.isKnockedBack){
			p.isStunned = true;
			p.isKnockedBack = true;
			p.knockbackTimer = p.maxKnockbackTime;
			p.knockbackVectorX = p.facingLeft ? 2 : -2; 
			p.knockbackVectorY = 0;
			p.canMove = true;
		}
	}

}
