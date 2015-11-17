package servlet.transaction;

import com.mongodb.BasicDBObject;
import config.Config;
import db.DbCon;
import org.bson.Document;

import javax.print.Doc;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by slgu1 on 11/12/15.
 */
public class RelationTransaction {
    //a backgroud thread to check if a transaction needs to be dealt.
    // two-phase commit
    public static BlockingQueue <Integer> messageQueue = new LinkedBlockingDeque<Integer>();
    public static int waitTime = 200;
    static {
        Thread execute_thread = new Thread(){
            @Override
            public void run() {
                while (true) {

                    //just check a notification
                    try {
                        messageQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                    }
                    catch (Exception e) {
                    }

                    //get a init transaction to execute
                    Document result_doc = null;
                    try {
                        DbCon.mongodb.getCollection(Config.TransactionConnection)
                                .findOneAndUpdate(
                                        new Document("state", "initial"),
                                        new Document("$currentDate", new Document("lastModified", true))
                                                .append("$set", new Document("state", "pending"))
                                );
                    }
                    catch (Exception e) {
                        continue;
                    }
                    if (result_doc == null)
                        continue;

                    //apply change to two user-document

                    try {
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("src_uid")),
                                new Document("$push", new Document("pendingTransactions", result_doc.get("uid")))
                                        .append("$push", new Document("friends_list", result_doc.get("dest_uid")))
                        );
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("dest_uid")),
                                new Document("$push", new Document("pendingTransactions", result_doc.get("uid")))
                                        .append("$push", new Document("friends_list", result_doc.get("src_uid")))
                        );
                    }
                    catch (Exception e) {
                        continue;
                    }

                    // update next
                    //if all right set state: applied
                    try {
                        DbCon.mongodb.getCollection(Config.TransactionConnection).updateOne(
                                new Document("uid", result_doc.get("uid")),
                                new Document("$currentDate", new Document("lastModified", true))
                                        .append("$set", new Document("state", "applied"))
                        );
                    }
                    catch (Exception e) {
                        continue;
                    }

                    //remove transaction id from user_document
                    // and set done
                    try {
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("src_uid"))
                                    .append("pendingTransactions", result_doc.get("uid")),
                                new Document("$pull", new Document("pendingTransactions", result_doc.get("uid")))
                        );
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("dest_uid"))
                                        .append("pendingTransactions", result_doc.get("uid")),
                                new Document("$pull", new Document("pendingTransactions", result_doc.get("uid")))
                        );
                        DbCon.mongodb.getCollection(Config.TransactionConnection).updateOne(
                                new Document("uid", result_doc.get("uid")),
                                new Document("$currentDate", new Document("lastModified", true))
                                        .append("$set", new Document("state", "done"))
                        );
                    }
                    catch (Exception e) {

                    }

                    //set done

                }
            }
        };
        execute_thread.start();

        //check error roll back or finish
        Thread check_thread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5 * 60 * 1000);
                    }
                    catch (InterruptedException e) {

                    }
                    Date dateAfterThirtyMinute = new Date(new Date().getTime() - 60 * 1000 * 30);
                    Document result_doc = null;
                    try {
                        result_doc = DbCon.mongodb.getCollection(Config.TransactionConnection)
                                .findOneAndUpdate(
                                        new Document("state", "pending")
                                        .append("lastModified", new Document("$lt", dateAfterThirtyMinute)),
                                        new Document()
                                );
                    }
                    catch (Exception e) {
                        continue;
                    }
                    if (result_doc == null)
                        continue;


                    //undo
                    try {
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("src_uid"))
                                        .append("pendingTransactions", result_doc.get("uid")),
                                new Document("$pull", new Document("pendingTransactions", result_doc.get("uid")))
                                        .append("$pull", new Document("friends_list", result_doc.get("dest_uid")))
                        );
                        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                                new Document("uid", result_doc.get("dest_uid"))
                                        .append("pendingTransactions", result_doc.get("uid")),
                                new Document("$pull", new Document("pendingTransactions", result_doc.get("uid")))
                                        .append("$pull", new Document("friends_list", result_doc.get("src_uid")))
                        );
                        DbCon.mongodb.getCollection(Config.TransactionConnection).updateOne(
                                new Document("uid", result_doc.get("uid")),
                                new Document("$currentDate", new Document("lastModified", true))
                                        .append("$set", new Document("state", "cancelled"))
                        );
                    }
                    catch (Exception e) {

                    }
                }
            }
        };
        check_thread.start();
    }
    public static void main() {

    }
}