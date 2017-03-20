package org.example.dayflower.memdinterop;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        final MemcachedClient memcachedClient =
                new MemcachedClient(
                        new PerlishKetamaConnectionFactory(3, "prefix:"),
                        getServerHosts());

        try {
            showMemcachedValues(memcachedClient);
        } finally {
            memcachedClient.shutdown();
        }
    }

    private static void showMemcachedValues(MemcachedClient memcachedClient) {
        for (int i = 1; i <= 20; i++) {
            final String key = "prefix:" + String.format("key%d", i);

            final Object oldVal = memcachedClient.get(key);
            memcachedClient.set(key, 0, "java");
            final Object newVal = memcachedClient.get(key);

            log.info("{}: {} => {}", key, oldVal, newVal);
        }
    }

    private static List<InetSocketAddress> getServerHosts() {
        return Arrays.asList(
                new InetSocketAddress("127.0.0.1", 18401),
                new InetSocketAddress("127.0.0.1", 18402),
                new InetSocketAddress("127.0.0.1", 18403),
                new InetSocketAddress("127.0.0.1", 18404),
                new InetSocketAddress("127.0.0.1", 18405),
                new InetSocketAddress("127.0.0.1", 18406),
                new InetSocketAddress("127.0.0.1", 18407)
        );
    }
}
