package com.stouduo.agentmesh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AgentMeshApplicationTests {


    @Test
    public void contextLoads() {
    }

    public static void main(String[] args) {
        int retry = 3;
        while (retry-- > 0) {
            try {
                test();
            } catch (Exception e) {
                if(retry==0)
                    System.out.println("xxx");
                else System.out.println(retry);
            }
        }
    }

    public static void test() throws Exception {
        throw new IndexOutOfBoundsException("xxx");
    }

}
