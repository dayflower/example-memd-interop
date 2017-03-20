package org.example.dayflower.memdinterop;

import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;

import java.util.List;

public class PerlishKetamaConnectionFactory extends KetamaConnectionFactory {
    private final int ketamaPoints;
    private final String namespace;

    public PerlishKetamaConnectionFactory(int ketamaPoints, String namespace) {
        super();
        this.ketamaPoints = ketamaPoints;
        this.namespace = namespace;
    }

    @Override
    public NodeLocator createLocator(List<MemcachedNode> nodes) {
        return new PerlishKetamaNodeLocator(nodes, ketamaPoints, namespace);
    }
}
