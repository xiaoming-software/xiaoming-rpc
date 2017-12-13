/*
 * Copyright (c) 2017 xiaoming software Technology Co., Ltd.
 * All Rights Reserved.
 */
package com.xiaoming.software.rpc.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xiaoming.software.rpc.client.XiaoMingRpcClient;
import com.xiaoming.software.rpc.server.handler.DefaultServerHandlerImpl;
import com.xiaoming.software.rpc.server.handler.XiaoMingRpcServerHandler;


/**
 * rpc server
 * @author xiaoming
 */
public class XiaoMingRpcServer {
	private int port = 18088;
	
	private XiaoMingRpcServerHandler handler = null;
	
	public XiaoMingRpcServer(int port, XiaoMingRpcServerHandler handler){
		this.port = port;
		this.handler = handler;
	}
	/**
	 * To start server
	 */
	public void startServer(){
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			System.out.println("Started xiaoming-software rpc server in port:" + this.port + "");
			while(true){
				Socket socket = server.accept();
				
				//开启新线程去执行请求
				ExecutorUtil.executor( getTask( socket));
			}
		} catch (IOException e) {
			if(server != null){
				try {
					server.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	private Runnable getTask(Socket socket){
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					InputStream in = socket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					while(true){
		                String line = br.readLine();
		                if (line != null) {
		                	//调用业务handler
							handler.handle(line);
		                }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(socket != null){
						//关闭socket
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		};
		return task;
	}
	
	public static void main(String[] args) {
		DefaultServerHandlerImpl handler = new DefaultServerHandlerImpl();
		XiaoMingRpcServer  server = new XiaoMingRpcServer(8080, handler);
		server.startServer();
	}
	
}

/**
 * 线程池配置
 * @author xiaoming
 * 2017年4月20日
 */
class ExecutorUtil {  
  
    /** 最小线程数 */  
    private static int corePoolSize = 8;
    /** 最大线程数 */  
    private static int maxPoolSize = 16;
    /** 等待处理队列长度 */  
    private static int queueCapacity = 32;  
    /** 空闲时间 */
    private static int keepAliveSeconds = 300;  
    
    private static Executor executor = null;
    
    static{
    	if(executor == null){
    		executor = asyncConfig();
    	}
    }
    
	private static Executor asyncConfig() {
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(queueCapacity);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, taskQueue, new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				r.run();
			}

		});
		
		return executor;
	} 
	/**
	 * 异步执行
	 * @param task
	 * @author xiaoming
	 */
	public static void executor(Runnable task){
		executor.execute(task);
	}
}  
