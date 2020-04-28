package comp3170.utils;

import java.awt.event.KeyEvent;

/**
 * 输入状态管理，用于保持用户点击、键盘输入事件
 * 使用一个简单有效的单例模式
 */
public class InputStateManager {
	private static InputStateManager INSTANCE = new InputStateManager();
	private InputStateManager() { }
	public static InputStateManager get() {
		return INSTANCE;
	}

	private boolean upKeyDown;
	private boolean leftKeyDown;
	private boolean rightKeyDown;
	private boolean lKeyDown;
	private boolean tKeyDown;
	private boolean cKeyDown;
	private int mouseX = -1;
	private int mouseY = -1;

	public boolean isUpKeyDown()    { return upKeyDown; }
	public boolean isLeftKeyDown()  { return leftKeyDown; }
	public boolean isRightKeyDown() { return rightKeyDown; }
	public boolean isLKeyDown()     { return lKeyDown; }
	public boolean isTKeyDown()     { return tKeyDown; }
	public boolean iscKeyDown() { return cKeyDown; }

	public int     getMouseX()      { return mouseX; }
	public int     getMouseY()      { return mouseY; }

	public void keyPressed(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				upKeyDown = true;
				break;
			case KeyEvent.VK_LEFT:
				leftKeyDown = true;
				break;
			case KeyEvent.VK_RIGHT:
				rightKeyDown= true;
				break;
			case KeyEvent.VK_L:
				lKeyDown= true;
				break;
			case KeyEvent.VK_T:
				tKeyDown= true;
				break;
			case KeyEvent.VK_C:
				cKeyDown= true;
				break;
		}
	}

	public void keyReleased(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				upKeyDown = false;
				break;
			case KeyEvent.VK_LEFT:
				leftKeyDown = false;
				break;
			case KeyEvent.VK_RIGHT:
				rightKeyDown= false;
				break;
			case KeyEvent.VK_L:
				lKeyDown= false;
				break;
			case KeyEvent.VK_T:
				tKeyDown= false;
				break;
			case KeyEvent.VK_C:
				cKeyDown= false;
				break;
		}
	}

	public void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	public void resetMouse() {
		mouseX = -1;
		mouseY = -1;
	}

	public boolean isMouseSet() {
		return mouseX != -1 && mouseY != -1;
	}
}
