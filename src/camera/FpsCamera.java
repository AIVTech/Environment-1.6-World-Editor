package camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import display.DisplayManager;
import terrain.Terrain;

public class FpsCamera {

	private Vector3f position = new Vector3f(550, 6.2f, -490);
	private float pitch, yaw, roll;

	// movement variables
	private float walkingSpeed = 40;
	private float runningSpeed = 70;
	private float crouchDistance = 6.2f;
	private float movementSpeed = walkingSpeed;

	// mouse movement variables
	float mouseSensitivity = 0.02f;
	float xoffset = 0, yoffset = 0;

	// movement physics
	private float GRAVITY = -60;
	private float JUMP_POWER = 20;
	private float upwardSpeed = 0;
	private boolean inAir = false;

	public FpsCamera() { }

	public void update(Terrain terrain) {
		processKeyboardInput();
		processMouseInput();

		// Jumping
		float terrainHeight = terrain.getHeightOfTerrain(position.x, position.z) + crouchDistance;
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		position.y += upwardSpeed * DisplayManager.getFrameTimeSeconds();
		if (position.y < terrainHeight) {
			position.y = terrainHeight;
			upwardSpeed = 0;
			inAir = false;
		}
	}

	private void calculateMouseOffsets() {
		xoffset = Mouse.getDX();
		yoffset = -Mouse.getDY();
	}

	private void jump() {
		if (!inAir) {
			upwardSpeed = JUMP_POWER;
			inAir = true;
		}
	}

	private void processMouseInput() {
		calculateMouseOffsets();

		xoffset *= mouseSensitivity;
		yoffset *= mouseSensitivity;

		yaw += xoffset;
		pitch += yoffset;

		// Limit checking
		if (pitch > 89.0f)
			pitch = 89.0f;
		if (pitch < -89.0f)
			pitch = -89.0f;
	}

	private void processKeyboardInput() {
		float velocity = movementSpeed * DisplayManager.getFrameTimeSeconds();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			movementSpeed = runningSpeed;
		}
		else {
			movementSpeed = walkingSpeed;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.x += Math.sin(Math.toRadians(yaw)) * velocity;
			position.z -= Math.cos(Math.toRadians(yaw)) * velocity;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.x -= Math.sin(Math.toRadians(yaw)) * velocity;
			position.z += Math.cos(Math.toRadians(yaw)) * velocity;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x += Math.sin(Math.toRadians(yaw - 90)) * velocity;
			position.z -= Math.cos(Math.toRadians(yaw - 90)) * velocity;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += Math.sin(Math.toRadians(yaw + 90)) * velocity;
			position.z -= Math.cos(Math.toRadians(yaw + 90)) * velocity;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}

	/******** GETTERS AND SETTERS *********/

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

}
