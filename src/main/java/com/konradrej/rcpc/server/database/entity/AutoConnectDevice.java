package com.konradrej.rcpc.server.database.entity;

import javax.persistence.*;

/**
 * Entity for holding a saved AutoConnect device.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "AutoConnectDevice")
public class AutoConnectDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "uuid")
    private String uuid;

    /**
     * Empty constructor.
     */
    public AutoConnectDevice() {

    }

    /**
     * Constructor with parameters.
     *
     * @param uuid device uuid
     */
    public AutoConnectDevice(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get uuid.
     *
     * @return uuid value
     */
    public String getUuid() {
        return uuid;
    }
}
