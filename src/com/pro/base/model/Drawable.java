package com.pro.base.model;

import com.pro.base.World;
import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Paint;

public interface Drawable {
	/**
	 * 每帧的游戏循环回调方法
	 * @param canvas
	 * @param paint
	 */
	public void onLoop(Canvas canvas,Paint paint,World world);
	/**
	 * 游戏暂停时每帧的游戏循环调用方法
	 * @param canvas
	 * @param paint
	 */
	public void onPause(Canvas canvas,Paint paint,World world);
}
