package com.pro.base;
/**
 * 世界信息
 * 用于描述游戏世界的属性信息
 * @author Xiloer
 *
 */
public class WorldInfo {
	//刷新率
	private int fps;
	//游戏是否处于运行状态
	private boolean isGaming;
	//游戏是否处于暂停状态
	private boolean isPause;
	//游戏的屏幕宽度
	private int screenWidth;
	//游戏的屏幕高度
	private int screenHeight;	
	//屏幕中心点X
	private int centerX;
	//屏幕中心点Y
	private int centerY;
	public boolean isGaming() {
		return isGaming;
	}

	

	public int getFps() {
		return fps;
	}



	public void setFps(int fps) {
		this.fps = fps;
	}



	public int getCenterX() {
		return centerX;
	}



	protected void setCenterX(int centerX) {
		this.centerX = centerX;
	}



	public int getCenterY() {
		return centerY;
	}



	protected void setCenterY(int centerY) {
		this.centerY = centerY;
	}



	public void setGaming(boolean isGaming) {
		this.isGaming = isGaming;
	}



	public boolean isPause() {
		return isPause;
	}



	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}



	public int getScreenWidth() {
		return screenWidth;
	}



	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}



	public int getScreenHeight() {
		return screenHeight;
	}



	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}



	protected WorldInfo(){
		//默认屏幕刷新率为每秒30帧
		fps = 30;
	}
}
