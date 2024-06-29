package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBGameServer;
import com.shnok.javaserver.db.interfaces.GameServerDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class GameServerRepository implements GameServerDao {
    private static GameServerRepository instance;
    public static GameServerRepository getInstance() {
        if (instance == null) {
            instance = new GameServerRepository();
        }
        return instance;
    }

    @Override
    public List<DBGameServer> getAllGameServers() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBGameServer i", DBGameServer.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void addGameServer(DBGameServer gameServer) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(gameServer);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }
    }
}
