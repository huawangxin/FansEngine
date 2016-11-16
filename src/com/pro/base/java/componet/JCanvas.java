package com.pro.base.java.componet;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.pro.base.World;
import com.pro.base.graphics.Bitmap;
import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Matrix;
import com.pro.base.graphics.Paint;

/**
 * ª≠∞Â¿‡
 * @author Xiloer
 *
 */
public class JCanvas implements Canvas{		
	/*
	 * ªÊ÷∆ª≠∞Â
	 */
	private BufferedImage canvas ;
	private JPaint paint ;
	
	public JCanvas(World world){
		canvas = new BufferedImage(world.getWorldInfo().getScreenWidth(), world.getWorldInfo().getScreenHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		paint = new JPaint(canvas);
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	
	public BufferedImage getCanvas() {
		return canvas;
	}


	@Override
	public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {	
		this.paint.getGraphics().drawImage(bitmap.getImage(),((JMatrix)matrix).trans, null);
	}
	@Override
	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
		this.paint.getGraphics().drawImage(bitmap.getImage(),(int)x,(int)y, null);			
	}	
	
	public class JPaint implements Paint{
		Graphics2D graphics;
		public JPaint(BufferedImage canvas){
			graphics = (Graphics2D)canvas.getGraphics();
		}
		
		@Override
		public void setTypeface(Object obj) {
		}

		public Graphics2D getGraphics() {
			return graphics;
		}

		@Override
		public void setAntiAlias(boolean tf) {
			if(tf){
				RenderingHints qualityHints = graphics.getRenderingHints();
				if(qualityHints==null){
					qualityHints = new  RenderingHints(RenderingHints.KEY_ANTIALIASING,              
					  		RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setRenderingHints(qualityHints);
				}else{
					qualityHints.put(RenderingHints.KEY_ANTIALIASING,               
					  		RenderingHints.VALUE_ANTIALIAS_ON); 
				}				
				qualityHints.put(RenderingHints.KEY_RENDERING,               
						RenderingHints.VALUE_RENDER_QUALITY); 
				qualityHints.put(RenderingHints.KEY_DITHERING ,               
						RenderingHints.VALUE_DITHER_DISABLE); 
			}
		}

		@Override
		public void setFilterBitmap(boolean tf) {
		}

		@Override
		public void setDither(boolean tf) {
			if(tf){
				RenderingHints qualityHints = graphics.getRenderingHints();
				if(qualityHints==null){
					qualityHints = new  RenderingHints(RenderingHints.KEY_DITHERING,              
					  		RenderingHints.VALUE_DITHER_ENABLE);
					graphics.setRenderingHints(qualityHints);
				}else{
					qualityHints.put(RenderingHints.KEY_DITHERING,               
					  		RenderingHints.VALUE_DITHER_ENABLE); 
				}	
				qualityHints.put(RenderingHints.KEY_RENDERING,               
						RenderingHints.VALUE_RENDER_QUALITY);
			}
		}

		@Override
		public void setTextSize(int size) {
		}

		@Override
		public void setColor(int color) {
		}
		
	}

	@Override
	public void translate(double dx, double dy) {
		this.paint.getGraphics().translate(dx, dy);		
	}
}
