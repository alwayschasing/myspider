package webspider;
/**
 * Scheduler is the part of url management.
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 * @author jack
 *
 */
public interface Scheduler {
	/**
	 * add a url to fetch
	 * @param request
	 */
	public void push(Request request);
	/**
	 * get an url to crawl
	 */
	public Request poll();
}
