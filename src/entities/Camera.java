package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromPlayer = 0;
	private float angleAroundPlayer = 0;

	private Vector3f position = new Vector3f(100, 35, 50);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;

	private Player player;

	public Camera(Player player) {
		this.player = player;
		
		calculatePitch();
		calculateYaw();
	}

	public void move() {
		if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
		}
		
		if (Mouse.isGrabbed()) {
			calculatePitch();
			calculateYaw();
	
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
	
			this.yaw = 180 - angleAroundPlayer;
	
			player.setRotY(angleAroundPlayer);
			
			if (player.getIsCrouching()) {
				position.setY(position.getY() - 0.5f);
			} else if (!player.getIsCrouching()) {
				position.setY(position.getY() + 1.0f);
			}
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + (verticDistance + 5);
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * 0.3f;
		pitch -= pitchChange;
	}

	private void calculateYaw() {
		float angleChange = Mouse.getDX() * 0.3f;
		angleAroundPlayer -= angleChange;
	}
}