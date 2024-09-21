package com.friendly.commons.cache;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.instance.impl.HazelcastInstanceFactory;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Component
public class HazelcastCacheFactory {
    private static final Logger log = LoggerFactory.getLogger(HazelcastCacheFactory.class);
    private HazelcastInstance hazelcastInstance;
    UUID listenerId;

    @Value("${server.path}")
    private String serverPath;

    @PostConstruct
    public void init() {
        try {
            hazelcastInstance = initAcsHazel();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            initLocalHazel();
        }
    }

    private HazelcastInstance createHazelcastInstance(String fileName) throws IOException {
        ClientConfig config = new XmlClientConfigBuilder(fileName).build();
        config.setClassLoader(this.getClass().getClassLoader());
        return HazelcastClient.newHazelcastClient(config);
    }

    public <K, V> IMap<K, V> createCache(String name) {
        return hazelcastInstance.getMap("oneIotWebBackend/" + name);
    }

    public <K, V> IMap<K, V> getCache(String name) {
        return hazelcastInstance.getMap("customer1/" + name);
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    private void initLocalHazel() {
        log.warn("!!! No hazelcast instance detected => running built-in hazelcast. Only local cache available !!!");
        final Config configLocal = new Config();
        final UserCodeDeploymentConfig distCLConfig = configLocal.getUserCodeDeploymentConfig();
        distCLConfig.setEnabled(true)
                .setClassCacheMode(UserCodeDeploymentConfig.ClassCacheMode.ETERNAL)
                .setProviderMode(UserCodeDeploymentConfig.ProviderMode.LOCAL_CLASSES_ONLY);

        MapConfig mapConfig = configLocal.getMapConfig("default");
        mapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        NetworkConfig network = configLocal.getNetworkConfig();
        network.setPort(5600);

        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(true);
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        hazelcastInstance = HazelcastInstanceFactory.newHazelcastInstance(configLocal);
    }

    private HazelcastInstance initAcsHazel() throws IOException {
        HazelcastInstance hazelcastInstance = createHazelcastInstance(serverPath + "hazelcast-client-oneiot-back.xml");
        listenerId = hazelcastInstance.getLifecycleService().addLifecycleListener(lifecycleEvent -> {
            if (lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.SHUTDOWN)) {
                startAcsHazelCheck();
            } else if (lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.CLIENT_DISCONNECTED)) {
                initLocalHazel();
                CpeParameterNameCache.initCaches();
            }
        });
        return hazelcastInstance;
    }

    private void startAcsHazelCheck() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(10000);
                    try {
                        HazelcastInstance hazel = initAcsHazel();
                        try {
                            hazelcastInstance.shutdown();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        hazelcastInstance = hazel;
                        CpeParameterNameCache.initCaches();
                        log.info("ACS hazelcast initialized");
                        break;
                    } catch (Exception e) {
                        log.warn("ACS cache unavailable");
                    }
                }
            } catch (InterruptedException e) {
                log.error("INTERRUPTED");
            }
        }).start();
    }

}
