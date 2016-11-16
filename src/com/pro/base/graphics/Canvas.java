package com.pro.base.graphics;

/*
 * ����
 */
public interface Canvas {
	/**
	 * ��������ƽ��
	 * @param dx ƽ�Ƶ�x
	 * @param dy ƽ�Ƶ�y
	 */
	public void translate(double dx, double dy);
	
	public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint);

	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint);
}
