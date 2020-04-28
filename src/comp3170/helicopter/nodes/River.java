package comp3170.helicopter.nodes;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.utils.Shader;

public class River extends Node {
	
	private int vertexBuffer;
	private float[] vertices = new float[] {
			-1.0f,	-1.0f,
			1.0f,	-1.0f,
			1.0f,	1.0f,

			1.0f,	1.0f,
			-1.0f,  1.0f,
			-1.0f,  -1.0f
	};

	private float[] colour = {0f, 0f, 1f};
	
	public River(Shader shader) {
	    vertexBuffer = shader.createBuffer(this.vertices);
	    localMatrix
				.rotateZ((float) (-Math.PI/180*12))
				.scale(1.3f, 0.2f, 1.0f);
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.vertices.length / 2);
	}
}
