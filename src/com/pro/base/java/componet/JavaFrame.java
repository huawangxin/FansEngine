package com.pro.base.java.componet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import com.pro.base.World;
import com.pro.base.graphics.Frame;

public class JavaFrame implements Frame{
	private JFrame frame;
	
	@Override
	public void addWorld(final World world) {
		frame = new JFrame();
    	frame.setSize(world.getWorldInfo().getScreenWidth(),world.getWorldInfo().getScreenHeight());
    	frame.setUndecorated(true); // 去掉窗口的装饰 
//    	frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);//采用指定的窗口装饰风格 
//		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(world);
//		frame.setAlwaysOnTop(true);
		frame.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
//				world.setPause(false);
				world.onReleased(e.getX(), e.getY());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
//				world.setPause(true);
//				world.setFps(world.getWorldInfo().getFps()+1);
				world.onTouch(e.getX(), e.getY());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void show() {
		frame.setVisible(true);
	}

}
