package org.example.dayflower.memdinterop;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;

@Slf4j
public class PerlishKetamaNodeLocator implements NodeLocator {
    private List<MemcachedNode> allNodes;
    private TreeMap<Long, MemcachedNode> ketamaNodes;
    private int ketamaPoints;
    private String namespace;

    public PerlishKetamaNodeLocator(List<MemcachedNode> allNodes, int ketamaPoints, String namespace) {
        this.allNodes = allNodes;
        this.ketamaPoints = ketamaPoints;
        this.namespace = namespace;
        updateKetamaNodes();
    }

    private PerlishKetamaNodeLocator(List<MemcachedNode> allNodes, TreeMap<Long, MemcachedNode> ketamaNodes, int ketamaPoints, String namespace) {
        this.ketamaPoints = ketamaPoints;
        this.allNodes = allNodes;
        this.ketamaNodes = ketamaNodes;
        this.namespace = namespace;
        updateKetamaNodes();
    }

    @Override
    public MemcachedNode getPrimary(String k) {
        byte[] bytes = k.getBytes(StandardCharsets.UTF_8);

        if (this.namespace != null) {
            if (k.startsWith(this.namespace)) {
                bytes = k.substring(this.namespace.length()).getBytes(StandardCharsets.UTF_8);
            }
        }

        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        final long hash = crc32.getValue();
        final Map.Entry<Long, MemcachedNode> entry = ketamaNodes.ceilingEntry(hash);
        if (entry != null) {
            return entry.getValue();
        }
        return ketamaNodes.firstEntry().getValue();
    }

    @Override
    public Iterator<MemcachedNode> getSequence(String k) {
        return new Iterator<MemcachedNode>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public MemcachedNode next() {
                return null;
            }
        };
    }

    @Override
    public Collection<MemcachedNode> getAll() {
        return allNodes;
    }

    @Override
    public NodeLocator getReadonlyCopy() {
        return new PerlishKetamaNodeLocator(this.allNodes, this.ketamaNodes, this.ketamaPoints, this.namespace);
    }

    @Override
    public void updateLocator(List<MemcachedNode> allNodes) {
        this.allNodes = allNodes;
        updateKetamaNodes();
    }

    private static final byte[] HOST_PORT_DELIM = {0x00};

    private void updateKetamaNodes() {
        final TreeMap<Long, MemcachedNode> nodeMap = new TreeMap<>();

        for (MemcachedNode node : allNodes) {
            final CRC32 crc32 = new CRC32();

            InetSocketAddress address = (InetSocketAddress) node.getSocketAddress();

            final ByteBuffer indexBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);

            long point = 0;
            for (int i = 0; i < ketamaPoints; i++) {
                crc32.reset();

                crc32.update(address.getHostString().getBytes(StandardCharsets.UTF_8));
                crc32.update(HOST_PORT_DELIM);
                crc32.update(String.valueOf(address.getPort()).getBytes(StandardCharsets.UTF_8));

                indexBuffer.rewind();
                indexBuffer.putInt((int) point);
                indexBuffer.rewind();
                crc32.update(indexBuffer);

                point = crc32.getValue();

                log.debug(String.format("%d/%d: 0x%08x", i, ketamaPoints, point));
                nodeMap.put(point, node);
            }
        }

        ketamaNodes = nodeMap;
    }
}
