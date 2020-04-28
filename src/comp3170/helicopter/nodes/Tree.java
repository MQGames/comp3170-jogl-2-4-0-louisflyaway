package comp3170.helicopter.nodes;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import comp3170.utils.Shader;

public class Tree extends Node {
	
	private int vertexBuffer;
	private int leavesVertexBuffer;
	
	private float[] trunkVertices = new float[] {
			-0.3f, -2.0f,
			 0.3f, -2.0f,
			 0.3f,	0.0f,

			 0.3f,	0.0f,
			-0.3f,  0.0f,
			-0.3f, -2.0f
	};
	private float[] leavesVertices;
	private final static float LEAVES_RADIUS = 1f;
	
	private float[] trunkColour = {0.62f, 0.4f, 0f};
	private float[] leafColour = {0f, 0.85f, 0f};
	
	public Tree(Shader shader, int polyNum) {		
	    vertexBuffer = shader.createBuffer(this.trunkVertices);
		createLeaves(shader, polyNum);
	}

	public Tree(Shader shader, int polyNum, float x, float y) {
		this(shader, polyNum);
		this.localMatrix.scale(0.1f, 0.1f, 1);
		this.localMatrix.translate(x, y, 0);
	}
	
	private void createLeaves(Shader shader, int polyNum) {
		leavesVertices = new float[polyNum * 3 * 2];
		for (int i = 0; i < polyNum; i++) {
			leavesVertices[i*6]   = (float) (LEAVES_RADIUS * Math.cos(2 * Math.PI * i / polyNum));
			leavesVertices[i*6+1] = (float) (LEAVES_RADIUS * Math.sin(2 * Math.PI * i / polyNum));
			leavesVertices[i*6+2] = (float) (LEAVES_RADIUS * Math.cos(2 * Math.PI * (i+1) / polyNum));
			leavesVertices[i*6+3] = (float) (LEAVES_RADIUS * Math.sin(2 * Math.PI * (i+1) / polyNum));
			leavesVertices[i*6+4] = 0;
			leavesVertices[i*6+5] = 0;
		}
		leavesVertexBuffer = shader.createBuffer(leavesVertices);
	}

	@Override
	protected void onDraw(Shader shader, GL4 gl) {
		shader.setAttribute("a_position", this.vertexBuffer);
		shader.setUniform("u_colour", this.trunkColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.trunkVertices.length / 2);
        
        shader.setAttribute("a_position", this.leavesVertexBuffer);
		shader.setUniform("u_colour", this.leafColour);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.leavesVertices.length / 2);
	}
}
