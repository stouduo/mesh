package com.stouduo.mesh_.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcConfigs {
    private static ConcurrentMap<String, Object> configs = new ConcurrentHashMap<>();

    /**
     * 系统cpu核数
     */
    public static final String SYSTEM_CPU_CORES = "system.cpu.cores";

    /**
     * 默认服务提供者启动器
     */
    public static final String DEFAULT_PROVIDER_BOOTSTRAP = "default.provider.bootstrap";
    /**
     * 默认服务端调用者启动器
     */
    public static final String DEFAULT_CONSUMER_BOOTSTRAP = "default.consumer.bootstrap";
    /**
     * 默认注册中心
     */
    public static final String DEFAULT_REGISTRY = "default.registry";
    /**
     * 默认协议
     */
    public static final String DEFAULT_PROTOCOL = "default.protocol";
    /**
     * 默认序列化
     */
    public static final String DEFAULT_SERIALIZATION = "default.serialization";
    /**
     * 默认字符集 utf-8
     */
    public static final String DEFAULT_CHARSET = "default.charset";
    /**
     * 注册中心发现服务（保存注册中心地址的服务）的地址
     */
    public static final String REGISTRY_ADDRESS = "registry.address";
    /**
     * 注册中心心跳发送间隔
     */
    public static final String REGISTRY_HEARTBEAT_PERIOD = "registry.heartbeat.period";
    /**
     * 默认绑定网卡
     */
    public static final String SERVER_HOST = "server.host";
    /**
     * 默认启动端口，包括不配置或者随机，都从此端口开始计算
     */
    public static final String SERVER_PORT = "server.port";
    /**
     * 默认发布路径
     */
    public static final String SERVER_ROOT_PATH = "server.root.path";
    /**
     * 默认io线程大小，推荐自动设置
     */
    public static final String SERVER_IOTHREADS = "server.ioThreads";
    /**
     * 默认服务端业务线程池类型
     */
    public static final String SERVER_POOL_TYPE = "server.pool.type";
    /**
     * 默认服务端业务线程池最小
     */
    public static final String SERVER_POOL_CORE = "server.pool.core";
    /**
     * 默认服务端业务线程池最大
     */
    public static final String SERVER_POOL_MAX = "server.pool.max";
    /**
     * 默认服务端业务线程池队列类型
     */
    public static final String SERVER_POOL_QUEUE_TYPE = "server.pool.queue.type";
    /**
     * 默认服务端业务线程池队列
     */
    public static final String SERVER_POOL_QUEUE = "server.pool.queue";
    /**
     * 默认服务端业务线程池回收时间
     */
    public static final String SERVER_POOL_ALIVETIME = "server.pool.aliveTime";

    /**
     * 接口下每方法的最大可并行执行请求数
     */
    public static final String PROVIDER_CONCURRENTS = "provider.concurrents";

    /**
     * 默认负载均衡算法
     */
    public static final String CONSUMER_LOAD_BALANCER = "consumer.loadBalancer";

    /**
     * 默认回调线程池最小
     */
    public static final String ASYNC_POOL_CORE = "async.pool.core";
    /**
     * 默认回调线程池最大
     */
    public static final String ASYNC_POOL_MAX = "async.pool.max";
    /**
     * 默认回调线程池队列
     */
    public static final String ASYNC_POOL_QUEUE = "async.pool.queue";
    /**
     * 默认回调线程池回收时间
     */
    public static final String ASYNC_POOL_TIME = "async.pool.time";
    /**
     * 客户端IO线程池
     */
    public static final String AGENT_CLIENT_IO_THREADS = "agent.client.io.threads";
    /**
     * 服务端boss线程数
     */
    public static final String AGENT_SERVER_BOSS_THREADS = "agent.server.boss.threads";
    /**
     * 服务端IO线程数
     */
    public static final String AGENT_SERVER_IO_THREADS = "agent.server.io.threads";
}
