package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Player{ //TODO refactor/organize code so that each character should extend this class

	public float x, y;
	public float velX, velY;
	public float accelX, accelY;
	//Position/vel/accel
	
	
	//***********************************************************************
	//HEALTHBAR STUFF

	public float maxHealth = 100.0f;
	public float health = maxHealth;
	
	public float healthBarMaxWidth = 200;
	public float healthBarHeight = 20;
	
	public float healthBarX = 50;
	public float healthBarY = 20;
	
	public float healthBarRed = 0;
	public float healthBarGreen = 0;
	public float healthBarBlue = 0;

	
	//***********************************************************************

	//Constants which may be adjusted later
	public final float gravity = 0.2f;

	public final float frictionX = 0.4f;
	public final float frictionY = 0.4f;

	public final float moveSpeed = 1.0f;
	public final float jumpSpeed = 2.0f;

	public final float maxSpeedX = 2.0f;
	public final float maxSpeedY = 4.0f;

	protected float knockbackVectorX;
	protected float knockbackVectorY;
	public float knockbackVelX = 1.0f;
	public float knockbackVelY = 1.0f;
	public boolean isKnockedBack;
	public float knockbackTimer;
	public float maxKnockbackTime = 0.25f; //Should be half a second
	public final float KNOCKBACK_FORCE = 0.75f; //affects how hard the player is knocked back based on the attack's force

	public final float maxAttackCooldown = 0.05f;
	public float attackCooldownTimer;
	public boolean canAttack = true;

	public boolean onGround;
	public boolean isActive;

	public boolean facingRight, facingLeft;

	public Block hitbox;
	public Gameplay level;
	public String type;
	
	public ButtonChain myChain;
	
	//Dimensions are 33x27
	public Sprite s, s2, current;

	//Variables that represent the controls in order to make key bindings easier - TODO: Controls different for each character
	protected int LEFT = Keys.A;
	protected int RIGHT = Keys.D;
	protected int JUMP = Keys.W;
	protected int ATTACK1 = Keys.Q;
	protected int ATTACK2 = Keys.E;

	public Player(float x, float y, int playerID, Gameplay level, int left, int right, int jump, int attack1, int attack2){
		this.x = x;
		this.y = y;
		hitbox = new Block(x, y, 0, 0, 0, 0, 32, 32, level); //the hitbox's dimensions change to fit the sprite
		hitbox.type = "Hitbox";
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		onGround = false;
		isActive = false;
		this.level = level;
		type = "Player1";
		this.LEFT = left;
		this.RIGHT = right;
		this.JUMP = jump;
		this.ATTACK1 = attack1;
		this.ATTACK2 = attack2; 
		
		s = new Sprite(new Texture(Gdx.files.internal("don_right_high.png"))); //TODO temporary sprite
		s.setOrigin(0, 0);
		s.flip(false, true);
		s.setScale(2);
		s2 = new Sprite(new Texture(Gdx.files.internal("don_right_low.png")));
		s2.setOrigin(0, 0);
		s2.flip(false, true);
		s2.setScale(2);
		current = s;
		hitbox.setSize(16 * s.getScaleX(), s.getHeight() * s.getScaleY());
		
		this.myChain = new ButtonChain(200.0f);
		
		if( playerID == 1 ){
			healthBarX = 50;
			
			facingRight = true;
			facingLeft = false;
		}
		else if( playerID == 2){
			healthBarX = Gdx.graphics.getWidth() - (healthBarMaxWidth + 50);
			
			s.flip(true, false);
			s2.flip(true, false);
			s.setOrigin(s.getWidth(), 0);
			s2.setOrigin(s2.getWidth(), 0);
			facingRight = false;
			facingLeft = true;
		}
	}

	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.drawRect(healthBarX, healthBarY, healthBarMaxWidth, healthBarHeight);
		
		g.setColor(new Color(healthBarRed, healthBarGreen, healthBarBlue, 1.0f));
		g.fillRect(healthBarX, healthBarY, minZero(healthBarMaxWidth * getHealthPercentage()), healthBarHeight);
		
		g.setColor(Color.WHITE);
		g.drawSprite(current, x, y);
	}

	public void update(float delta){
		if(collisionExistsAt(x, y + 1)){
			onGround = true;
		}
		else{
			onGround = false;
		}

		accelX = 0; //keep resetting the x acceleration

		//Move Left
		if(Gdx.input.isKeyPressed(this.LEFT) && velX > -maxSpeedX){
			myChain.addPress('L');
			accelX = -moveSpeed;
		}
		//Move Right
		if(Gdx.input.isKeyPressed(this.RIGHT) && velX < maxSpeedX){
			myChain.addPress('R');
			accelX = moveSpeed;
		}
		//Jump
		if(Gdx.input.isKeyJustPressed(this.JUMP) ){
			
			myChain.addPress('J');
			
			if(onGround){
				jump();
			}
			
		}
		//Attack 1
		if(Gdx.input.isKeyJustPressed(this.ATTACK1) ){ //TODO: Can player attack in air? Crouching? While moving?
			
			myChain.addPress('1');
			
			if(canAttack){
				attack1();
				canAttack = false; 
				current = s;
			}
		}
		if(Gdx.input.isKeyJustPressed(this.ATTACK2)){
			
			myChain.addPress('2');
			
			if(canAttack){
				attack2();
				canAttack = false;
				current = s2;
			}
		}
		//Apply friction when not moving or when exceeding the max horizontal speed
		if(Math.abs(velX) > maxSpeedX || !Gdx.input.isKeyPressed(this.LEFT) && !Gdx.input.isKeyPressed(this.RIGHT)){
			friction(true, false);
		}

		//Attack cooldown
		if(!canAttack){
			attackCooldownTimer += delta;
			if(attackCooldownTimer >= maxAttackCooldown){
				attackCooldownTimer = 0;
				canAttack = true;
			}
		}

		limitSpeed(false, true);
		checkKnockback(delta);
		move();
		gravity();
		hitbox.setX(this.x);
		hitbox.setY(this.y);		
		
		//healthBarRed = minZero( (float) ( 2 * (0.5 - getHealthPercentage() ) ));
		//healthBarGreen = minZero( (float) (1.0 - 2 * Math.abs(0.5 - getHealthPercentage())));
		//healthBarBlue = minZero(  (float) (2 * (getHealthPercentage() - 0.5)) );
		
		if( getHealthPercentage() == 1.0f ){
			healthBarRed = 1.0f;
			healthBarGreen = 1.0f;
			healthBarBlue = 1.0f;
		}
		else{
			healthBarRed = minZero( (float) (1.0 - getHealthPercentage()) );
			healthBarGreen = minZero( (float) (getHealthPercentage()) );
			healthBarBlue = 0;
		}
	}
	
	public void damage( float amount ){//Damages this unit
		this.health -= amount;
	}

	/*
	 * High attack - should be overridden for each character
	 * TODO: finish code for one attack type and translate it onto the second attack type
	 * should specific movement be handled by the Player or Projectile class
	 */
	public void attack1(){
		if(facingRight){
			RelativeProjectile projectile = new RelativeProjectile(0, -5, 2, 0, 0, 0, 12, 4, this.level, this);
			level.solids.add(projectile);
		}
		else if(facingLeft){
			RelativeProjectile projectile = new RelativeProjectile(0, -5, -2, 0, 0, 0, 12, 4, this.level, this);
			level.solids.add(projectile);
		}
	}

	/*
	 * Low attack - should be overridden for each character
	 */
	public void attack2(){
		if(facingRight){
			RelativeProjectile projectile = new RelativeProjectile(0, 5, 2, 0, 0, 0, 12, 4, this.level, this);
			level.solids.add(projectile);
		}
		else if(facingLeft){
			RelativeProjectile projectile = new RelativeProjectile(0, 5, -2, 0, 0, 0, 12, 4, this.level, this);
			level.solids.add(projectile);
		}
	}

	/*
	 * Checks if there is a collision if the player was at the given position.
	 */
	public boolean isColliding(Block other, float x, float y){
		if(other == this.hitbox){ //Make sure solid isn't stuck on itself
			return false;
		}
		if(other.type.equals("Projectile")){ //Make sure a character isn't hit by its own attacks
			if(((Projectile) other).parent == this){
				return false;
			}
			else{ //Hit by opponent's attack
				if(x <= other.x + other.width && x + hitbox.width >= other.x && y <= other.y + other.height && y + hitbox.height >= other.y){
					
					this.damage( ((Projectile)other).damageAmount);
					
					isKnockedBack = true;
					knockbackTimer = maxKnockbackTime;
					knockbackVectorX = facingLeft ? 2 : -2; //TODO knockback force may depend on the attack type
					knockbackVectorY = 0;
					level.solids.remove(other);
					return true;
				}
			}
		}
		if(x < other.x + other.width && x + hitbox.width > other.x && y < other.y + other.height && y + hitbox.height > other.y){
			return true;
		}
		return false;
	}

	/*
	 * Helper method for checking whether there is a collision if the player moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}

	public void move(){
		moveX();
		moveY();
	}

	/*
	 * Applies a friction force in the given axes by subtracting the respective velocity components
	 * with the given friction components.
	 */
	public void friction(boolean horizontal, boolean vertical){
		//if there is horizontal friction
		if(horizontal){
			if(velX > 0){
				velX -= frictionX; //slow down
				if(velX < 0){
					velX = 0;
				}
			}
			if(velX < 0){
				velX += frictionX; //slow down
				if(velX > 0){
					velX = 0;
				}
			}
		}
		//if there is vertical friction
		if(vertical){
			if(velY > 0){
				velY -= frictionY; //slow down
				if(velY < 0){
					velY = 0;
				}
			}
			if(velY < 0){
				velY += frictionY; //slow down
				if(velY > 0){
					velY = 0;
				}
			}
		}
	}

	/*
	 * Limits the speed of the player to a set maximum
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
	 * Checks if player is being knocked back and updates movement accordingly.
	 */
	public void checkKnockback(float delta){
		if(isKnockedBack){
			if(knockbackTimer == maxKnockbackTime){ //so that the enemy doesn't mimic player movement, we save the initial knockback speed
				knockbackVelX = knockbackVectorX * KNOCKBACK_FORCE;
				knockbackVelY = knockbackVectorY * KNOCKBACK_FORCE;
			}
			velX = knockbackVelX; //set the velocity equal to the initial knockback speed
			velY = knockbackVelY;
			knockbackTimer -= delta;
			if(knockbackTimer <= 0){
				knockbackTimer = 0;
				knockbackVelX = 0;
				knockbackVelY = 0;
				isKnockedBack = false;
			}
		}
	}

	public void jump(){
		velY = -jumpSpeed;
	}
	
	public float minZero( float input ){
		return( input > 0 ? input : 0 );
	}

	public float getHealthPercentage(){
		return(health/maxHealth);
	}
	
	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileX(int tileSize){
		return (int)(x / tileSize) * tileSize;
	}

	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileY(int tileSize){
		return (int)(y / tileSize) * tileSize;
	}

	/*
	 * Returns the distance between the player and the given target
	 */
	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	public void gravity(){
		velY += gravity;
	}

	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(isColliding(solid, x + velX, y)){
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
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(isColliding(solid, x, y + velY)){
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
	 * Sets up any images that the player may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			s[i].setOrigin(0, 0);
			s[i].flip(false, true);
		}
	}

}

