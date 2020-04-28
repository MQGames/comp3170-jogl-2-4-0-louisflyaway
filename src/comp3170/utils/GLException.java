package comp3170.utils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;

public class GLException extends Exception {

	private int glError;
	
	public GLException(int glError, GLException suppressed) {
		super(new GLU().gluErrorString(glError));
		this.glError = glError;
		this.addSuppressed(suppressed);
	}
	
	public GLException(String message) {
		super(message);
		this.glError = 0;
	}

	public static void checkGLErrors() throws GLException  {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		GLException e = null;
		
		int error = gl.glGetError();

		while (error != GL.GL_NO_ERROR) {
			e = new GLException(error, e);
			error = gl.glGetError();
		}
		
		if (e != null) {
			throw e;
		}
	}

}
