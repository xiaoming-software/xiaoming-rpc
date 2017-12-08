/*
 * Copyright (c) 2017 xiaoming software Technology Co., Ltd.
 * All Rights Reserved.
 */
package com.xiaoming.software.rpc.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.xiaoming.software.rpc.exception.XiaoMingRpcException;


/**
 * rpc client
 * @author xiaoming
 */
public class XiaoMingRpcClient {
	private String addres;
	private int port;
	private Socket socket = null;
	
	public XiaoMingRpcClient(String addres, int port){
		this.addres = addres;
		this.port = port;
		
		/* Start to connection */
		try {
			socket = new Socket(this.addres, this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	
	public void send(String msg){
		if(socket != null){
			OutputStream out = null;
			try {
				out = socket.getOutputStream();
				BufferedOutputStream bout = new BufferedOutputStream(out);
				//
				bout.write(msg.getBytes("UTF-8"));
				bout.flush();
				socket.shutdownOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			throw new XiaoMingRpcException("Socket connection is empty!");
		}
	}
	
}
