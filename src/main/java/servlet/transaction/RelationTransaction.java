package servlet.transaction;

/**
 * Created by slgu1 on 11/12/15.
 */
public class RelationTransaction {
    //a backgroud thread to check if a transaction needs to be dealt.
    // two-phase commit
    static {
        Thread t = new Thread(){
            @Override
            public void run() {

            }
        };
        t.start();
    }
    public static void main() {
    }
}