package com.pro.base.model;

import com.pro.base.World;
import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Paint;
/**
 * æ´¡È¿‡
 * @author Xiloer
 *
 */
public abstract class Spirit extends AnimatDrawable implements EventAble{
	@Override
	public void onLoop(Canvas canvas, Paint paint, World world) {
		onLoop(world);
		super.onLoop(canvas, paint, world);
	}

	@Override
	public void onPause(Canvas canvas, Paint paint, World world) {
		onPause(world);
		super.onPause(canvas, paint, world);
	}
	
	public abstract void onLoop(World world);
	public abstract void onPause(World world);
	
}
