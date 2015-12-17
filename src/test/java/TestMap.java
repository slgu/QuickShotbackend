import com.mongodb.client.MapReduceIterable;
import db.DbCon;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by slgu1 on 12/17/15.
 */
public class TestMap {
    public static void main(String [] args) {
        ArrayList <Double> list = new ArrayList<Double>();
        list.add(3.4);
        list.add(4.4);
        list.add(3.1);
        list.add(2.9);
        System.out.println(list);
        String map ="function () {"+
                "var another = " + list.toString() +";"+
                "var res = 0;" +
                "var idx = 0;" +
                "for(idx = 0; idx < this.val.length; ++idx) {" +
                "res += (this.val[idx] - another[idx]) * (this.val[idx] - another[idx]);" +
                "}" +
                "res = Math.sqrt(res);" +
                "emit(-res, this.key);"+
                "}";
        String reduce = "function(key, values) {return values.toString();}";
        System.out.println(map);
        System.out.println(reduce);
        MapReduceIterable <Document> res = DbCon.mongodb.getCollection("vecs").mapReduce(map,
                reduce);
        Iterator <Document> itr = res.iterator();
        //top 10 return
        while (itr.hasNext()) {
            System.out.println(itr.next());
            //insert into priority queue
        }
    }
}