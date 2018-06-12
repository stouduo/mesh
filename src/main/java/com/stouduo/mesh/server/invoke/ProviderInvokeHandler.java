package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.server.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

public class ProviderInvokeHandler implements Invoker {
    @Autowired
    private AgentClient agentClient;

    public DeferredResult<ResponseEntity<String>> invoke(HttpServletRequest request) {
        DeferredResult<ResponseEntity<String>> response = new DeferredResult<>();
        agentClient.invoke(request).whenComplete((data, throwable) -> {
            if (throwable == null)
                response.setResult(new ResponseEntity<>(data, HttpStatus.OK));
            else response.setResult(new ResponseEntity<>(throwable.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        });
        return response;
    }
}
