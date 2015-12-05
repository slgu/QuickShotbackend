package worker;

import config.Config;
import db.Topic;
import util.AwsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by slgu1 on 11/21/15.
 */

public class WorkThreadPool {
    protected BlockingDeque <Topic> queue = new LinkedBlockingDeque<Topic>();
    class SQSPoller implements Runnable{
        public void run() {
            while (true) {
                List <Topic> res = AwsUtil.getTopicFromSQS();
                queue.addAll(res);
                try {
                    Thread.sleep(300);
                }
                catch (InterruptedException e) {
                }
            }
        }
    }
    class Worker implements Runnable {
        public void run() {
            while (true) {
                Topic topic = null;
                try {
                    topic = queue.take();
                }
                catch (InterruptedException e) {
                    continue;
                }
                //process topic
                System.out.println("deal with topic");
                final String title = topic.getTitle();
                final String desc = topic.getDesc();
                try {
                    HttpUtil.post(Config.VECTOR_URL, new HashMap<String, Object>() {{
                        put("text",
                                title + " " + desc);
                    }});
                }
                catch (Exception e) {
                   //maybe not needed to deal with this exception
                }
                //TODO store in db or elastic search
            }
        }
    }
    public void work() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Runnable poller = new SQSPoller();
        executor.execute(poller);
        for (int i = 0; i < 10; i++) {
            Worker worker = new Worker();
            //set rds
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("done");
    }
    public static void main(String [] args) {
        WorkThreadPool pool = new WorkThreadPool();
        pool.work();
    }
}