package comp3170.helicopter.nodes.helicopter;

import comp3170.helicopter.nodes.Node;
import comp3170.utils.InputStateManager;
import org.joml.Vector3f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.utils.Shader;

public class Helicopter extends Node {

	private final Helipad helipad;
	private int vertexBuffer;
	private int windowVertexBuffer;
	private float moveSpeed = 0;
	// max move speed
	private final static float MAX_MOVE_SPEED = 3f;
	// acceleratate
	private final float acceleration = 0.003f;
	// variaables
	private boolean moving = false;
	private boolean landing = true;
	private boolean landed = false;
	private final float rotationSpeed = 1.5f;
	// target coordinates triggered by mouse events
	private float destinationX = -1;
	private float destinationY = -1;
	// set rotor
	private Rotor rotor1;
	private Rotor rotor2;
	
	private float[] bodyVertices = new float[] {
			-1f,   -0.5f,
			 1f,   -0.5f,
			 1.5f,	0f,

			-1f,   -0.5f,
			 1.5f,  0f,
			 1f,    0.5f,
			 
			-1f,   -0.5f,
			 1f,    0.5f,
			-1.5f,  0f,
			 
			 1f,    0.5f,
			-1.5f,  0f,
			-1f,    0.5f
	};

	private float[] windowVertices = new float[] {
			 1f,      0.375f,
			 1f,      0.25f,
			 1.2f,    0f,

			 1f,      0.375f,
			 1.2f,    0f,
			 1.375f,  0f,

			 1.2f,    0f,
			 1.375f,  0f,
			 1f,     -0.375f,

			 1.2f,    0f,
			 1f,     -0.25f,
			 1f,     -0.375f,
	};

	private float[] colour = {0.65f, 0.75f, 0.95f};
	private float[] windowColour = {0.85f, 0.85f, 0.85f};
	private InputStateManager inputStateManager;

	public Helicopter(Shader shader, Helipad helipad) {
	    this.vertexBuffer = shader.createBuffer(this.bodyVertices);
	    this.windowVertexBuffer = shader.createBuffer(this.windowVertices);
	    // set rotor
	    rotor1 = new Rotor(shader, true,  0.5f);
	    rotor2 = new Rotor(shader, false, -0.6f);
		add(rotor1);
		add(rotor2);
	    // asscociated landing node
		this.helipad = helipad;
		helipad.setHelicopter(this);
		localMatrix.scale(0.08f, 0.08f, 1);
		inputStateManager = InputStateManager.get();
	}
	
	public Helicopter(Shader shader, float startX, float startY, Helipad helipad) {
		this(shader, helipad);
		this.localMatrix.setTranslation(startX, startY, 0);
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.bodyVertices.length / 2);

