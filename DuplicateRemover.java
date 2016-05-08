package webspider;

/**
 * Remove duplicated requests.
 * @author jack
 *
 */
public interface DuplicateRemover {
	/**
	 * Check whether the request is duplicate.
	 */
	public boolean isDuplicate(Request request);
	/**
	 * Reset duplicate check
	 */
	public void resetDuplicateCheck();
	/**
	 * Get TotalRequestsCount for monitor
	 */
	public int getTotalRequestsCount();
}
