package com.stouduo.mesh;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class AgentMeshApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentMeshApplication.class, args);
    }
}
