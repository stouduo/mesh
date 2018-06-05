package com.stouduo.mesh.util;

import java.io.Serializable;
import java.util.Objects;

public class Endpoint implements Serializable {
    private final String host;
    private final int port;
    private final int capacity;
    private int currentCapacity;

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
        this.capacity = 1;
        this.currentCapacity = 0;
    }

    public Endpoint(String host, int port, int capacity) {
        this.host = host;
        this.port = port;
        this.capacity = capacity;
        this.currentCapacity = 0;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port + "-" + capacity;
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
