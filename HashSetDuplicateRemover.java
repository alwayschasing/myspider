package webspider;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HashSetDuplicateRemover implements DuplicateRemover{
	private Set<String> urls = new HashSet<String>();
	@Override
	public boolean isDuplicate(Request request){
		return !urls.add(request.getUrl());
	}
	@Override
	public void resetDuplicateCheck(){
		urls.clear();
	}
	@Override
	public int getTotalRequestsCount(){
		return urls.size();
	}
}
