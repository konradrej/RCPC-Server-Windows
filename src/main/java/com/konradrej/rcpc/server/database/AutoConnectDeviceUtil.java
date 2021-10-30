package com.konradrej.rcpc.server.database;

import com.konradrej.rcpc.server.database.entity.AutoConnectDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Support class for AutoConnectDevice entities.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class AutoConnectDeviceUtil {
    private static final Logger LOGGER = LogManager.getLogger(AutoConnectDeviceUtil.class);

    /**
     * Saves an instance of AutoConnectDevice in database.
     *
     * @param autoConnectDevice AutoConnectDevice to save
     * @return true if successful, false on error
     */
    public static boolean saveDevice(AutoConnectDevice autoConnectDevice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.save(autoConnectDevice);

            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Could not save device. Error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether device with given uuid exists in database.
     *
     * @param uuid uuid to check
     * @return true if exists, false if not or error
     */
    public static boolean containsDevice(String uuid) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AutoConnectDevice> query = session.createQuery("FROM AutoConnectDevice A WHERE A.uuid = :uuid", AutoConnectDevice.class);
            query.setParameter("uuid", uuid);

            List<AutoConnectDevice> autoConnectDevices = query.list();

            if (!autoConnectDevices.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Could not get saved devices. Error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Removes all devices saved in database.
     *
     * @return true if successful, false on error
     */
    public static boolean clearDevices() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query query = session.createQuery("DELETE FROM AutoConnectDevice");
            query.executeUpdate();

            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Could not clear saved devices. Error: " + e.getMessage());
        }

        return false;
    }
}
