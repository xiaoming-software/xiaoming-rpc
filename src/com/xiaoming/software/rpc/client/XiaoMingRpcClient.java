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

/**
 * rpc client
 * @author xiaoming
 */
public class XiaoMingRpcClient {
	private String addres;
	private int port;
	private int timeOut = 3000;
	private Socket socket = null;
	
	public XiaoMingRpcClient(String addres, int port, int timeOut){
		this.addres = addres;
		this.port = port;
		this.timeOut = timeOut;
		
		/* Start to connection */
		newSocket();
	}
	
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
	
	
	public void send(String msg){
		if(socket == null){
			newSocket();
		}
		try {
			OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
			BufferedWriter oos = new BufferedWriter(os);
			oos.write(msg);
			oos.newLine();
			oos.flush();
		} catch (Exception e) {
			close();
			socket = null;
			e.printStackTrace();
		}
	}
	
	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
                   socket = null; 
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<10000;i++){
			sb.append("老婆ABCdabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjddabsdbabsdbasdahsdjasgdasdgjasgdhjasgdhsjagdasgdshadjasgdgasjd你好");
		}
		String line = sb.toString();
		System.out.println(line.length());
		
		XiaoMingRpcClient client = new XiaoMingRpcClient("192.168.0.222", 8080, 1000000);
		for(int i = 0;i<1000;i++){
			client.send(line);
			System.out.println(i);
		}
		
		//关闭客户端
		client.close();
		
	}
	
}
