package comp3170.helicopter.nodes;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.utils.Shader;

public class House extends Node {

	private int vertexBuffer;
	private int roofVertexBuffer;
	private float[] vertices = new float[] {
			-1.0f,	0.0f,
			 1.0f,	0.0f,
			 1.0f,	2.0f,

			 1.0f,	2.0f,
			-1.0f,  2.0f,
			-1.0f,  0.0f
	};
	private float[] roofVertices = new float[] {
			 0.0f,	4.0f,
			-1.6f,	2.0f,
			 1.6f,	2.0f,
	};

	private float[] colour = {0.37f, 0.37f, 0.22f};
	private float[] roofColour = {0.45f, 0.0f, 0};
	
	public House(Shader shader) {		
	    this.vertexBuffer = shader.createBuffer(this.vertices);
	    this.roofVertexBuffer = shader.createBuffer(this.roofVertices);    
	}
	
	public House(Shader shader, float x, float y) {
		this(shader);
		localMatrix
				.scale(0.05f, 0.05f, 1)
				.translate(x, y, 0);
	}


	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.vertices.length / 2);           	

		shader.setAttribute("a_position", this.roofVertexBuffer);	    
		shader.setUniform("u_colour", this.roofColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.roofVertices.length / 2);           	
	}
}
