package com.stouduo.mesh.server.netty.util;

import com.lmax.disruptor.dsl.Disruptor;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Component
public class DisruptorHolder {
    private Disruptor<RpcEvent> disruptor;
    @Autowired
    private Invoker invoker;

    @PostConstruct
    private void init() {
        disruptor = new Disruptor<>(RpcEvent::new, 1024, (ThreadFactory) Thread::new);
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> invoker.invoke(event.getRpcDTO()));
        disruptor.start();
    }

    public Disruptor<RpcEvent> getDisruptor() {
        return disruptor;
    }

    public static class RpcEvent {
        private RpcDTO rpcDTO;

        public RpcDTO getRpcDTO() {
            return rpcDTO;
        }

        public void setRpcDTO(RpcDTO rpcDTO) {
            this.rpcDTO = rpcDTO;
        }
    }
}
