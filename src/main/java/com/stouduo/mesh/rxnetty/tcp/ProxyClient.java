/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.stouduo.mesh.rxnetty.tcp;

import com.stouduo.mesh.util.IpHelper;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;
import rx.Observable;

import java.nio.charset.Charset;

/**
 */
public final class ProxyClient {

    public static void main(String[] args) {

        /*
         * Retrieves the server address, using the following algorithm:
         * <ul>
             <li>If any arguments are passed, then use the first argument as the server port.</li>
             <li>If available, use the second argument as the server host, else default to localhost</li>
             <li>Otherwise, start the passed server class and use that address.</li>
         </ul>
         */

        HttpClient.newClient(IpHelper.getHostIp(), 20000)
                .enableWireLogging("proxy-client", LogLevel.DEBUG)
                .createPost("/")
                .writeStringContent(Observable.just("interface=com.al\n" +
                        "ibaba.dubbo.perf\n" +
                        "ormance.demo.pro\n" +
                        "vider.IHelloServ\n" +
                        "ice&method=hash&\n" +
                        "parameterTypesSt\n" +
                        "ring=Ljava%2Flan\n" +
                        "      g%2FString%3B&pa\n" +
                        "     rameter=6mcU5jG7\n" +
                        "C42BtdX3eLMnLzga\n" +
                        "DlOFgqKJRBa5GiE0\n" +
                        "  jKs4ZXjpz9Qg5WLw\n" +
                        " C4G6ncRGHR7j3MRB\n" +
                        "sapPVg33a3xkYfKK\n" +
                        "MOrGlk9ohdg9re6M\n" +
                        "   W2HzXPZ5iX7zUHTU\n" +
                        "KzNgs2wmHS9g6kMH\n" +
                        "9MYp4i3GNoQNERKk\n" +
                        "M47NcBg6"))
                .flatMap(resp -> resp.getContent()
                        .map(bb -> bb.toString(Charset.defaultCharset()))
                )
                .toBlocking()
                .forEach(System.out::println);
    }
}
