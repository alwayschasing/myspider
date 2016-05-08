package webspider;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.IOUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {

	public Page download(Request request){
		int statusCode=0;
		String charset = null;          //the char encoding way
		Set<Integer> acceptStatCode = null;
		Map<String,String> headers = null;
		CloseableHttpResponse httpResponse = null;
		try{
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpUriRequest httpUriRequest = getHttpUriRequest(request,headers);
			httpResponse = httpClient.execute(httpUriRequest);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusAccept(acceptStatCode,statusCode)){
				Page page=handleResponse(httpResponse,charset,request);
				return page;
			}
			else return null;
		}catch(IOException e){
			return null;
		}finally{
			try{
				if(httpResponse != null){
					EntityUtils.consume(httpResponse.getEntity());
				}
			}catch (IOException e){
				
			}
		}
	}
	protected HttpUriRequest getHttpUriRequest(Request request,Map<String,String> headers){
		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
		if(headers != null){
			for(Map.Entry<String, String> headerEntry : headers.entrySet()){
				requestBuilder.addHeader(headerEntry.getKey(),headerEntry.getValue());
			}
		}
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(5000)
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.setCookieSpec(CookieSpecs.DEFAULT);
		requestBuilder.setConfig(requestConfigBuilder.build());
		return requestBuilder.build();
	}
	private RequestBuilder selectRequestMethod(Request request){
		String method = request.getMethod();
		if(method == null||method.equalsIgnoreCase((String)"GET")){
			return RequestBuilder.get();
		}else if(method.equalsIgnoreCase((String)"POST")){
			
		}else if(method.equalsIgnoreCase((String)"HEAD")){
			
		}else if(method.equalsIgnoreCase((String)"PUT")){
			
		}else if(method.equalsIgnoreCase((String)"DELETE")){
			
		}else if(method.equalsIgnoreCase((String)"TRACE")){
			
		}
		throw new IllegalArgumentException("Illegal HTTP Method"+method);
	}
	private boolean statusAccept(Set<Integer> acceptStatCode, int statusCode){
		return acceptStatCode.contains(statusCode);
	}
	protected Page handleResponse(HttpResponse httpResponse,String charset,Request request) throws IOException{
		String content = getContent(charset,httpResponse);
		Page page = new Page();
		page.setRawText(content);
		page.setUrl(request.getUrl());
		page.setRequest(request);
		page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		return page;
	}
	protected String getContent(String charset, HttpResponse httpResponse) throws IOException{
		if (charset == null) {
			byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
			String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
			if(htmlCharset != null){
				return new String(contentBytes, htmlCharset);
			}else{
				return new String(contentBytes);
			}
		}else{
			return IOUtils.toString(httpResponse.getEntity().getContent(),charset);
		}
	}
	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes ) throws IOException{
		String charset;
		//1,charset in http header Content-Type
		String value = httpResponse.getEntity().getContentType().getValue();
		charset = getCharset(value);
		if(charset != null){
			return charset;
		}
		Charset defaultcharset = Charset.defaultCharset();
		String content = new String(contentBytes,defaultcharset.name());
		// 2,charset in meta
		if(content != null){
			Document document = Jsoup.parse(content);
			Elements links = document.select("meta");
			for(Element link : links){
				//2.1  html4  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				String metaContent = link.attr("content");
				String metaCharset = link.attr("charset");
				if(metaContent.indexOf("charset")!= -1){
					metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
					charset = metaContent.split("=")[1];
					break;
				}
				//2.2 html5 <meta charset="UTF-8"/>	
				else if (metaCharset != null){
					charset = metaCharset;
					break;
				}
			}
		}
		return charset;
	}
	protected String getCharset(String s){
		Pattern pattern = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");
		Matcher matcher = pattern.matcher(s);
		if(matcher.find()){
			String charset = matcher.group(1);
			if(Charset.isSupported(charset)){
				return charset;
			}
		}
		return null;
	}
	
}
