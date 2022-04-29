package com.lwjhn.domino;

import lotus.domino.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DatabaseCollection extends LotusBase {
    protected Map<String, Database> dbs = new HashMap<String, Database>();
    protected final String SEP = String.valueOf((char) 0);
    private String key = null;
    protected Session ss = null;
    protected int db_pool_size = 10;
    protected boolean debug = false;

    public DatabaseCollection() {

    }

    public DatabaseCollection(Session session) {
        ss = session;
    }

    public DatabaseCollection(Session session, int poolSize) {
        ss = session;
        this.db_pool_size = poolSize < 2 ? 1 : poolSize;
    }

    public Map<String, Database> getDatabaseCollection() {
        return this.dbs;
    }

    public Database add(Database database) throws NotesException {
        if (database == null || database.isOpen()) return null;
        Database db = dbs.get(this.key = this.getKey(database));
        if (db == null) {
            dbs.put(this.key, database);
            this.db_pool_size++;
        }
        return db;
    }

    public DatabaseCollection add(Collection<Database> dbc) throws NotesException {
        for (Database db : dbc) {
            add(db);
        }
        return this;
    }

    public Database getDatabase(String server, String filepath) throws NotesException {
        Database db = dbs.get(this.key = this.getKey(server, filepath));
        if (db == null || !db.isOpen()) {
            if (db != null) {
                dbs.remove(this.key);
                BaseUtils.recycle(db);
            }

            db = getSession().getDatabase(server, filepath);
            if (db == null || !db.isOpen()) {
                if (debug) System.out.println("can't open database . " + server + " " + filepath);
                return null;
            } else if (dbs.size() > this.db_pool_size) {
                Entry<String, Database> entry = dbs.entrySet().iterator().next();
                dbs.remove(entry.getKey());
                BaseUtils.recycle(entry.getValue());
            }
            dbs.put(this.key, db);
        }
        return db;
    }

    public Session getSession() throws NotesException {
        return ss == null ? (ss = NotesFactory.createSession()) : ss;
    }

    public String getKey(String server, String filepath) {
        return (server.replaceAll("([^\\/]*=)", "") + SEP + filepath.replaceAll("\\\\", "/")).toLowerCase();
    }

    public String getKey(Database db) throws NotesException {
        return this.getKey(db.getServer(), db.getFilePath());
    }

    public void recycle(String server, String filepath) {
        Database db = dbs.get(this.key = this.getKey(server, filepath));
        if (db != null) {
            dbs.remove(this.key);
            BaseUtils.recycle(db);
        }
    }

    public void recycle(Database db) throws Exception {
        recycle(db.getServer(), db.getFilePath());
        BaseUtils.recycle(db);
    }

    public void recycle() {
        BaseUtils.recycle(dbs);
        dbs.clear();
        dbs = null;
        ss = null;
    }

    public int setPoolSize(int pool_size) {
        return this.db_pool_size = pool_size < 2 ? 1 : pool_size;
    }

    public int getPoolSize() {
        return this.db_pool_size;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
