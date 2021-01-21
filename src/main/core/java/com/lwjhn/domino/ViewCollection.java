package com.lwjhn.domino;

import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class ViewCollection extends DatabaseCollection {
	protected Map<String,View> views =new HashMap<String, View>();
	protected View view=null;
	protected String viewkey = null;	
	protected int view_pool_size=10;
	
	public ViewCollection(){
		super();
	}
	
	public ViewCollection(Session session){
		super(session);
	}

	public ViewCollection(Session session, int poolSize){
		super(session,poolSize);
		this.view_pool_size=poolSize<2 ? 1 : poolSize;
	}
	
	public ViewCollection(Session session, int viewPoolSize, int databasePoolSize){
		super(session,databasePoolSize);
		this.view_pool_size=viewPoolSize<2 ? 1 : viewPoolSize;
	}
	
	public Map<String,View> getViewCollection(){
		return this.views;
	}
	
	public View addView(View view,boolean recycleRepeat) throws NotesException{
		if(view==null) return null;
		this.view=views.get(this.viewkey=this.getKey(view));
		if(this.view==null){
			views.put(this.viewkey, view);
			this.view_pool_size++;
			this.add(view.getParent(), recycleRepeat);
			return view;
		}else{
			if(recycleRepeat) { try { view.recycle(); view = null; } catch (Exception e) { } }
			return this.view;
		}
	}
	
	public ViewCollection addViews(Collection<View> views,boolean recycleRepeat) throws NotesException{
		for(View view : views){
			addView(view,recycleRepeat);
		}
		return this;
	}
	
	public View getView(String server,String filepath,String viewname) throws NotesException{
		view=views.get(this.viewkey=this.getKey(server, filepath, viewname));
		if(view==null){
			this.db=this.getDatabase(server, filepath);
			if(this.db==null) return null;
			view=this.db.getView(viewname);
			if(view == null){
				if(debug) System.out.println(String.format("can't find view %s.  %s %s",viewname,server,filepath));
				return null;
			}else if(views.size()>this.view_pool_size){
				Entry<String, View> entry=views.entrySet().iterator().next();
				View view=null;
				try {
					if ((view=entry.getValue()) != null) { 
						view.recycle();
						view = null; 
					}
					views.remove(entry.getKey());
				} catch (Exception e) { }
			}
			views.put(this.viewkey, view);
		}
		
		return view;
	}
	
	public String getKey(String server,String filepath,String viewname){
		return (server.replaceAll("([^\\/]*=)","")+SEP+filepath.replaceAll("\\\\", "/")+SEP+viewname).toLowerCase();
	}
	
	public String getKey(View view) throws NotesException{
		return this.getKey(view.getParent().getServer(),view.getParent().getFilePath(),view.getName());
	}
	
	public void recycle(String server,String filepath,String viewname) throws Exception{
		this.db=dbs.get(this.viewkey=this.getKey(server, filepath));
		if(this.db!=null){
			if (this.db != null) { try { this.db.recycle(); this.db = null; } catch (Exception e) { } }
			dbs.remove(this.viewkey);
		}
		if (db != null) { try { db.recycle(); db = null; } catch (Exception e) { } }
	}
	
	public void recycle(View view) throws Exception{
		this.view=views.get(this.viewkey=this.getKey(view));
		if(this.view!=null){
			if (this.view != null) { try { this.view.recycle(); this.view = null; } catch (Exception e) { } }
			views.remove(this.viewkey);
		}
		if (view != null) { try { view.recycle(); view = null; } catch (Exception e) { } }
	}

	public void recycle() {
		BaseUtils.recycle(views);
		views.clear();
		views=null;
		super.recycle();
	}

	public int setViewPoolSize(int pool_size){
		return this.view_pool_size=pool_size<2 ? 1 : pool_size;
	}
	
	public int getViewPoolSize(){
		return this.view_pool_size;
	}
}
