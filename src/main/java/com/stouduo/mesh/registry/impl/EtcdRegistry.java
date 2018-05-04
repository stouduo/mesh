package com.stouduo.mesh.registry.impl;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch.Watcher;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.stouduo.mesh.registry.BaseRegistry;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.util.Endpoint;
import com.stouduo.mesh.util.IpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EtcdRegistry extends BaseRegistry implements IRegistry {
    private Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);

    private Lease lease;
    private KV kv;
    private long leaseId;
    private String registryKey;
    private final Long revsion = 10l;

    @Override
    public void register() {
        if (this.lease == null) {
            logger.info(">>>>>开始注册服务，服务类型为:" + serverType);
            Client client = Client.builder().endpoints(serverUrl).build();
            this.lease = client.getLeaseClient();
            this.kv = client.getKVClient();
            try {
                this.registryKey = MessageFormat.format("/{0}/{1}/{2}:{3}", rootPath, serverName, IpHelper.getHostIp(), serverPort);
                this.leaseId = this.lease.grant(30).get().getID();
                keepAlive();
                // 如果是provider，去etcd注册服务并监听
                if (isProvider()) {
                    register2Etcd();
                    watch(client);
                }
            } catch (Exception e) {
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
                        listener.listen();
                        logger.info("心跳:" + leaseId + "; lease:" + Long.toHexString(leaseId));
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
        ByteSequence bsFindKey = ByteSequence.fromString(registryKey);
        final Watcher watcher = client.getWatchClient().watch(
                bsFindKey, WatchOption.newBuilder().withRevision(revsion).withPrefix(bsFindKey).build()
        );
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        boolean keepListen = true;
                        while (keepListen) {
                            WatchResponse response = watcher.listen();
                            for (WatchEvent event : response.getEvents()) {
                                switch (event.getEventType()) {
                                    case PUT:
                                        providers.put(findKey, findServers(findKey));
                                        break;
                                    case DELETE:
                                        providers.put(findKey, findServers(findKey));
                                        keepListen = false;
                                        break;
                                    case UNRECOGNIZED:
                                        break;
                                }
                            }
                            if (watcher != null) watcher.close();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    } finally {
                        if (watcher != null) watcher.close();
                    }
                }
        );
    }


    private void register2Etcd() throws Exception {
        kv.put(ByteSequence.fromString(registryKey), ByteSequence.fromString(serverCapacity), PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        logger.info("注册一个新服务【" + registryKey + "】，容量为：" + serverCapacity);
    }

    private List<Endpoint> findServers(String findKey) throws Exception {
        ByteSequence key = ByteSequence.fromString(findKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        List<Endpoint> endpoints = new ArrayList<>();
        for (KeyValue kv : response.getKvs()) {
            String k = kv.getKey().toStringUtf8();
            String v = kv.getValue().toStringUtf8();
            String endpointStr = k.substring(k.lastIndexOf("/") + 1, k.length());

            String host = endpointStr.split(":")[0];
            int port = Integer.valueOf(endpointStr.split(":")[1]);

            endpoints.add(new Endpoint(host, port, Integer.parseInt(v)));
        }
        return endpoints;
    }

    @Override
    public List<Endpoint> find(String serviceName) throws Exception {
        String findKey = MessageFormat.format("/{0}/{1}", rootPath, StringUtils.isEmpty(serviceName) ? this.serverName : serviceName);
        List<Endpoint> endpoints = providers.get(findKey);
        if (endpoints != null) {
            if (endpoints.size() != 0) return endpoints;
            else providers.remove(findKey);
        }
        endpoints = findServers(findKey);
        providers.put(findKey, endpoints);
        return endpoints;
    }

    @Override
    public void serverDown() throws Exception {
        kv.delete(ByteSequence.fromString(registryKey)).get();
        logger.info("服务【" + registryKey + "】已下线！");
    }

    @Override
    public String toString() {
        return "EtcdRegistry{" +
                "lease=" + lease +
                ", kv=" + kv +
                ", leaseId=" + leaseId +
                ", registryKey='" + registryKey + '\'' +
                ", revsion=" + revsion +
                ", rootPath='" + rootPath + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", serverType='" + serverType + '\'' +
                ", serverPort=" + serverPort +
                ", serverName='" + serverName + '\'' +
                ", serverCapacity='" + serverCapacity + '\'' +
                '}';
    }
}
