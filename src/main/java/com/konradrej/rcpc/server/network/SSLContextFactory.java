package com.konradrej.rcpc.server.network;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * A class to retrieve a preconfigured instance of SSLContext.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.2
 * @since 1.0
 */
public class SSLContextFactory {
    private static final ClassLoader classLoader = SSLContextFactory.class.getClassLoader();
    private static Properties properties = null;

    /**
     * Configures a SSLContext according to ssl.properties resource settings.
     *
     * @return a configured SSLContext instance or null if an error occurred
     * @throws IOException if ssl.properties resource is missing
     * @since 1.1
     */
    public static SSLContext getPreconfigured() throws IOException {
        if (properties == null) {
            properties = new Properties();
            properties.load(SSLContextFactory.class.getClassLoader().getResource("ssl.properties").openStream());
        }

        return getConfiguredInstance(properties.getProperty("keystore.filename"), properties.getProperty("keystore.password"), properties.getProperty("truststore.filename"), properties.getProperty("truststore.password"));
    }

    /**
     * Given keystore and truststore information returns a configured SSLContext.
     *
     * @param keyStoreFilename   keystore filename to get from resources folder
     * @param keyStorePassword   keystore password
     * @param trustStoreFilename truststore filename to get from resources folder
     * @param trustStorePassword truststore password
     * @return a configured SSLContext instance, a default SSLContext instance if keystore/truststore error or null if SSLContext creation error
     * @since 1.0
     */
    public static SSLContext getConfiguredInstance(String keyStoreFilename, String keyStorePassword, String trustStoreFilename, String trustStorePassword) {
        KeyManager[] keyStoreManagers = getKeyManagers(keyStoreFilename, keyStorePassword);
        TrustManager[] trustStoreManagers = getTrustManagers(trustStoreFilename, trustStorePassword);

        try {
            if (keyStoreManagers == null || trustStoreManagers == null) {
                return SSLContext.getDefault();
            } else {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
                sslContext.init(keyStoreManagers, trustStoreManagers, null);

                return sslContext;
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return null;
        }
    }

    private static KeyManager[] getKeyManagers(String keyStoreFilename, String keyStorePassword) {
        if (keyStoreFilename != null && keyStorePassword != null) {
            try {
                char[] passwordArr = keyStorePassword.toCharArray();
                String keyStoreType = keyStoreFilename.substring(keyStoreFilename.lastIndexOf(".") + 1);

                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(classLoader.getResourceAsStream(keyStoreFilename), passwordArr);

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, passwordArr);

                return keyManagerFactory.getKeyManagers();
            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ignored) {
            }
        }

        return null;
    }

    private static TrustManager[] getTrustManagers(String trustStoreFilename, String trustStorePassword) {
        if (trustStoreFilename != null && trustStorePassword != null) {
            try {
                char[] passwordArr = trustStorePassword.toCharArray();
                String trustStoreType = trustStoreFilename.substring(trustStoreFilename.lastIndexOf(".") + 1);

                KeyStore trustStore = KeyStore.getInstance(trustStoreType);
                trustStore.load(classLoader.getResourceAsStream(trustStoreFilename), passwordArr);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                return trustManagerFactory.getTrustManagers();
            } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ignored) {
            }
        }

        return null;
    }
}
