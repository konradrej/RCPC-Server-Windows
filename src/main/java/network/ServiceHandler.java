package network;

import com.kstruct.gethostname4j.Hostname;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Class responsible for handling DNS-SD using the JmDNS library, used
 * to control registering of the offered service.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 */
public class ServiceHandler {
    private static final Logger LOGGER = LogManager.getLogger(ServiceHandler.class);

    private static final String SERVICE_TYPE = "_rcpc._tcp.local.";
    private static final Thread onShutdown = new Thread(ServiceHandler::stop);
    private static JmDNS jmDNS = null;

    /**
     * Registers DNS-SD RCPC service.
     */
    public static void start() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, "RCPC Host", 7598, Hostname.getHostname());

            jmDNS = JmDNS.create(address);
            jmDNS.registerService(serviceInfo);

            LOGGER.info("DNS-SD service registered");

            Runtime.getRuntime().addShutdownHook(onShutdown);
        } catch (IOException e) {
            LOGGER.error("DNS-SD service could not be registered\n" + e.fillInStackTrace());

            stop();
        }
    }

    /**
     * Unregisters DNS-SD RCPC service.
     */
    public static void stop() {
        if (jmDNS != null) {
            jmDNS.unregisterAllServices();

            LOGGER.info("DNS-SD service unregistered");

            Runtime.getRuntime().removeShutdownHook(onShutdown);
        }
    }

    private ServiceHandler() {

    }
}
