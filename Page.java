package webspider;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	private Request request;
	private String rawText;
	private int statusCode;
	private String url;
	
	private List<Request> targetRequests = new ArrayList<Request>();
	
	public Page setRawText(String rawText){
		this.rawText = rawText;
		return this;
	}
	public String getRawText(){
		return this.rawText;
	}
	public Page setRequest(Request request){
		this.request = request;
		return this;
	}
	public Request getRequest(){
		return this.request;
	}
	public Page setStatusCode(int statusCode){
		this.statusCode = statusCode;
		return this;
	}
	public int getStatusCode(){
		return this.statusCode;
	}
	public Page setUrl(String url){
		this.url = url;
		return this;
	}
	public String getUrl(){
		return this.url;
	}
	public List<String> selectUrls(){
		List<String> urls = new ArrayList<String>();
		String urlPattern;
		if(this.rawText != null){
			Document document = Jsoup.parse(this.rawText);
			Elements links = document.select("a[href]");
			for(Element link : links){
				String url=link.attr("href");
				urls.add(url);
			}
		}
		return urls;
	}
	public void addTargetRequests(List<String> requests){
		synchronized(targetRequests){
			for(String s : requests){
				if(!s.isEmpty() || s.equals("#") || s.startsWith("javascript:") ){
					continue;
				}
				targetRequests.add(new Request(s));
			}
		}
	}
	public List<Request> getTargetRequest(){
		return targetRequests;
	}
	
}
