package com.stouduo.mesh.server;

import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.invoke.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AgentServer {
    @Autowired
    private Invoker invoker;

    @RequestMapping("/")
    public DeferredResult<ResponseEntity<String>> invoke(HttpServletRequest request) {
        return invoker.invoke(request);

    }
}
