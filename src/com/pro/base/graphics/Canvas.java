package com.pro.base.graphics;

/*
 * 画板
 */
public interface Canvas {
	/**
	 * 画板整体平移
	 * @param dx 平移的x
	 * @param dy 平移的y
	 */
	public void translate(double dx, double dy);
	
	public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint);

	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint);
}
