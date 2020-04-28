package comp3170.helicopter;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import comp3170.helicopter.nodes.*;
import comp3170.helicopter.nodes.helicopter.Helipad;
import comp3170.utils.InputStateManager;
import org.joml.Matrix4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import comp3170.utils.GLException;
import comp3170.utils.Shader;
import comp3170.helicopter.nodes.helicopter.Helicopter;

public class Starter extends JFrame implements GLEventListener {
	private static final long serialVersionUID = -4428934514676903091L;

	final private File DIRECTORY = new File("src/comp3170/helicopter/shaders");
	private Shader shader;
	private final InputStateManager inputStateManager;
	private Node root;
	private Matrix4f worldMatrix;
	private long oldTime;
	private Helicopter helicopter;
	private Helipad helipad;

	public Starter() {
	    // 添加 GL 画布
		final GLProfile profile = GLProfile.get(GLProfile.GL4);
		final GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		add(canvas);

		// 监听输入状态改变
		inputStateManager = InputStateManager.get();
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				inputStateManager.setMouse(e.getX(), e.getY());
			}
		});
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				inputStateManager.keyPressed(e.getKeyCode());
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				inputStateManager.keyReleased(e.getKeyCode());
			}
		});

		// 开启 GL 绘制
		Animator animator = new Animator(canvas);
		animator.start();
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
	    // 编译着色器
		try {
			String VERTEX_SHADER = "vertex.glsl";
			final File vertexShader = new File(DIRECTORY, VERTEX_SHADER);
			String FRAGMENT_SHADER = "fragment.glsl";
			final File fragmentShader = new File(DIRECTORY, FRAGMENT_SHADER);
			this.shader = new Shader(vertexShader, fragmentShader);
		} catch (final IOException | GLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.worldMatrix = new Matrix4f();

		// TODO 这里可以用 JSON 这类配置场景
		this.root = new Node();
		Node fakeRoot = new Node();
		root.add(fakeRoot);

		// 绘制小地图
		Node miniMap = new Node();
		Matrix4f miniMatrix = new Matrix4f();
		miniMatrix.scale(0.1f).translate(-7f, -7f, 0);
		miniMap.setLocalMatrix(miniMatrix);
		miniMap.add(new Rectangle(this.shader, new float[]{ 0.1f, 0.1f, 0.1f }));
		miniMap.add(fakeRoot);
		root.add(miniMap);

		// 绘制主场景
		fakeRoot.add(new River(this.shader));
		fakeRoot.add(new House(this.shader, 8, -10));
		fakeRoot.add(new House(this.shader, 10, -12));
		fakeRoot.add(new House(this.shader, 11, -18));
		fakeRoot.add(new Tree(this.shader, 12,1.45f, -6.5f));
		fakeRoot.add(new Tree(this.shader, 5,-2f, 7f));
		fakeRoot.add(new Tree(this.shader, 6, -0.2f, 8f));
		fakeRoot.add(new Tree(this.shader, 8, 1f, 5.5f));
		helipad = new Helipad(this.shader, -5.0f, 4f);
		helicopter = new Helicopter(this.shader, helipad.getXPosition(), helipad.getYPosition(), helipad);
		fakeRoot.add(helipad);
		fakeRoot.add(helicopter);
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		final GL4 gl = (GL4) GLContext.getCurrentGL();

		// 刷新
		// 计算每一帧的时间
		final long time = System.currentTimeMillis(); // ms
		final float deltaTime = (time - oldTime) / 1000f; // seconds
		oldTime = time;
		root.update(deltaTime);

		// 背景颜色
		gl.glClearColor(0.1f, 0.4f, 0.1f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT);

		// 启用着色器
		shader.enable();

		// 绘制
		worldMatrix.identity();
		root.draw(shader, worldMatrix);

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
		final GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) { }

	public static void main(final String[] args) {
		Starter starter = new Starter();
		starter.setSize(1024, 1024);
		starter.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		starter.setVisible(true);
	}
}
