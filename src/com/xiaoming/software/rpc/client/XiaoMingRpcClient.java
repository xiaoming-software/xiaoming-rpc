/*
 * Copyright (c) 2017 xiaoming software Technology Co., Ltd.
 * All Rights Reserved.
 */
package com.xiaoming.software.rpc.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.xiaoming.concurrent.zip.ConcurrentZip;

/**
 * rpc client
 * 不是线程安全的长链接.如果是多线程并发场景下建议去使用client pool.
 * @author xiaoming
 */
public class XiaoMingRpcClient {
	private boolean enableZIP = false;
	private String addres;
	private int port;
	private int timeOut = 3000;
	private Socket socket = null;
	/**
	 * @param addres
	 * @param port
	 * @param timeOut
	 */
	public XiaoMingRpcClient(String addres, int port, int timeOut){
		this(addres, port, timeOut, false);
	}
	/**
	 * @param addres
	 * @param port
	 * @param timeOut
	 * @param enableZIP 是否启用压缩传输？默认false
	 */
	public XiaoMingRpcClient(String addres, int port, int timeOut, boolean enableZIP){
		this.enableZIP = enableZIP;
		this.addres = addres;
		this.port = port;
		this.timeOut = timeOut;
		
		/* Start to connection */
		newSocket();
	}
	/**
	 * Get a new socket.
	 */
	private synchronized void newSocket(){
		try {
			if(socket == null){
				socket = new Socket();
				socket.connect(new InetSocketAddress(this.addres, this.port), timeOut);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	/**
	 * Send a msg.
	 * @param msg
	 */
	public void send(String msg){
		if(socket == null){
			newSocket();
		}
		try {
			/** Enable zip transfer msg */
			if(enableZIP){
				int threadNum = getThreadNum(msg);
				msg = ConcurrentZip.concurrentGZip(msg, threadNum);
			}
			
			OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
			BufferedWriter oos = new BufferedWriter(os);
			oos.write(msg);
			oos.newLine();
			oos.flush();
		} catch (Exception e) {
			close();
			e.printStackTrace();
		}
	}
	/**
	 * 根据msg解析得出thread num，按照1M单位拆分。
	 * @param msg
	 * @return
	 */
	private int getThreadNum(String msg) {
		int num = 1;
		if(msg.length() > 1024){
			num = msg.length() / 1024;
		}
		return num;
	}
	
	/**
	 * 
	 */
	public synchronized void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}
}
