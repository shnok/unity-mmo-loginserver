package com.shnok.javaserver.config;

import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Sources({
        "file:./conf/server.properties",
        "classpath:conf/server.properties"
})
@LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface ServerConfig extends Mutable, Reloadable {
    // Connection
    @Key("loginserver.host")
    String loginserverHost();
    @Key("loginserver.port")
    Integer loginserverPort();
    @Key("gameserver.host")
    String gameserverHost();
    @Key("gameserver.port")
    Integer gameserverPort();
    @Key("server.connection.timeout.ms")
    Integer serverConnectionTimeoutMs();
    @Key("accept.new.gameserver")
    Boolean acceptNewGameserver();
    @Key("flood.protection.enabled")
    Boolean floodProtectionEnabled();
    @Key("fast.connection.limit")
    Integer fastConnectionLimit();
    @Key("normal.connection.time")
    Integer normalConnectionTime();
    @Key("fast.connection.time")
    Integer fastConnectionTime();
    @Key("max.connection.per.ip")
    Integer maxConnectionPerIp();
    @Key("server.account.autocreate")
    Boolean autoCreateAccount();
    @Key("server.account.autocreate.access.level")
    Integer autoCreateAccountAccessLevel();
    @Key("server.account.inactive.access.level")
    Integer accountInactiveLevel();
    @Key("server.show.license")
    Boolean showLicense();
}
