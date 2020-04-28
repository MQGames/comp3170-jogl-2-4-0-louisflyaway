package comp3170.helicopter.nodes;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import comp3170.utils.Shader;

public class Rectangle extends Node {

	private int vertexBuffer;

	private float[] vertices = new float[] {
			-1.0f,	-1.0f,
			 1.0f,	-1.0f,
			 1.0f,	1.0f,

			 1.0f,	1.0f,
			 -1.0f,  1.0f,
			 -1.0f,  -1.0f
	};

	private float[] colour = {0f, 0.3f, 1f};

	public Rectangle(Shader shader) {
		this.vertexBuffer = shader.createBuffer(this.vertices);
	}

	public Rectangle(Shader shader, float[] colour) {
	    this(shader);
		this.colour = colour;
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.vertices.length / 2);
	}
	
	
}
