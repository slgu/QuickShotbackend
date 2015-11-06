package db;
import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import config.Config;
import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import util.Util;

import javax.jws.soap.SOAPBinding;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import static org.elasticsearch.index.query.QueryBuilders.*;

import static java.util.Arrays.asList;
/**
 * Created by slgu1 on 11/4/15.
 */
public class User {
    private String uid = "";
    private String email = "";
    private String name = "";
    private String passwd = "";
    private String [] friends_list = new String[]{};
    private String [] topics_list = new String[]{};
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String[] getFriends_list() {
        return friends_list;
    }

    public void setFriends_list(String[] friends_list) {
        this.friends_list = friends_list;
    }

    public String[] getTopics_list() {
        return topics_list;
    }

    public void setTopics_list(String[] topics_list) {
        this.topics_list = topics_list;
    }

    public boolean checkDb() {
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                new Document().append("name", name)
                        .append("passwd", passwd)
        );
        if (res.iterator().hasNext())
            return true;
        else
            return false;
    }
    public static List <User> puzzyFind(String username) {
        /* TODO elastic search */
        SearchResponse res = DbCon.esclient.prepareSearch("cloud").setTypes("users").setQuery(
                fuzzyQuery("name", username)
        ).execute().actionGet();
        LinkedList <User> queryres = new LinkedList<User>();
        for (SearchHit hit:res.getHits().getHits()) {
            User user = new User();
            user.setUid((String)hit.getSource().get("uuid"));
            user.setName((String)hit.getSource().get("name"));
            queryres.add(user);
        }
        return queryres;
    }
    private boolean validate() {
        if (!email_pattern.matcher(email).matches())
            return false;
        if (!alpha_pattern.matcher(name).matches())
            return false;
        return true;
    }
    public boolean insert() {
        if (!validate())
            return false;
        uid = Util.uuid();
        Document doc = new Document();
        doc.append("uid", uid).append("email", email).append("name",name)
                .append("passwd",passwd).append("friends_list", asList(friends_list))
                .append("topics_list", asList(topics_list));
        try {
            DbCon.mongodb.getCollection(Config.UserConnection).insertOne(doc);
        }
        catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        /* insert into elastic search *
         */
        Map <String, String> mp = new HashMap<String, String>();
        mp.put("name", name);
        mp.put("uuid", uid);
        if (!DbCon.esclient.prepareIndex("cloud", "users").setSource(new Gson().toJson(mp)).get().isCreated()) {
            System.out.println("ES create error");
        }
        return true;
    }
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern email_pattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern alpha_pattern = Pattern.compile("^[_A-Za-z0-9]+$");
    public static void main(String [] args) {
        User user = new User();
        user.setName("slgu1");
        user.setEmail("sg3301@columbia.edu");
        System.out.println(user.insert());
    }
}