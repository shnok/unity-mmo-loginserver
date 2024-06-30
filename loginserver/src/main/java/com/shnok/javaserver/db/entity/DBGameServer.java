package com.shnok.javaserver.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GameServers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBGameServer {
    /**
     * CREATE TABLE `gameservers` (
     *   `server_id` int(11) NOT NULL DEFAULT '0',
     *   `hexid` varchar(50) NOT NULL DEFAULT '',
     *   `host` varchar(50) NOT NULL DEFAULT '',
     *   PRIMARY KEY (`server_id`)
     * );
     */

    @Id
    @Column(name = "server_id")
    private int serverId;
    @Column(name = "hexid")
    private String hexId;
    @Column(name = "host")
    private String host;
}