package com.stouduo.mesh.util;

import java.util.Objects;

public class Endpoint {
    private final String host;
    private final int port;
    private final int capacity;

    public Endpoint(String host, int port, int capacity) {
        this.host = host;
        this.port = port;
        this.capacity = capacity;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", capacity=" + capacity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return port == endpoint.port &&
                capacity == endpoint.capacity &&
                Objects.equals(host, endpoint.host);
    }

    @Override
    public int hashCode() {

        return Objects.hash(host, port, capacity);
    }

    public int getCapacity() {

        return capacity;
    }

}
