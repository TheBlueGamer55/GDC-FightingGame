package com.mygdx.gdcfightinggame;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Player{ 

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

	public final float moveSpeed = 2.0f;
	public final float jumpSpeed = 6.0f;

	public final float maxSpeedX = 2.0f;
	public final float maxSpeedY = 6.0f;

	protected float knockbackVectorX;
	protected float knockbackVectorY;
	public float knockbackVelX = 1.0f;
	public float knockbackVelY = 1.0f;
	public boolean isKnockedBack;
	public float knockbackTimer;
	public float maxKnockbackTime = 0.25f; //Should be half a second
	public final float KNOCKBACK_FORCE = 0.75f; //affects how hard the player is knocked back based on the attack's force

	public boolean isStunned = false; 
	public float stunTimer;
	public float maxStunTimer = 0.25f; //How long a player is stunned for

	public final float maxAttackCooldown = 0.1f;
	public float attackCooldownTimer;
	public boolean canAttack = true;

	public boolean canMove = true;
	public boolean onGround;
	public boolean isActive;

	public boolean facingRight, facingLeft;

	public Block hitbox;
	public Gameplay level;
	public String type;
	public int id;

	public Sound hurt, slash, clang1, clang2;

	public ButtonChain myChain;

	//Dimensions are 33x27
	public Sprite sRightHigh, sRightLow, sLeftHigh, sLeftLow, current;

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
		hitbox.setParent(this);
		this.id = playerID;
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

		sRightHigh = new Sprite(new Texture(Gdx.files.internal("don_right_high.png")));
		sRightHigh.setOrigin(0, 0);
		sRightHigh.flip(false, true);
		sRightHigh.setScale(2);
		sRightLow = new Sprite(new Texture(Gdx.files.internal("don_right_low.png")));
		sRightLow.setOrigin(0, 0);
		sRightLow.flip(false, true);
		sRightLow.setScale(2);
		sLeftHigh = new Sprite(new Texture(Gdx.files.internal("don_left_high.png"))); 
		sLeftHigh.setOrigin(0, 0);
		sLeftHigh.flip(false, true);
		sLeftHigh.setScale(2);
		sLeftHigh.setOrigin(sLeftHigh.getWidth(), 0);
		sLeftLow = new Sprite(new Texture(Gdx.files.internal("don_left_low.png")));
		sLeftLow.setOrigin(0, 0);
		sLeftLow.flip(false, true);
		sLeftLow.setScale(2);
		sLeftLow.setOrigin(sLeftLow.getWidth(), 0);
		current = sRightHigh;
		hitbox.setSize(16 * sRightHigh.getScaleX(), sRightHigh.getHeight() * sRightHigh.getScaleY());

		myChain = new ButtonChain(200.0f);

		if( playerID == 1 ){
			healthBarX = 50;

			facingRight = true;
			facingLeft = false;
		}
		else if( playerID == 2){
			healthBarX = Gdx.graphics.getWidth() - (healthBarMaxWidth + 50);

			current = sLeftHigh;
			facingRight = false;
			facingLeft = true;
		}

		hurt = Gdx.audio.newSound(Gdx.files.internal("hurt.wav"));
		slash = Gdx.audio.newSound(Gdx.files.internal("slash.mp3"));
		clang1 = Gdx.audio.newSound(Gdx.files.internal("clang_strong.mp3"));
		clang2 = Gdx.audio.newSound(Gdx.files.internal("clang_stronger.mp3"));
	}

	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.drawRect(healthBarX, healthBarY, healthBarMaxWidth, healthBarHeight);

		g.setColor(new Color(healthBarRed, healthBarGreen, healthBarBlue, 1.0f));
		g.fillRect(healthBarX, healthBarY, minZero(healthBarMaxWidth * getHealthPercentage()), healthBarHeight);

		g.setColor(Color.WHITE);
		g.drawSprite(current, x, y);
		g.drawString(id + "", x + current.getWidth() / 2, y - 16);
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
		if(Gdx.input.isKeyJustPressed(JUMP)){
			myChain.addPress('J');
			if(onGround){
				jump();
			}
		}
		//Move Left
		if(Gdx.input.isKeyPressed(LEFT) && velX > -maxSpeedX){
			myChain.addPress('L');
			accelX = -moveSpeed;
		}
		//Move Right
		if(Gdx.input.isKeyPressed(RIGHT) && velX < maxSpeedX){
			myChain.addPress('R');
			accelX = moveSpeed;
		}

		//Attack 1
		if(Gdx.input.isKeyJustPressed(ATTACK1) && !isStunned && onGround){ 

			myChain.addPress('1');

			if(canAttack){
				attack1();
				canAttack = false; 
				canMove = false;
				if(facingRight){
					current = sRightHigh;
				}
				else{
					current = sLeftHigh;
				}
			}
		}
		if(Gdx.input.isKeyJustPressed(ATTACK2) && !isStunned && onGround){

			myChain.addPress('2');

			if(canAttack){
				attack2();
				canAttack = false;
				canMove = false;
				if(facingRight){
					current = sRightLow;
				}
				else{
					current = sLeftLow;
				}
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
				canMove = true;
			}
		}

		//Stun cooldown
		if(isStunned){
			stunTimer += delta;
			if(stunTimer >= maxStunTimer){
				stunTimer = 0;
				isStunned = false;
			}
		}

		//Always face the opponent
		if(this == level.don){ //For player one
			if(this.x <= level.knight.x){ //If to the left of the opponent
				facingRight = true;
				facingLeft = false;
				if(!(current == sRightHigh || current == sRightLow)){ //If switching directions
					current = sRightHigh;
				}
			}
			else{
				facingRight = false;
				facingLeft = true;
				if(!(current == sLeftHigh || current == sLeftLow)){ //If switching directions
					current = sLeftHigh;
				}
			}
		}
		else if(this == level.knight){ //For player 2
			if(this.x <= level.don.x){ //If to the left of the opponent
				facingRight = true;
				facingLeft = false;
				if(!(current == sRightHigh || current == sRightLow)){ //If switching directions
					current = sRightHigh;
				}
			}
			else{
				facingRight = false;
				facingLeft = true;
				if(!(current == sLeftHigh || current == sLeftLow)){ //If switching directions
					current = sLeftHigh;
				}
			}
		}

		limitSpeed(false, true);
		checkKnockback(delta);
		if(canMove){ //Player cannot move while attacking
			move();
		}
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
		hurt.play();
	}

	/*
	 * High attack - should be overridden for each character
	 * should specific movement be handled by the Player or Projectile class
	 */
	public void attack1(){
		slash.play();
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
		slash.play();
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
		if(other != this.hitbox){
			if(other.type.equals("Projectile")){ 
				//Hit by opponent's attack 
				if(((Projectile) other).parent != this){ 
					if(x <= other.x + other.width && x + hitbox.width >= other.x && y <= other.y + other.height && y + hitbox.height >= other.y){

						this.damage( ((Projectile)other).damageAmount);

						isKnockedBack = true;
						knockbackTimer = maxKnockbackTime;
						knockbackVectorX = facingLeft ? 2 : -2; 
						knockbackVectorY = 0;
						level.solids.remove(other);
						return true;
					}
				}
			}
			if(x < other.x + other.width && x + hitbox.width > other.x && y < other.y + other.height && y + hitbox.height > other.y){
				return true;
			}
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
			canMove = true;
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
				double dist = Math.abs(solid.x - this.x);
				/*
				 * NOTE: For some reason, when timed correctly, if the player taps twice, the player's own 
				 * x coordinate jumps ahead really far, which causes the isColliding() method to return false,
				 * which in turn causes an infinite loop. Limiting this with a for loop  and dist is only a 
				 * hard-coded solution. The problem is most likely with the knockback and collision code.
				 */
				for(int n = 1; n <= dist && !isColliding(solid, x + Math.signum(velX), y) && Math.signum(velX) != 0; n++){
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
				double dist = Math.abs(solid.y - this.y);
				for(int n = 1; n <= dist && !isColliding(solid, x, y + Math.signum(velY)) && Math.signum(velY) != 0; n++){
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

