package webspider;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object contains url to crawl.
 * Also contains some additional information.
 * @author jack
 *
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 6442180050055990436L;

	private String url;
	
	private String method;
	/**
	 * Store additional information in extras.
	 */
	private Map<String,Object> extras;
	/**
	 * Priority of  the request.
	 * The bigger will be processed earlier.
	 * 
	 */
	private long priority;
	
	public Request(){
	}
	public Request(String url){
		this.url=url;
	}
	/**
	 * Set the priority of request for sorting.
	 * need a scheduler supporting priority.
	 * @param priority
	 * @return this
	 */
	public Request setPriority(long priority){
		this.priority=priority;
		return this;
	}
	public Object getExtra(String key){
		if(extras==null){
			return null;
		}
		return extras.get(key);
	}
	public Request putExtra(String key,Object value){
		if(extras==null){
			extras=new HashMap<String,Object>();
		}
		extras.put(key, value);
		return this;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
		this.url=url;
	}
	@Override
	public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Request request=(Request)o;
		if(!url.equals(request.url)) return false;
		return true;
	}
	public Map<String,Object> getExtras(){
		return extras;
	}
	public void setExtras(Map<String,Object> extras){
		this.extras=extras;
	}
	@Override
	public int hashCode(){
		return url.hashCode();
	}
	/**
	 * The http method of the request,get for default.
	 * @return heepMethod
	 */
	public String getMethod(){
		return method;
	}
	public void setMethod(String method){
		this.method=method;
	}
	@Override
	public String toString(){
		return "Request{"+
						"url='"+url+'\''+
						",method='"+method+'\''+
						",extras="+extras+
						",priority="+priority+
						'}';
	}
}
