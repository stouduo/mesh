package com.stouduo.mesh.registry.impl;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch.Watcher;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.stouduo.mesh.registry.BaseRegistry;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.util.Endpoint;
import com.stouduo.mesh.util.IpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtcdRegistry extends BaseRegistry implements IRegistry {
    private Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);

    private Lease lease;
    private KV kv;
    private long leaseId;
    private String registryKey;
    private final Long revision = 1l;
    private AtomicBoolean serverDown = new AtomicBoolean(true);

    @PostConstruct
    private void init() {
        register();
    }

    @Override
    public void register() {
        if (this.lease == null) {
            logger.info(">>>>>开始注册服务，服务类型为：" + serverType);
            Client client = Client.builder().endpoints(serverUrl).build();
            this.lease = client.getLeaseClient();
            this.kv = client.getKVClient();
            try {
                this.leaseId = this.lease.grant(30).get().getID();
                this.serverDown.compareAndSet(true, false);
                keepAlive();
                // 如果是provider，去etcd注册服务
                if (isProvider()) {
                    this.registryKey = MessageFormat.format("/{0}/{1}/{2}:{3}", rootPath, serverName, IpHelper.getHostIp(), serverPort);
                    register2Etcd();
                } else {
                    //监听
                    watch(client);
                    findServers(rootPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
            logger.info(">>>>>服务注册完成！");
        }
    }

    // 发送心跳到ETCD,表明该host是活着的
    private void keepAlive() {
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    Lease.KeepAliveListener listener = null;
                    try {
                        listener = lease.keepAlive(leaseId);
                        while (!serverDown.get()) {
                            Thread.sleep(200);
                            listener.listen();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    } finally {
                        if (listener != null) listener.close();
                    }
                }
        );
    }

    private void watch(Client client) {
        final String findKey = MessageFormat.format("/{0}/{1}", rootPath, this.serverName);
        logger.info(">>>>>开始监听服务【" + findKey + "】");
        ByteSequence bsFindKey = ByteSequence.fromString(findKey);
        final Watcher watcher = client.getWatchClient().watch(bsFindKey, WatchOption.newBuilder().withRevision(revision).withPrefix(bsFindKey).build());
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        while (!serverDown.get()) {
                            for (WatchEvent event : watcher.listen().getEvents()) {
                                logger.info(">>>>>监听到事件【" + event.getEventType().toString() + "】");
                                switch (event.getEventType()) {
                                    case UNRECOGNIZED:
                                        break;
                                    default:
                                        findServers(findKey);
                                        break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
        );
    }


    private void register2Etcd() throws Exception {
        kv.put(ByteSequence.fromString(registryKey), ByteSequence.fromString(serverCapacity), PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        logger.info(kv.get(ByteSequence.fromString(registryKey)).get().getCount() != 0 ? (">>>>>注册一个新服务【" + registryKey + "】，容量为：" + serverCapacity) : ">>>>>未知原因，注册失败。");
    }

    private void findServers(String findKey) throws Exception {
        providers.clear();
        ByteSequence key = ByteSequence.fromString(findKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();
        response.getKvs().forEach(kv -> {
            String k = kv.getKey().toStringUtf8();
            String v = kv.getValue().toStringUtf8();
            int splitIndex = k.lastIndexOf("/");
            String serviceKey = k.substring(0, splitIndex);
            List<Endpoint> endpoints = providers.getOrDefault(serviceKey, new ArrayList<>());
            String[] endpointStr = k.substring(splitIndex + 1, k.length()).split(":");
            endpoints.add(new Endpoint(endpointStr[0], Integer.valueOf(endpointStr[1]), Integer.parseInt(v)));
            providers.put(serviceKey, endpoints);
        });
        logger.info(">>>>>服务列表为：" + providers.toString());
    }

    @Override
    public void serverDown(Endpoint endpoint) throws Exception {
        String serverRegKey = MessageFormat.format("/{0}/{1}/{2}:{3}", rootPath, serverName, endpoint.getHost(), endpoint.getPort() + "");
        kv.delete(ByteSequence.fromString(serverRegKey)).get();
        this.serverDown.compareAndSet(false, true);
        logger.info(">>>>>服务【" + serverRegKey + "】已下线！");
    }

    @Override
    public  List<Endpoint> find(String serviceName) throws Exception {
        String findKey = new StringBuilder("/").append(rootPath).append("/").append(serviceName).toString();
//        String findKey = MessageFormat.format("/{0}/{1}", rootPath, StringUtils.isEmpty(serviceName) ? this.serverName : serviceName);
        List<Endpoint> endpoints = providers.get(findKey);
        if (endpoints != null) {
            if (endpoints.size() != 0) return endpoints;
            else providers.remove(findKey);
        }
        return Collections.emptyList();
    }
}
