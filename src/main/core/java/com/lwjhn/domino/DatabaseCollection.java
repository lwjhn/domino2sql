package com.lwjhn.domino;

import lotus.domino.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class DatabaseCollection extends LotusBase {
	protected Map<String,Database> dbs =new HashMap<String, Database>();
	protected final String SEP = String.valueOf((char)0); 
	protected Database db =null;
	private String key = null;
	protected Session ss=null;
	protected int db_pool_size=10;
	protected boolean debug=false;
	
	public DatabaseCollection(){
		
	}
	
	public DatabaseCollection(Session session){
		ss=session;
	}

	public DatabaseCollection(Session session, int poolSize){
		ss=session;
		this.db_pool_size=poolSize<2 ? 1 : poolSize;
	}
	
	public Map<String,Database> getDatabaseCollection(){
		return this.dbs;
	}
	
	public Database add(Database database,boolean recycleRepeat) throws NotesException{
		if(database==null || database.isOpen()) return null;
		db=dbs.get(this.key=this.getKey(database));
		if(db==null){
			dbs.put(this.key, database);
			this.db_pool_size++;
			return database;
		}else{
			if(recycleRepeat) { try { database.recycle(); database = null; } catch (Exception e) { } }
			return db;
		}
	}
	
	public DatabaseCollection add(Collection<Database> dbc,boolean recycleRepeat) throws NotesException{
		for(Database db : dbc){
			add(db,recycleRepeat);
		}
		return this;
	}
	
	public Database getDatabase(String server,String filepath) throws NotesException{
		db=dbs.get(this.key=this.getKey(server,filepath));
		if(db==null || !db.isOpen()){
			if (db != null && !db.isOpen()) { try { db.recycle(); db = null; dbs.remove(this.key);} catch (Exception e) { } }
			
			db=getSession().getDatabase(server,filepath);
			if(db==null || !db.isOpen()){
				if(debug) System.out.println("can't open database . "+ server + " "+ filepath);
				return null;
			}else if(dbs.size()>this.db_pool_size){
				Entry<String, Database> entry=dbs.entrySet().iterator().next();
				Database tdb=null;
				try {
					if ((tdb=entry.getValue()) != null) { 
						tdb.recycle();
						tdb = null; 
					}
					dbs.remove(entry.getKey());
				} catch (Exception e) { }
			}
			dbs.put(this.key, db);
		}
		return db;
	}
	
	public Session getSession() throws NotesException{
		return ss==null ? (ss=NotesFactory.createSession()) : ss;
	}
	
	public String getKey(String server,String filepath){
		return (server.replaceAll("([^\\/]*=)","")+SEP+filepath.replaceAll("\\\\", "/")).toLowerCase();
	}
	
	public String getKey(Database db) throws NotesException{
		return this.getKey(db.getServer(),db.getFilePath());
	}
	
	public void recycle(String server,String filepath) {
		this.db=dbs.get(this.key=this.getKey(server, filepath));
		if(this.db!=null){
			if (this.db != null) { try { this.db.recycle(); this.db = null; } catch (Exception e) { } }
			dbs.remove(this.key);
		}
		if (db != null) { try { db.recycle(); db = null; } catch (Exception e) { } }
	}
	
	public void recycle(Database db) throws Exception {
		this.db=dbs.get(this.key=this.getKey(db));
		if(this.db!=null){
			if (this.db != null) { try { this.db.recycle(); this.db = null; } catch (Exception e) { } }
			dbs.remove(this.key);
		}
		if (db != null) { try { db.recycle(); db = null; } catch (Exception e) { } }
	}
	
	public void recycle() {
		BaseUtils.recycle(dbs);
		dbs.clear();
		dbs=null;
		ss=null;
	}

	public int setPoolSize(int pool_size){
		return this.db_pool_size=pool_size<2 ? 1 : pool_size;
	}
	
	public int getPoolSize(){
		return this.db_pool_size;
	}
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
