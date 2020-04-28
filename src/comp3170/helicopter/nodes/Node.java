package comp3170.helicopter.nodes;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import comp3170.utils.Shader;

public class Node {

	public Matrix4f localMatrix;
	private Matrix4f worldMatrix;
	private List<Node> children;

	public Node() {
		this.localMatrix = new Matrix4f();
		this.localMatrix.identity();
		
		this.worldMatrix = new Matrix4f();
		this.worldMatrix.identity();
			
		this.children = new ArrayList<>();
	}

	/**
	 * add child node
	 * @param child node
	 */
	public void add(Node child) {
	    children.add(child);
	}

	/**
	 * delete child node
	 * @param child node
	 */
	public void remove(Node child) {
	    children.remove(child);
	}

	protected void onDraw(Shader shader, GL4 gl) { }

	/**
	 * 
	 * @param shader 
	 * @param parentMatrix 
	 */
	public void draw(Shader shader, Matrix4f parentMatrix) {
		parentMatrix.get(this.worldMatrix);
		this.worldMatrix.mul(this.localMatrix);

		GL4 gl = (GL4) GLContext.getCurrentGL();
		shader.setUniform("u_worldMatrix", this.worldMatrix);
		onDraw(shader, gl);
	
		for (Node child : children) {
			child.draw(shader, worldMatrix);
		}
	}

	protected void onUpdate(float deltaTime) { }

	public void update(float deltaTime) {
		onUpdate(deltaTime);
	    for (Node child : children) {
	    	child.update(deltaTime);
		}
	}

	public void setLocalMatrix(Matrix4f localMatrix) {
		this.localMatrix = localMatrix;
	}

	public float getXPosition() {
		return this.localMatrix.get(new float[16])[12];
	}
	
	public float getYPosition() {
		return this.localMatrix.get(new float[16])[13];
	}
	
	public double distanceFrom(Node other) {
		return Math.sqrt(Math.pow((this.getXPosition() - other.getXPosition()), 2) + Math.pow(this.getYPosition() - other.getYPosition(), 2));
	}
	
	public double facingAngle() {
		return this.localMatrix.getEulerAnglesZYX(new Vector3f()).z;
	}
}
