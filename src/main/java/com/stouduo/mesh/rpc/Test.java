package com.stouduo.mesh.rpc;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

public class Test {
    private String interface_;
    private String parameter;
    private String parameterTypesString;
    private String method;

    public String getParameter() {
        return parameter;
    }

    public static void main(String[] args) {
        Mono<Integer> result = WebClient.create().post()
                .uri("http://127.0.0.1:20000")
                .syncBody(fromFormData("interface_", "v1").with("method", "v2")
                        .with("parameterTypesString", "v2").with("parameter", "v2"))
                .retrieve()
                .bodyToMono(Integer.class);
        System.out.println(result.block());
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getInterface_() {
        return interface_;
    }

    public void setInterface_(String interface_) {
        this.interface_ = interface_;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
