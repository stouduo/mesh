package com.stouduo.mesh;

import com.stouduo.mesh.server.AgentServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgentMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentMeshApplication.class, args);
    }

    @Bean
    public ApplicationRunner serverStart() {
        return new AgentServer();
    }
}
