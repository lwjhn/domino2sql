package com.lwjhn.domino;

import lotus.domino.Database;
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
	
	public View addView(View view) throws NotesException{
		if(view==null) return null;
		View tview=views.get(this.viewkey=this.getKey(view));
		if(tview==null){
			views.put(this.viewkey, view);
			this.view_pool_size++;
			this.add(view.getParent());
		}
		return view;
	}
	
	public ViewCollection addViews(Collection<View> views) throws NotesException{
		for(View view : views){
			addView(view);
		}
		return this;
	}
	
	public View getView(String server,String filepath,String viewname) throws NotesException{
		View view=views.get(this.viewkey=this.getKey(server, filepath, viewname));
		if(view==null){
			Database db=this.getDatabase(server, filepath);
			if(db==null) return null;
			view=db.getView(viewname);
			if(view == null){
				if(debug) System.out.println(String.format("can't find view %s.  %s %s",viewname,server,filepath));
				return null;
			}else if(views.size()>this.view_pool_size){
				Entry<String, View> entry=views.entrySet().iterator().next();
				views.remove(entry.getKey());
				BaseUtils.recycle(entry.getValue());
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

	public void recycle(String server,String filepath,String viewName) throws Exception{
		View view=views.get(this.viewkey=this.getKey(server, filepath, viewName));
		if(view!=null){
			views.remove(this.viewkey);
		}
		BaseUtils.recycle(view);
	}
	
	public void recycle(View view) throws Exception{
		recycle(view.getParent().getServer(),view.getParent().getFilePath(),view.getName());
		BaseUtils.recycle(view);
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
