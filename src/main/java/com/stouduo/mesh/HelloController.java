package com.stouduo.mesh;

import com.stouduo.mesh.rpc.Test;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

    @RequestMapping(value = "/test/", method = RequestMethod.POST)
    public Mono<String> hello(@RequestBody Test test) {
        System.out.println(test.toString());
        return Mono.justOrEmpty("hello ! it works.");
    }
}