        shader.setAttribute("a_position", this.windowVertexBuffer);
        shader.setUniform("u_colour", this.windowColour);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.windowVertices.length / 2);
	}

	@Override
	protected void onUpdate(float deltaTime) {
        // set targets based on mouse events
		if (inputStateManager.isMouseSet()) {
			setDestination(inputStateManager.getMouseX(), inputStateManager.getMouseY());
			inputStateManager.resetMouse();
		}
		// forward
		setMove(inputStateManager.isUpKeyDown());
		// calculate direction change anagle
		if (inputStateManager.isLeftKeyDown()) {
			rotate(true, deltaTime);
		}
		if (inputStateManager.isRightKeyDown()) {
			rotate(false, deltaTime);
		}
		if (helipad.isCanLand()) {
			if (!isLanding() && inputStateManager.isLKeyDown()) {
				land();
			} else if (isLanding() && inputStateManager.isTKeyDown()) {
				takeOff();
			}
		}
		// cauculate distance
		move(deltaTime);
	}

	// cauculate distance
	private void move(float deltaTime) {
		if (this.landing) {
			onLanding();
		}
		else {
			onTakingOff();
			//The propeller speed needs to reach the threshold
			if (rotor1.isReady() && rotor2.isReady()) {
				if (this.destinationX != -1 && this.destinationY != -1) {
				    // mouse move
					moveWithTarget(deltaTime);
				}
				else {
					// keyboard move
					moveWithDirection();
				}
			}
		}
        //
		this.localMatrix.translate(this.moveSpeed * deltaTime, 0, 0);
	}

	// zoom in 
	private void onTakingOff() {
		if (rotor1.isReady() && this.localMatrix.getScale(new Vector3f()).x < 0.15) {
			float factor = 1.0001f;
			this.localMatrix.scale(factor, factor, 1f);
		}
	}

	// zoom out
	private void onLanding() {
		if (this.localMatrix.getScale(new Vector3f()).x > 0.08) {
			float factor = 0.9999f;
			this.localMatrix.scale(factor, factor, 1f);
		}
		else {
			landed = true;
		}
	}

	// Mouse-triggered movement (target-driven movement)
	private void moveWithTarget(float deltaTime) {
		rotateToMouseCoordinates(deltaTime);
		moveTowardsMouseCoordinates();
	}

	// Move towards the target
	// Slow down when the distance is about
	private void moveTowardsMouseCoordinates() {
		if (Math.abs(angleCalculate()) > Math.PI/ (10 * distance())) {
			if (distance() > 0.18) {
				accelerate();
			} else {
				decelerate();
			}
		}
	}

	//control the rotor speed
	private void rotateToMouseCoordinates(float deltaTime) {
		if (angleCalculate() < Math.PI && angleCalculate() > -Math.PI) {
			if (angleCalculate() > 0) {
				this.localMatrix.rotateZ(-this.rotationSpeed * deltaTime);
				// 
				if (angleCalculate() <= 0 ) {
					this.localMatrix.rotateZ(this.rotationSpeed * deltaTime);
				}
			}
			else {
				this.localMatrix.rotateZ(this.rotationSpeed * deltaTime);
				// 
				if (angleCalculate() > 0) {
					this.localMatrix.rotateZ(-this.rotationSpeed * deltaTime);
				}
			}
		}
	}

	//angle
	private double angleCalculate() {
		double angleToDestination = Math.atan2(this.getYPosition() - destinationY, this.getXPosition() - destinationX);
		return Math.atan2(Math.sin(angleToDestination - facingAngle()), Math.cos(angleToDestination - facingAngle()));
	}

	// discount
	public double distance() {
		return Math.sqrt(Math.pow((this.getXPosition() - this.destinationX), 2) + Math.pow(this.getYPosition() - this.destinationY, 2));
	}

	//keyboard trigged move
	private void moveWithDirection() {
		if (this.moving) {
			accelerate();
		}
		else if (this.moveSpeed > 0) {
			decelerate();
		}
	}

	// acclerate
	private void accelerate() {
		this.moveSpeed += this.acceleration;
		if (this.moveSpeed > Helicopter.MAX_MOVE_SPEED) {
			this.moveSpeed = Helicopter.MAX_MOVE_SPEED;
		}
	}

	// decelerate
	private void decelerate() {
		this.moveSpeed -= this.acceleration;
		if (this.moveSpeed < 0) {
			this.moveSpeed = 0;
		}
	}

	// keyboard triggered move
	public void setMove(boolean moving) {
		if (!this.moving && moving) {
		    // retset mouse
			resetDestination();
		}
		this.moving = moving;
	}

	// control rotation
	public void rotate(boolean left, float deltaTime) {
		if (!landed && rotor1.isReady()) {
			int negation = left ? 1 : -1;
			resetDestination();
			this.localMatrix.rotateZ(rotationSpeed * negation * deltaTime);
		}
	}

	// set codi
	public void setDestination(float x, float y) {
	    // change codi
		this.destinationX = (x / 1000) * 2 - 1;
		this.destinationY = (1 - (y / 1000)) * 2 - 1;
	}

	// reset 
	public void resetDestination() {
		this.destinationX = -1;
		this.destinationY = -1;
	}

	// checking landing
	public boolean isLanding() {
		return landing;
	}

	// landing
	public void land() {
		this.landing = true;
		rotor1.stop();
		rotor2.stop();
	}

	// takeoff
	public void takeOff() {
		this.landing = false;
		this.landed = false;
		rotor1.start();
		rotor2.start();
	}
}