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
 * ������
 * �����ʵ�����ڹ�����Ϸ�е�����
 * @author Xiloer
 *
 */
public class World extends JPanel{
	private static final long serialVersionUID = 1L;
	//����
	private static World world;
	/**
	 * �޸�ͼ��Ĳ�������
	 */
	//����ͼ��
	private final static int CHANGE_MODE_UPDATE = 0;
	//���Ԫ�ص�ͼ��
	private final static int CHANGE_MODE_ADD = 1;
	//ɾ��Ԫ�ش�ͼ��
	private final static int CHANGE_MODE_REMOVE = 2;
	//������Ϣ,����һ���̰߳�ȫ��
	private Map<Object,Object> extra = Collections.synchronizedMap(new HashMap<Object,Object>());
	// ͼƬ��ͼ��ֲ�
	private HashMap<Integer, ArrayList<Drawable>> picLayer =new HashMap<Integer, ArrayList<Drawable>>();
	// �޸ĺ��ͼƬ��ͼ��ֲ�,������ݲ�����Ϊ������ͼ�㣬�ֱ�����ӵ�Ԫ�أ���ɾ����Ԫ��
	private HashMap<Integer, ArrayList<Drawable>> addPicLayer = new HashMap<Integer, ArrayList<Drawable>>(),removePicLayer = new HashMap<Integer, ArrayList<Drawable>>();
	// �Ƿ��޸Ĺ�ͼ��
	private boolean changeLayer = false;
	private int picLayerId[] = new int[0]; // ����һ��ͼ��ID�����ٻ�ȡͼ����ƣ�ʡȥ�˴�map�л�ȡ����ͼ���������⣩
	private Paint paint; // ����
	private WorldLoopThread wlt; // ��Ļ�����̣߳����ڿ��ƻ���֡���������Ե���onDraw����
	//��Ϸ�������Ϣ
	private WorldInfo worldInfo;
	//������Ϸ���ĵ�����Xλ��
	private double translateX;
	//������Ϸ���ĵ�����Yλ��
	private double translateY;
	//���̹߳���ģʽ�޸���Ϸ���ĵ�
	private ExecutorService changeCenterExecutor = Executors.newSingleThreadExecutor();
	//����
	private JCanvas canvas ;
	//����Ӧʱ��
	private LinkedList<EventAble> eventAbles;
	/**
	 * ��������
	 * @param screenWidth	��Ļ���
	 * @param screenHeight	��Ļ�߶�
	 */
	private World(int screenWidth,int screenHeight) {
		worldInfo = new WorldInfo();
		eventAbles = new LinkedList<EventAble>();
		worldInfo.setScreenWidth(screenWidth);
		worldInfo.setScreenHeight(screenHeight);
		//������Ļ���
		setSize(screenWidth, screenHeight);
		//������Ļ���ĵ�λ��
		setWorldCenter(screenWidth/2, screenHeight/2);
		//��������
		Frame frame = new JavaFrame();
		frame.addWorld(this);
		canvas = new JCanvas(this);
		paint = canvas.getPaint();
		paint.setAntiAlias(true);//���ÿ����
		paint.setDither(true);
		wlt = new WorldLoopThread();
		frame.show();
	}
	/**
	 * ��ʼ��Ϸ����ģ��
	 * @throws Exception
	 */
	public void start(){
		//������Ϸ��������
		worldInfo.setGaming(true);
		//����ˢ���߳�
		wlt.start();
	}
	/**
	 * ��ͼ������������������߳̿��ƣ������Ե��õ�
	 */
	public void onDraw(Canvas canvas) {
		//����ͼ������
		updatePicLayer(CHANGE_MODE_UPDATE,0,null);
		
		// ��������ͼ�㣬��ͼ���Ⱥ�˳�����
		for (int id : picLayerId) {
				for (Drawable drawable : picLayer.get(id)) {
					//��Ϸ��ͣ
					if(worldInfo.isPause()){
						drawable.onPause(canvas, paint,this);
						continue;
					}
					drawable.onLoop(canvas, paint,this);
				}
		}
	}
	/**
	 * ����ͼ�㣬�����Ϊ���ֲ������ֱ��Ǹ�����ʱͼ���е����ݵ�����ͼ���У�ɾ������ͼ���е�Ԫ�أ���ӻ���ͼ���е�Ԫ��
	 * ������˸��߳�������֤���߳��²���ͼ��İ�ȫ��
	 * @param mode	�Ի���ͼ��Ĳ������ͣ���Ӧ��ǰ���CHANGE_MODE����
	 * @param layerId	������ͼ��ID
	 * @param draw		������ͼ��Ԫ��
	 */
	private synchronized void updatePicLayer(int mode,int layerId,Drawable draw){
		switch(mode){
		//����ʱͼ���е����ݸ���������ͼ����
		case CHANGE_MODE_UPDATE:
			//������޸�
			if(changeLayer){
				//��ͼ������µ�Ԫ��
				for(Entry<Integer,ArrayList<Drawable>>entry:addPicLayer.entrySet()){
						//���Ҫ��ӵ�Ԫ������ͼ�㲻���ڣ��򴴽����ͼ�㣬������ͼ��ID����
						if(this.picLayer.get(entry.getKey())==null){
							this.picLayer.put(entry.getKey(), new ArrayList<Drawable>());
							updateLayerIds(entry.getKey());
						}
						this.picLayer.get(entry.getKey()).addAll(entry.getValue());
				}
				addPicLayer.clear();
				//ɾ��ͼ���е�Ԫ��
				for(Entry<Integer,ArrayList<Drawable>> entry:removePicLayer.entrySet()){
					try {
						this.picLayer.get(entry.getKey()).removeAll(entry.getValue());
					} catch (Exception e) {
						System.out.println("ͼ�����ݲ�����:"+entry.getKey());
					}
				}
				removePicLayer.clear();
				changeLayer = false;			
			}
			break;
		/**
		 * ���������ͼͼ������ӻ���ɾ��Ԫ�أ�������ֱ�Ӳ�������ͼ�㣬���Ǵ���ڶ�Ӧ����ʱͼ���У��ȴ����Ʒ������������н��仯�����ݸ��µ�����ͼ����
		 * ��֤���̲߳�������µİ�ȫ��
		 */
		//���һ��Ԫ��
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
		//ɾ��һ��Ԫ��
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
	 * ��һ���ɻ��Ƶ�ͼ����ͼ����
	 * 
	 * @param layer
	 *            ͼ��� ͼ�����Ȼ��int������ʵ����ֻ֧�ֵ�byte��ԭ����ͼ��û�б�Ҫ��ô��
	 * @param pic
	 *            �ɻ��Ƶ�ͼ
	 */
	public void putDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("ͼ�����ݲ���Ϊ��:��Ӧͼ��:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_ADD,layer,pic);
	}

	/**
	 * ��һ���ɻ��Ƶ�ͼ��ͼ�����Ƴ�
	 * 
	 * @param layer
	 * @param pic
	 */
	public void removeDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("ͼ�����ݲ���Ϊ��:��Ӧͼ��:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_REMOVE,layer,pic);
	}

	/**
	 * ����ͼ��Id
	 * 
	 * @param newLayerId
	 */
	private void updateLayerIds(int newLayerId) {
		// ��ʼ��ͼ��
		if (picLayerId.length == 0) {
			picLayerId = new int[1];
			picLayerId[0] = newLayerId; // ���µ�ͼ��ID��ӵ���ʼ����ͼ��ID������
		} else {
			// ����һ���µ�ͼ�����飬���ȱ�ԭ���Ĵ�1λ
			int picLayerIdFlag[] = new int[picLayerId.length + 1];
			for (int i = 0; i < picLayerId.length; i++) {
				// �������������µ�ͼ��IDС�ڵ�ǰͼ��ID�����µ�ͼ��ID��������
				if (picLayerId[i] > newLayerId) {
					for (int f = picLayerIdFlag.length - 1; f > i; f--) {
						picLayerIdFlag[f] = picLayerId[f - 1];
					}
					picLayerIdFlag[i] = newLayerId;
					break;
				} else {
					picLayerIdFlag[i] = picLayerId[i];
				}
				// ���������󣬶�û�б���ͼ��ID��ģ��ͽ��µ�ͼ��ID�������
				if (i == picLayerId.length - 1) {
					picLayerIdFlag[picLayerIdFlag.length - 1] = newLayerId;
				}
			}
			// ���µ�ͼ��ID���鸲��ԭ�е�
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
	 * ��Ϸ����ѭ���߳�
	 * @author Xiloer
	 *
	 */
	private class WorldLoopThread extends Thread{
		private int drawSpeed;//ÿ�λ��ƺ����Ϣ�����������ֵ�Ǹ��ݳ����еĻ���֡��������
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
	 * ������Ϸ�����ĵ�
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
	 * ��ȡ��Ϸ������Ϣ
	 */
	public WorldInfo getWorldInfo(){
		return worldInfo;
	}
	/**
	 * ���������Ϣ
	 * @param name
	 * @param value
	 */
	public synchronized void putExtra(Object name,Object value){
		extra.put(name, value);
	}
	/**
	 * ��ȡ������Ϣ
	 * @param name
	 * @return
	 */
	public synchronized Object getExtra(Object name){
		return extra.get(name);
	}
	/**
	 * ������Ϸ��ͣ
	 * @param isPause
	 */
	public void setPause(boolean isPause){
		worldInfo.setPause(isPause);
	}
	/**
	 * ����ˢ����
	 * @param fps
	 */
	public void setFps(int fps){
		this.worldInfo.setFps(fps);
		wlt.resetFps();
	}
	/**
	 * ��ȡ����
	 * @return
	 */
	public static World getWorld(int screenWidth,int screenHeight){
		if(world == null){
			world = new World(screenWidth,screenHeight);
		}
		return world;
	}
	
	/**
	 * ��ȡ��ǰ����
	 * @return ��û�д����򷵻�null
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
