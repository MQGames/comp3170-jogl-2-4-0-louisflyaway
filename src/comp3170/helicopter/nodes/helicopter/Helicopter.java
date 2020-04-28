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
	// 最大移动速度
	private final static float MAX_MOVE_SPEED = 3f;
	// 加速度参数
	private final float acceleration = 0.003f;
	// 状态变量
	private boolean moving = false;
	private boolean landing = true;
	private boolean landed = false;
	private final float rotationSpeed = 1.5f;
	// 鼠标事件触发的目标坐标
	private float destinationX = -1;
	private float destinationY = -1;
	// 螺旋桨
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
	    // 螺旋桨
	    rotor1 = new Rotor(shader, true,  0.5f);
	    rotor2 = new Rotor(shader, false, -0.6f);
		add(rotor1);
		add(rotor2);
	    // 关联着陆节点
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
        // 根据鼠标事件设置目标
		if (inputStateManager.isMouseSet()) {
			setDestination(inputStateManager.getMouseX(), inputStateManager.getMouseY());
			inputStateManager.resetMouse();
		}
		// 前进
		setMove(inputStateManager.isUpKeyDown());
		// 计算方向改变角度
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
		// 计算移动距离
		move(deltaTime);
	}

	// 计算移动距离
	private void move(float deltaTime) {
		if (this.landing) {
			onLanding();
		}
		else {
			onTakingOff();
			// 螺旋桨的速度需要达到阈值
			if (rotor1.isReady() && rotor2.isReady()) {
				if (this.destinationX != -1 && this.destinationY != -1) {
				    // 鼠标驱动移动
					moveWithTarget(deltaTime);
				}
				else {
					// 键盘驱动
					moveWithDirection();
				}
			}
		}
        // x 是前进方向
		this.localMatrix.translate(this.moveSpeed * deltaTime, 0, 0);
	}

	// 起飞：放大
	private void onTakingOff() {
		if (rotor1.isReady() && this.localMatrix.getScale(new Vector3f()).x < 0.15) {
			float factor = 1.0001f;
			this.localMatrix.scale(factor, factor, 1f);
		}
	}

	// 降落：缩小
	private void onLanding() {
		if (this.localMatrix.getScale(new Vector3f()).x > 0.08) {
			float factor = 0.9999f;
			this.localMatrix.scale(factor, factor, 1f);
		}
		else {
			landed = true;
		}
	}

	// 鼠标触发移动（目标驱动移动）
	private void moveWithTarget(float deltaTime) {
		rotateToMouseCoordinates(deltaTime);
		moveTowardsMouseCoordinates();
	}

	// 朝目标移动
	// 距离差不多的时候开始减速
	private void moveTowardsMouseCoordinates() {
		if (Math.abs(angleCalculate()) > Math.PI/ (10 * distance())) {
			if (distance() > 0.18) {
				accelerate();
			} else {
				decelerate();
			}
		}
	}

	// 控制旋转
	private void rotateToMouseCoordinates(float deltaTime) {
		if (angleCalculate() < Math.PI && angleCalculate() > -Math.PI) {
			if (angleCalculate() > 0) {
				this.localMatrix.rotateZ(-this.rotationSpeed * deltaTime);
				// 防止转过头
				if (angleCalculate() <= 0 ) {
					this.localMatrix.rotateZ(this.rotationSpeed * deltaTime);
				}
			}
			else {
				this.localMatrix.rotateZ(this.rotationSpeed * deltaTime);
				// 同上
				if (angleCalculate() > 0) {
					this.localMatrix.rotateZ(-this.rotationSpeed * deltaTime);
				}
			}
		}
	}

	// 计算角度
	private double angleCalculate() {
		double angleToDestination = Math.atan2(this.getYPosition() - destinationY, this.getXPosition() - destinationX);
		return Math.atan2(Math.sin(angleToDestination - facingAngle()), Math.cos(angleToDestination - facingAngle()));
	}

	// 计算距离
	public double distance() {
		return Math.sqrt(Math.pow((this.getXPosition() - this.destinationX), 2) + Math.pow(this.getYPosition() - this.destinationY, 2));
	}

	// 键盘触发移动（前向移动）
	private void moveWithDirection() {
		if (this.moving) {
			accelerate();
		}
		else if (this.moveSpeed > 0) {
			decelerate();
		}
	}

	// 加速
	private void accelerate() {
		this.moveSpeed += this.acceleration;
		if (this.moveSpeed > Helicopter.MAX_MOVE_SPEED) {
			this.moveSpeed = Helicopter.MAX_MOVE_SPEED;
		}
	}

	// 减速
	private void decelerate() {
		this.moveSpeed -= this.acceleration;
		if (this.moveSpeed < 0) {
			this.moveSpeed = 0;
		}
	}

	// 键盘触发移动
	public void setMove(boolean moving) {
		if (!this.moving && moving) {
		    // 重置鼠标触发的移动
			resetDestination();
		}
		this.moving = moving;
	}

	// 控制机身旋转
	public void rotate(boolean left, float deltaTime) {
		if (!landed && rotor1.isReady()) {
			int negation = left ? 1 : -1;
			resetDestination();
			this.localMatrix.rotateZ(rotationSpeed * negation * deltaTime);
		}
	}

	// 设定目标坐标
	public void setDestination(float x, float y) {
	    // 转换坐标系
		this.destinationX = (x / 1000) * 2 - 1;
		this.destinationY = (1 - (y / 1000)) * 2 - 1;
	}

	// 重置目标（鼠标）
	public void resetDestination() {
		this.destinationX = -1;
		this.destinationY = -1;
	}

	// 是否着陆
	public boolean isLanding() {
		return landing;
	}

	// 着陆
	public void land() {
		this.landing = true;
		rotor1.stop();
		rotor2.stop();
	}

	// 起飞
	public void takeOff() {
		this.landing = false;
		this.landed = false;
		rotor1.start();
		rotor2.start();
	}
}