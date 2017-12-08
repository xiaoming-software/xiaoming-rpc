# xiaoming-rpc
XiaoMing java版RPC工具，高并发，大数据文本传输性能不错。

# 如何使用？

### 开启server
DefaultServerHandlerImpl handler = new DefaultServerHandlerImpl();
XiaoMingRpcServer server = new XiaoMingRpcServer(28888, handler);

### 创建client 并发送数据，随后socket会自动断开链接。想要再次发送就需要重新new client。
XiaoMingRpcClient client = new XiaoMingRpcClient("127.0.0.1", 28888);
client.send("Hello techer cang");
