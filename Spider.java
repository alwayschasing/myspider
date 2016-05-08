package webspider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
/**
 * Entrance of a crawler.
 * A spider contains three modules:Downloader,Scheduler,PageProcessor,PipeStore
 * Every module is a field of Spider.
 * @author jack
 *
 */
public class Spider implements Runnable {
	protected Downloader downloader;              
	protected PageProcessor pageProcessor;     
	protected List<Request> startRequest;
	protected Scheduler scheduler = new QueueScheduler();              //manage the url processing
	protected PipeLine pipeline;              //store the data in files
	protected List<PipeLine> pipelines = new ArrayList<PipeLine>();
	protected MyThreadPool threadPool;
	protected int threadNum = 3;
	protected AtomicInteger stat = new AtomicInteger(STAT_INIT);
	protected final static int STAT_INIT=0;
	protected final static int STAT_RUNNING=1;
	protected final static int STAT_STOPED=2;
	protected boolean exitWhenComplete = true;
	protected boolean destroyWhenExit = true;
    private ReentrantLock newUrlLock = new ReentrantLock();
    private Condition newUrlCondition = newUrlLock.newCondition();
    private int emptySleepTime = 30000;
	
	protected void initSpider(){
		threadPool = new MyThreadPool(threadNum);
	    downloader = new Downloader();
	    pageProcessor = new PageProcessor();
	    pipeline = new PipeLine();
	}
	@Override
	public void run(){
		checkRunningStat();
		initSpider();
		while(!Thread.currentThread().isInterrupted()&&stat.get()==STAT_RUNNING){
			Request request=scheduler.poll();         //choose one request
			if(request==null){
				if(threadPool.getThreadAlive() == 0 && exitWhenComplete){
					break;
				}
				waitNewUrl();
			}else{
				final Request constrequest = request;
				threadPool.execute(new Runnable(){
					@Override
					public void run(){
						try{
						   processRequest(constrequest);
						}catch(Exception e){
							
						}finally{
							
						}
					}
				});
			}
			stat.set(STAT_STOPED);
			if(destroyWhenExit){
				close();                         //clear some thing in the end
			}
		}
	}
	private void checkRunningStat(){
		while(true){
			int statNow=stat.get();
			if(statNow == STAT_RUNNING){
				throw new IllegalStateException("spider is already running");
			}
			if(stat.compareAndSet(statNow, STAT_RUNNING)){
				break;
			}
		}
	}
	protected void processRequest(Request request){	
		Page page = downloader.download(request);
		if(page == null){
			return;
		}
		pageProcessor.process(page);
		extractAndAddRequests(page);
		if( !page.getResultItems().isSkip()){
			for (PipeLine pipeline : pepelines){
				
			}
		}
	}
	public void close(){
		
	}
	private void waitNewUrl(){
		newUrlLock.lock();
		try{
			//double check
			if(threadPool.getThreadAlive() == 0 && exitWhenComplete){
				return;
			}
			newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
		}catch(InterruptedException e){
			
		}finally{
			newUrlLock.unlock();
		}
	}
	protected void extractAndAddRequests(Page page){
		if(page.getTargetRequest() != null){
			for (Request request : page.getTargetRequest()){
				addRequest(request);
			}
		}
	}
	private void addRequest(Request request){
		scheduler.push(request);
	}
}
