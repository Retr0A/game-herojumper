package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	private static float RUN_SPEED_X = 20;
	private static float RUN_SPEED_Z = 20;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 20;

	private float currentSpeedX = 0;
	private float currentSpeedZ = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;
	private boolean isSprinting = false;
	private boolean isCrouching = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public boolean getIsCrouching() {
		return isCrouching;
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distanceX = currentSpeedX * DisplayManager.getFrameTimeSeconds();
		float distanceZ = currentSpeedZ * DisplayManager.getFrameTimeSeconds();

		float dxx = (float) (distanceX * Math.cos(Math.toRadians(-super.getRotY())));
		float dxz = (float) (distanceX * Math.sin(Math.toRadians(-super.getRotY())));
		float dzx = (float) (distanceZ * Math.sin(Math.toRadians(super.getRotY())));
		float dzz = (float) (distanceZ * Math.cos(Math.toRadians(super.getRotY())));

		super.increasePosition(dzx, 0, dzz);
		super.increasePosition(dxx, 0, dxz);

		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);

		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);

		if (super.getScale() > 0.5f) {
			isCrouching = false;
		}
		
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void sprint() {
		if (!isSprinting) {
			RUN_SPEED_X = 20;
			RUN_SPEED_Z = 20;
			isSprinting = true;
		} else if (isSprinting) {
			RUN_SPEED_X = 70;
			RUN_SPEED_Z = 70;
			isSprinting = false;
		}
	}

	@SuppressWarnings("unused")
	private void crouch() {
		if (!isCrouching) {
			this.setScale(0.5f);
			isCrouching = true;
		} else if (isCrouching) {
			this.setScale(1.0f);
			isCrouching = false;
		}
	}

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeedZ = RUN_SPEED_Z;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeedZ = -RUN_SPEED_Z;
		} else {
			this.currentSpeedZ = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentSpeedX = -RUN_SPEED_X;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentSpeedX = RUN_SPEED_X;
		} else {
			this.currentSpeedX = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_C)
				|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			//crouch();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			sprint();
		} else if(!isSprinting) {
			sprint();
		}
	}

}