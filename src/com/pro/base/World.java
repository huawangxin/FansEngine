package com.pro.base;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

import com.pro.base.graphics.Canvas;
import com.pro.base.graphics.Frame;
import com.pro.base.graphics.Paint;
import com.pro.base.java.componet.JCanvas;
import com.pro.base.java.componet.JavaFrame;
import com.pro.base.model.Drawable;
import com.pro.base.model.EventAble;


/**
 * 世界类
 * 该类的实例用于构建游戏中的世界
 * @author Xiloer
 *
 */
public class World extends JPanel{
	private static final long serialVersionUID = 1L;
	//世界
	private static World world;
	/**
	 * 修改图层的操作定义
	 */
	//更新图层
	private final static int CHANGE_MODE_UPDATE = 0;
	//添加元素到图层
	private final static int CHANGE_MODE_ADD = 1;
	//删除元素从图层
	private final static int CHANGE_MODE_REMOVE = 2;
	//额外信息,这是一个线程安全的
	private Map<Object,Object> extra = Collections.synchronizedMap(new HashMap<Object,Object>());
	// 图片的图层分布
	private HashMap<Integer, ArrayList<Drawable>> picLayer =new HashMap<Integer, ArrayList<Drawable>>();
	// 修改后的图片的图层分布,这里根据操作分为了两个图层，分别是添加的元素，和删除的元素
	private HashMap<Integer, ArrayList<Drawable>> addPicLayer = new HashMap<Integer, ArrayList<Drawable>>(),removePicLayer = new HashMap<Integer, ArrayList<Drawable>>();
	// 是否修改过图层
	private boolean changeLayer = false;
	private int picLayerId[] = new int[0]; // 定义一个图层ID，加速获取图层绘制（省去了从map中获取各个图层排序问题）
	private Paint paint; // 画笔
	private WorldLoopThread wlt; // 屏幕绘制线程，用于控制绘制帧数，周期性调用onDraw方法
	//游戏世界的信息
	private WorldInfo worldInfo;
	//根据游戏中心点的相对X位置
	private double translateX;
	//根据游戏中心点的相对Y位置
	private double translateY;
	//单线程工作模式修改游戏中心点
	private ExecutorService changeCenterExecutor = Executors.newSingleThreadExecutor();
	//画板
	private JCanvas canvas ;
	//可响应时间
	private LinkedList<EventAble> eventAbles;
	/**
	 * 创建世界
	 * @param screenWidth	屏幕宽度
	 * @param screenHeight	屏幕高度
	 */
	private World(int screenWidth,int screenHeight) {
		worldInfo = new WorldInfo();
		eventAbles = new LinkedList<EventAble>();
		worldInfo.setScreenWidth(screenWidth);
		worldInfo.setScreenHeight(screenHeight);
		//设置屏幕宽高
		setSize(screenWidth, screenHeight);
		//设置屏幕中心点位置
		setWorldCenter(screenWidth/2, screenHeight/2);
		//创建窗口
		Frame frame = new JavaFrame();
		frame.addWorld(this);
		canvas = new JCanvas(this);
		paint = canvas.getPaint();
		paint.setAntiAlias(true);//设置抗锯齿
		paint.setDither(true);
		wlt = new WorldLoopThread();
		frame.show();
	}
	/**
	 * 开始游戏世界模拟
	 * @throws Exception
	 */
	public void start(){
		//设置游戏世界运行
		worldInfo.setGaming(true);
		//启动刷新线程
		wlt.start();
	}
	/**
	 * 绘图方法，这个方法是由线程控制，周期性调用的
	 */
	public void onDraw(Canvas canvas) {
		//更新图层内容
		updatePicLayer(CHANGE_MODE_UPDATE,0,null);
		
		// 遍历所有图层，按图层先后顺序绘制
		for (int id : picLayerId) {
				for (Drawable drawable : picLayer.get(id)) {
					//游戏暂停
					if(worldInfo.isPause()){
						drawable.onPause(canvas, paint,this);
						continue;
					}
					drawable.onLoop(canvas, paint,this);
				}
		}
	}
	/**
	 * 更新图层，这里分为三种操作，分别是更新临时图层中的内容到绘制图层中，删除绘制图层中的元素，添加绘制图层中的元素
	 * 这里加了个线程锁，保证多线程下操作图层的安全性
	 * @param mode	对绘制图层的操作类型，对应当前类的CHANGE_MODE常量
	 * @param layerId	操作的图层ID
	 * @param draw		操作的图层元素
	 */
	private synchronized void updatePicLayer(int mode,int layerId,Drawable draw){
		switch(mode){
		//将临时图层中的内容更新至绘制图层中
		case CHANGE_MODE_UPDATE:
			//如果有修改
			if(changeLayer){
				//向图层添加新的元素
				for(Entry<Integer,ArrayList<Drawable>>entry:addPicLayer.entrySet()){
						//如果要添加的元素所处图层不存在，则创建这个图层，并更新图层ID数组
						if(this.picLayer.get(entry.getKey())==null){
							this.picLayer.put(entry.getKey(), new ArrayList<Drawable>());
							updateLayerIds(entry.getKey());
						}
						this.picLayer.get(entry.getKey()).addAll(entry.getValue());
				}
				addPicLayer.clear();
				//删除图层中的元素
				for(Entry<Integer,ArrayList<Drawable>> entry:removePicLayer.entrySet()){
					try {
						this.picLayer.get(entry.getKey()).removeAll(entry.getValue());
					} catch (Exception e) {
						System.out.println("图层内容不存在:"+entry.getKey());
					}
				}
				removePicLayer.clear();
				changeLayer = false;			
			}
			break;
		/**
		 * 无论是向绘图图层中添加还是删除元素，都不是直接操作绘制图层，都是存放在对应的临时图层中，等待绘制方法绘制周期中将变化的内容更新到绘制图层中
		 * 保证多线程操作情况下的安全性
		 */
		//添加一个元素
		case CHANGE_MODE_ADD:
			ArrayList<Drawable> al = addPicLayer.get(layerId);
			if(al==null){
				al = new ArrayList<Drawable>();
				addPicLayer.put(layerId, al);
				
			}
			al.add(draw);
			if(draw instanceof EventAble){
				eventAbles.add((EventAble)draw);
			}
			changeLayer = true;	
			break;
		//删除一个元素
		case CHANGE_MODE_REMOVE:
			ArrayList<Drawable> al1 = removePicLayer.get(layerId);
			if(al1==null){
				al1 = new ArrayList<Drawable>();
				removePicLayer.put(layerId, al1);
			}
			al1.add(draw);
			if(draw instanceof EventAble){
				eventAbles.remove(draw);
			}
			changeLayer = true;	
			break;
		}
		
	}
	
