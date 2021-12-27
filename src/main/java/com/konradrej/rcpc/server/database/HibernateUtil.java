package com.konradrej.rcpc.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Supporting class for handling h2/hibernate database.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.1
 * @since 1.0
 */
public class HibernateUtil {
    private static final Logger LOGGER = LogManager.getLogger(HibernateUtil.class);

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    /**
     * Initializes sessionFactory.
     *
     * @since 1.0
     */
    public static void initialize() {
        try {
            if (registry == null) {
                registry = new StandardServiceRegistryBuilder().configure().build();
            }

            if (sessionFactory == null && registry != null) {
                MetadataSources metadataSources = new MetadataSources(registry);
                Metadata metadata = metadataSources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
            }
        } catch (Exception e) {
            LOGGER.error("Could not initialize hibernate SessionFactory. Error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns a session factory instance.
     *
     * @return a configured session factory instance
     * @since 1.0
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            initialize();

        return sessionFactory;
    }

    /**
     * Shutdowns database.
     *
     * @since 1.0
     */
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
