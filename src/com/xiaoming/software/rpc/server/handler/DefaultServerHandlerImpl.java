/*
 * Copyright (c) 2017 xiaoming software Technology Co., Ltd.
 * All Rights Reserved.
 */
package com.xiaoming.software.rpc.server.handler;

public class DefaultServerHandlerImpl implements XiaoMingRpcServerHandler {

	@Override
	public void handle(String msg) {
		System.out.println("收到="+msg);
	}

}