	/**
	 * 将一个可绘制的图放入图层中
	 * 
	 * @param layer
	 *            图层号 图层号虽然是int，但是实际上只支持到byte，原因是图层没有必要那么多
	 * @param pic
	 *            可绘制的图
	 */
	public void putDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("图层内容不能为空:对应图层:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_ADD,layer,pic);
	}

	/**
	 * 将一个可绘制的图从图层中移除
	 * 
	 * @param layer
	 * @param pic
	 */
	public void removeDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("图层内容不能为空:对应图层:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_REMOVE,layer,pic);
	}

	/**
	 * 更新图层Id
	 * 
	 * @param newLayerId
	 */
	private void updateLayerIds(int newLayerId) {
		// 初始化图层
		if (picLayerId.length == 0) {
			picLayerId = new int[1];
			picLayerId[0] = newLayerId; // 将新的图层ID添加到初始化的图层ID数组中
		} else {
			// 创建一个新的图层数组，长度比原来的大1位
			int picLayerIdFlag[] = new int[picLayerId.length + 1];
			for (int i = 0; i < picLayerId.length; i++) {
				// 排序操作，如果新的图层ID小于当前图层ID，讲新的图层ID插入其中
				if (picLayerId[i] > newLayerId) {
					for (int f = picLayerIdFlag.length - 1; f > i; f--) {
						picLayerIdFlag[f] = picLayerId[f - 1];
					}
					picLayerIdFlag[i] = newLayerId;
					break;
				} else {
					picLayerIdFlag[i] = picLayerId[i];
				}
				// 如果到了最后，都没有比新图层ID大的，就将新的图层ID存入最后
				if (i == picLayerId.length - 1) {
					picLayerIdFlag[picLayerIdFlag.length - 1] = newLayerId;
				}
			}
			// 将新的图层ID数组覆盖原有的
			this.picLayerId = picLayerIdFlag;
		}
	}
	
