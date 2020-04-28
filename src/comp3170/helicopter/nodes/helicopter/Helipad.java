package comp3170.helicopter.nodes.helicopter;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.helicopter.nodes.Node;
import comp3170.utils.Shader;

public class Helipad extends Node {

	private static final float DISTANCE_THRESHOLD = 0.05f;

	private boolean canLand;
	
	private int vertexBuffer;
	private int iVertexBuffer;
	private int hVertexBuffer;

	private Helicopter helicopter;
	
	private float[] baseVertices = new float[] {
			-1.0f, -1.0f,
			 1.0f, -1.0f,
			 1.0f,	1.0f,

			 1.0f,	1.0f,
			-1.0f,  1.0f,
			-1.0f, -1.0f
	};
	
	private float[] iVertices = new float[] {
			-1.0f, -1.0f,
			-0.8f, -1.0f,
			-1.0f, -0.8f,
			
			 1.0f,	1.0f,
			 0.8f,  1.0f,
			 1.0f,  0.8f,
			 
			-1.0f,  1.0f,
			-1.0f,  0.8f,
			-0.8f,  1.0f,
			
			 1.0f, -1.0f,
			 1.0f, -0.8f,
			 0.8f, -1.0f
	};
	
	private float[] hVertices = new float[] {
		    -0.75f, -0.75f,
		    -0.75f,  0.75f,
		    -0.25f,  0.75f,
		     
		    -0.75f, -0.75f,
		    -0.25f,  0.75f,
		    -0.25f, -0.75f,
		     
		    -0.25f, -0.25f,
		    -0.25f,  0.25f,
		     0.25f,  0.25f,
		     
		    -0.25f, -0.25f,
		     0.25f,  0.25f,
		     0.25f, -0.25f,
		     
		     0.25f, -0.75f,
		     0.25f,  0.75f,
		     0.75f,  0.75f,
		     
		     0.25f, -0.75f,
		     0.75f,  0.75f,
		     0.75f, -0.75f
	};
	
	private float[] baseColour  = {0.51f, 0.51f, 0.51f};
	private float[] hColour     = {0.17f, 0.17f, 0.17f};
	private float[] iColour;
	
	private float[] notOkColour = {1f, 0f, 0f};
	private float[] okColour    = {0f, 0.75f, 0f};
	
	public Helipad(Shader shader) {
	    this.vertexBuffer = shader.createBuffer(this.baseVertices);
	    this.iVertexBuffer = shader.createBuffer(this.iVertices);
	    this.hVertexBuffer = shader.createBuffer(this.hVertices);
	}
	
	public Helipad(Shader shader, float x, float y) {
		this(shader);
		localMatrix
				.scale(0.15f, 0.15f, 1)
				.translate(x, y, 0)
                .rotateZ((float) Math.PI *2);
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.baseColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.baseVertices.length / 2);
        
		shader.setAttribute("a_position", this.iVertexBuffer);
		shader.setUniform("u_colour", this.iColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.iVertices.length / 2);
        
        shader.setAttribute("a_position", this.hVertexBuffer);
		shader.setUniform("u_colour", this.hColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.hVertices.length / 2);
	}

	public void setCanLand(boolean canLand) {
		this.canLand = canLand;
		if (this.canLand) {
			this.iColour = this.okColour;
		}
		else {
			this.iColour = this.notOkColour;
		}
	}

	// checking landing
	public boolean isCanLand() {
		return canLand;
	}

	@Override
	protected void onUpdate(float deltaTime) {
		// checking landing condition
		if (helicopter.distanceFrom(this) < DISTANCE_THRESHOLD) {
			setCanLand(true);
		} else {
			setCanLand(false);
		}
	}

	// Associated helicopter
	public void setHelicopter(Helicopter helicopter) {
		this.helicopter = helicopter;
	}
}
