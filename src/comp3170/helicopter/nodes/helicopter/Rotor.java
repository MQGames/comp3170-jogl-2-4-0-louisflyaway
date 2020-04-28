package comp3170.helicopter.nodes.helicopter;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.helicopter.nodes.Node;
import comp3170.utils.Shader;

public class Rotor extends Node {
	
	private int vertexBuffer;
	// 1 顺时针 -1 逆时针
	private int clockwise;
	private final float maxRotationSpeed = 7f;
	private float rotationSpeed = 0;
	private static final float rotationAcceleration = 0.0012f;
	private boolean rotating = false;

	private float[] bladeVertices = new float[] {
			 0f,    0f, 
			-0.05f,  1f,
			 0.05f,  1f,
			 
			 0f,    0f,
			-1f,    0.05f,  
			-1f,   -0.05f,
			
			 0f,    0f,
			-0.05f, -1f,
			 0.05f, -1f,
			 
			 0f,    0f,
			 1f,    0.05f,
			 1f,   -0.05f
	};
	
	private float[] colour = {0.35f, 0.35f, 0.35f};

	public Rotor(Shader shader, boolean clockwise) {	
		this.clockwise = clockwise ? -1 : 1;;
	    this.vertexBuffer = shader.createBuffer(this.bladeVertices);
		this.localMatrix.scale(1.3f, 1.3f, 0);
	}

	public Rotor(Shader shader, boolean clockwise, float position) {
		this(shader, clockwise);
		this.localMatrix.translate(position, 0, 0);
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.bladeVertices.length / 2);
	}

	@Override
	protected void onUpdate(float deltaTime) {
	    // 带加速度的旋转
		if (rotating && rotationSpeed < maxRotationSpeed) {
			rotationSpeed += rotationAcceleration;
			if (rotationSpeed > maxRotationSpeed) {
				rotationSpeed = maxRotationSpeed;
			}
		}
		else if (!rotating && rotationSpeed > 0) {
			rotationSpeed -= rotationAcceleration;
			if (rotationSpeed < 0) {
				rotationSpeed = 0;
			}
		}

		this.localMatrix.rotateZ(rotationSpeed * deltaTime * clockwise);
	}

	public void start() {
		rotating = true;
	}

	public void stop() {
		rotating = false;
	}
	
	public boolean isReady() {
		return rotationSpeed >= maxRotationSpeed * 0.75;
	}
}
