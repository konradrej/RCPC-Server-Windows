package network;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLContextFactory {
    private static final ClassLoader classLoader = SSLContextFactory.class.getClassLoader();

    public static SSLContext getConfiguredInstance(String keyStoreFilename, String keyStorePassword, String trustStoreFilename, String trustStorePassword) {
        KeyManager[] keyStoreManagers = getKeyManagers(keyStoreFilename, keyStorePassword);
        TrustManager[] trustStoreManagers = getTrustManagers(trustStoreFilename, trustStorePassword);

        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(keyStoreManagers, trustStoreManagers, null);

            return sslContext;
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
                String trustStoreType = trustStoreFilename.substring(trustStoreFilename.lastIndexOf("-") + 1);

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
