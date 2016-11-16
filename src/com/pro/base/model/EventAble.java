package com.pro.base.model;


/**
 * 可响应事件的基类
 * @author Xiloer
 *
 */
public interface EventAble {
	public boolean onTouch(int x,int y);
	public boolean onReleased(int x,int y);
}