	@Override
	public synchronized void paint(Graphics g) {
			g.drawImage(canvas.getCanvas(), 0, 0, null);		
	}
	
	public synchronized Canvas lockCanvas() {
			return canvas;		
	}

	
	public void unlockCanvasAndPost(Canvas canvas) {
		repaint();		
	}
	
	
	/**
	 * 游戏世界循环线程
	 * @author Xiloer
	 *
	 */
	private class WorldLoopThread extends Thread{
		private int drawSpeed;//每次绘制后的休息毫秒数，这个值是根据常量中的绘制帧数决定的
		private long startFps;
		public WorldLoopThread(){
			resetFps();
		}
		public void resetFps(){
			drawSpeed = 1000/worldInfo.getFps();
		}
		public void run(){
			Canvas canvas = null;
//			long sum = 0;
//			long start = System.currentTimeMillis();
			while(worldInfo.isGaming()){
//				if(System.currentTimeMillis()-start>=1000){
//					start = System.currentTimeMillis();
//					System.out.println("fps:"+sum);
//					sum = 0;
//				}
//				sum ++;
				startFps = System.currentTimeMillis();
				try{
					canvas = lockCanvas();
					synchronized (World.this) {
						if(canvas!=null){
							World.this.onDraw(canvas);
						}
					}
				}catch(Exception e){
//					Log.e(this.getName(), e.toString());
					e.printStackTrace();
				}finally{
					try{
						unlockCanvasAndPost(canvas);
					}catch(Exception e){
//						Log.e(this.getName(), e.toString());
					}
				}
				try{
					startFps = System.currentTimeMillis()-startFps;
					if(startFps<=drawSpeed){
						Thread.sleep(drawSpeed-startFps);	
					}
				}catch(Exception e){
					
				}
			}
		}
	}
	/**
	 * 设置游戏的中心点
	 * @param x
	 * @param y
	 */
	public void setWorldCenter(final int x,final int y){	
		changeCenterExecutor.execute(
				new Runnable(){
					public void run(){
						while(true){
							synchronized (World.this) {
								if(canvas!=null){
									canvas.translate(-translateX, -translateY);
									translateX = worldInfo.getScreenWidth()/2 -x;
									translateY = worldInfo.getScreenHeight()/2-y;
									canvas.translate(translateX, translateY);
									worldInfo.setCenterX(x);
									worldInfo.setCenterY(y);
									break;
								}
							}	
						}
					}
				});
	}
	
	/**
	 * 获取游戏世界信息
	 */
	public WorldInfo getWorldInfo(){
		return worldInfo;
	}
	/**
	 * 放入额外信息
	 * @param name
	 * @param value
	 */
	public synchronized void putExtra(Object name,Object value){
		extra.put(name, value);
	}
	/**
	 * 获取额外信息
	 * @param name
	 * @return
	 */
	public synchronized Object getExtra(Object name){
		return extra.get(name);
	}
	/**
	 * 设置游戏暂停
	 * @param isPause
	 */
	public void setPause(boolean isPause){
		worldInfo.setPause(isPause);
	}
	/**
	 * 设置刷新率
	 * @param fps
	 */
	public void setFps(int fps){
		this.worldInfo.setFps(fps);
		wlt.resetFps();
	}
	/**
	 * 获取世界
	 * @return
	 */
	public static World getWorld(int screenWidth,int screenHeight){
		if(world == null){
			world = new World(screenWidth,screenHeight);
		}
		return world;
	}
	
	/**
	 * 获取当前世界
	 * @return 若没有创建则返回null
	 */
	public static World getCurrentWorld(){
		return world;
	}
	
	public void onTouch(int x,int y){
		for(EventAble e : eventAbles){
			if(e.onTouch(x, y)){
				break;
			}
		}
	}
	
	public void onReleased(int x,int y){
		for(EventAble e : eventAbles){
			if(e.onReleased(x, y)){
				break;
			}
		}
	}
	
}
