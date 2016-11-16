package com.pro.base.model;

import com.pro.base.World;
import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Paint;

public interface Drawable {
	/**
	 * ÿ֡����Ϸѭ���ص�����
	 * @param canvas
	 * @param paint
	 */
	public void onLoop(Canvas canvas,Paint paint,World world);
	/**
	 * ��Ϸ��ͣʱÿ֡����Ϸѭ�����÷���
	 * @param canvas
	 * @param paint
	 */
	public void onPause(Canvas canvas,Paint paint,World world);
}
