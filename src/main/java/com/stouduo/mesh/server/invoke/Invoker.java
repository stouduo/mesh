package com.stouduo.mesh.server.invoke;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

public interface Invoker {
    DeferredResult<ResponseEntity<String>> invoke(HttpServletRequest request);
}
