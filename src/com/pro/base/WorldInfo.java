package com.pro.base;
/**
 * ������Ϣ
 * ����������Ϸ�����������Ϣ
 * @author Xiloer
 *
 */
public class WorldInfo {
	//ˢ����
	private int fps;
	//��Ϸ�Ƿ�������״̬
	private boolean isGaming;
	//��Ϸ�Ƿ�����ͣ״̬
	private boolean isPause;
	//��Ϸ����Ļ���
	private int screenWidth;
	//��Ϸ����Ļ�߶�
	private int screenHeight;	
	//��Ļ���ĵ�X
	private int centerX;
	//��Ļ���ĵ�Y
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
		//Ĭ����Ļˢ����Ϊÿ��30֡
		fps = 30;
	}
}
