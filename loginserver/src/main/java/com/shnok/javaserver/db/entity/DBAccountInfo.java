package com.shnok.javaserver.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "AccountInfo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBAccountInfo {
    @Id
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String passHash;
    @Column(name = "accessLevel")
    private int accessLevel;
    @Column(name = "lastServer")
    private int lastServer;
    @Column(name = "lastIp")
    private String lastIp;
    @Column(name = "lastactive")
    private Long lastActive;
}
