/*
 * Copyright (c) 2017 xiaoming software Technology Co., Ltd.
 * All Rights Reserved.
 */
package com.xiaoming.software.rpc.client;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * rpc client pool
 * 线程安全的连接对象池
 * @author xiaoming
 */
public class XiaoMingRpcClientPool {
	private boolean enableZIP = false;
	private int maxPoolSize = 16;
	private String addres;
	private int port;
	private int timeOut = 1000;
	
	ConcurrentLinkedQueue<XiaoMingRpcClient> clientPool = new ConcurrentLinkedQueue<XiaoMingRpcClient>();
	/**
	 * @param addres
	 * @param port
	 */
	public XiaoMingRpcClientPool(String addres, int port){
		this(addres, port, 16, 1000, false);
	}
	/**
	 * @param addres
	 * @param port
	 * @param maxPoolSize
	 */
	public XiaoMingRpcClientPool(String addres, int port, int maxPoolSize){
		this(addres, port, maxPoolSize, 1000, false);
	}
	/**
	 * @param addres
	 * @param port
	 * @param maxPoolSize
	 * @param timeOut
	 * @param enableZIP 是否启用zip压缩传输？默认false
	 */
	public XiaoMingRpcClientPool(String addres, int port, int maxPoolSize, int timeOut, boolean enableZIP){
		this.enableZIP = enableZIP;
		this.addres = addres;
		this.port = port;
		this.maxPoolSize = maxPoolSize;
		this.timeOut = timeOut;
		
		/* Init client pool */
		for(int i = 0 ;i < this.maxPoolSize;i++){
			newClient();
		}
	}
	/**
	 * Creat a new client
	 */
	private void newClient(){
		if(clientPool.size() < this.maxPoolSize){
			XiaoMingRpcClient client = new XiaoMingRpcClient(this.addres, this.port, this.timeOut, this.enableZIP);
			clientPool.add(client);
		}
	}
	/**
	 * 拿走一个链接对象
	 * @return
	 */
	public synchronized XiaoMingRpcClient getCleint(){
		try {
			if(this.clientPool.size() < 3){
				newClient();
			}
			return this.clientPool.poll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 归还链接对象
	 * @param client
	 */
	public synchronized void returnCleint(XiaoMingRpcClient client){
		if(clientPool.size() < this.maxPoolSize){
			this.clientPool.add(client);
		}else{
			//销毁
			client.close();
		}
	}
	
	
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public String getAddres() {
		return addres;
	}

	public int getPort() {
		return port;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<10000;i++){
			sb.append("老婆ABCdabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjd你好");
		}
		String line = sb.toString();
		
		XiaoMingRpcClientPool pool = new XiaoMingRpcClientPool("192.168.0.119", 8080, 2);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0;i<100;i++){
					XiaoMingRpcClient client = pool.getCleint();
					client.send(line);
					pool.returnCleint(client);
					System.out.println(Thread.currentThread().getName() + "  " + i);
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0;i<100;i++){
					XiaoMingRpcClient client = pool.getCleint();
					client.send(line);
					pool.returnCleint(client);
					System.out.println(Thread.currentThread().getName() + "  " + i);
				}
			}
		}).start();
		
	}
}
