# xiaoming-rpc
XiaoMing java版RPC工具，高并发，大数据文本传输性能不错。

# 如何使用？

### 开启server
#### 其中128是最大支持client个数，超出会抛出XiaoMingRpcException，服务停止。
```java
DefaultServerHandlerImpl handler = new DefaultServerHandlerImpl();
XiaoMingRpcServer server = new XiaoMingRpcServer(28888, handler, 128);
```

### 创建client 同时发送数据。
#### client是一个长连接，但不是线程安全的。在多线程高并发情况下不能使用同一个client。建议使用 client pool技术。
```java
XiaoMingRpcClient client = new XiaoMingRpcClient("127.0.0.1", 28888);
client.send("Hello techer cang");
client.send("Hello techer lmd");
client.close();
```

### client pool 多线程高并发情况下是线程安全的链接。
#### 其中2是链接个数
```java
XiaoMingRpcClientPool pool = new XiaoMingRpcClientPool("192.168.0.119", 8080, 2);
new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0;i<100;i++){
					XiaoMingRpcClient client = pool.getCleint();//从池中获取一个client
					client.send(line);
					pool.returnCleint(client);//归还client
					System.out.println(Thread.currentThread().getName() + "  " + i);
				}
			}
		}).start();
```
