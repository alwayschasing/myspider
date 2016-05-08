package webspider;

import java.util.List;

public class PageProcessor {
	public void process(Page page){
		List<String> requests = page.selectUrls();
		page.addTargetRequests(requests);
	}
}
