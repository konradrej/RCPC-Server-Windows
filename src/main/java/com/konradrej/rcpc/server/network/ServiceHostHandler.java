package com.konradrej.rcpc.server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for handling DNS-SD using the JmDNS library, used
 * to control registering of the offered service.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.2
 * @since 1.0
 */
public class ServiceHostHandler {
    private static final Logger LOGGER = LogManager.getLogger(ServiceHostHandler.class);

    private static final String SERVICE_TYPE = "_rcpc._tcp.local";
    private static final List<JmDNS> jmDNS = new ArrayList<>();

    /**
     * Registers DNS-SD RCPC service.
     *
     * @param port service port
     * @since 1.0
     */
    public static void start(int port) {
        new Thread(() -> {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName().replace(".mshome.net", ""));
                if (addresses != null) {
                    String serviceName;

                    for (InetAddress address : addresses) {
                        serviceName = "RCPC Host " + address.getCanonicalHostName();

                        ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, serviceName, port, "");

                        JmDNS dns = JmDNS.create(address);
                        dns.registerService(serviceInfo);

                        jmDNS.add(dns);

                        LOGGER.info("DNS-SD service registered. Service name: " + serviceName);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("DNS-SD service could not be registered.\n" + e.getLocalizedMessage());

                stop();
            }
        }).start();
    }

    /**
     * Unregisters DNS-SD RCPC service.
     *
     * @since 1.0
     */
    public static void stop() {
        for (JmDNS jmDNS : jmDNS) {
            jmDNS.unregisterAllServices();
            try {
                jmDNS.close();
            } catch (IOException ignored) {
            }
        }

        LOGGER.info("DNS-SD services unregistered.");
    }

    private ServiceHostHandler() {

    }
}
