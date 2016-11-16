package com.pro.base.model;

import com.pro.base.World;
import com.pro.base.graphics.Bitmap;
import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Matrix;
import com.pro.base.graphics.Paint;
import com.pro.base.java.componet.JMatrix;

/**
 * 带有动画效果的可绘对象
 * @author Xiloer
 *
 */
public class AnimatDrawable implements Drawable{
	private Bitmap[] animation;
	private int interval_ms = 0;//帧间隔
	private long pre_time = System.currentTimeMillis();//上次播放时的时间
	private int animation_index = 0;//帧索引
	private boolean playAnimation;
	private Matrix matrix = new JMatrix();
	@Override
	public void onLoop(Canvas canvas, Paint paint, World world) {
		// TODO Auto-generated method stub
		if(playAnimation){
			doAnimation();	
		}
		canvas.drawBitmap(animation[animation_index], matrix, paint);
	}

	@Override
	public void onPause(Canvas canvas, Paint paint, World world) {
		if(playAnimation){
			doAnimation();	
		}
		canvas.drawBitmap(animation[animation_index], matrix, paint);
	}
	
	/**
	 * 设置动画信息
	 * @param Animation
	 */
	public void setAnimation(Bitmap[] animation,int interval_ms){
		this.animation = animation;
		this.interval_ms = interval_ms;
		
	}
	/**
	 * 播放动画
	 * @param isPlay
	 */
	public void playAnimation(boolean isPlay){
		playAnimation = isPlay;
	}
	/**
	 * 计算动画
	 */
	private void doAnimation(){
		if(System.currentTimeMillis()-pre_time>=interval_ms){
			pre_time = System.currentTimeMillis();
			animation_index++;
			if(animation_index==animation.length){
				animation_index=0;
			}
		}
	}
	/**
	 * 获取当前帧所对应的图片
	 * @return
	 */
	public Bitmap getCurrentAnimate(){
		return animation[animation_index];
	}
	/**
	 * 设置平移位置
	 * @param x
	 * @param y
	 */
	public void setLocal(float x,float y){
		matrix.setTranslate(x, y);
	}
	/**
	 * 设置旋转
	 * @param angle
	 * @param x
	 * @param y
	 */
	public void setRotate(float angle, float x, float y){
		matrix.preRotate(angle, x, y);
	}
	/**
	 * 设置缩放
	 * @param x
	 * @param y
	 */
	public void setScale(float x, float y) {
		matrix.preScale(x, y);	
	}
}
