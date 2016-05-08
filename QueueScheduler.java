package webspider;

import org.apache.http.annotation.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ThreadSafe
public class QueueScheduler implements Scheduler{
	
	private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
	
	private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();
	public void pushWhenNoDuplicated(Request request){
		queue.add(request);
	}
	public void push(Request request){
		if( !duplicatedRemover.isDuplicate(request) ){
			pushWhenNoDuplicated(request);
		}
	}
	public synchronized Request poll(){
		return queue.poll();
	}
}
