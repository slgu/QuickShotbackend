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

import static java.util.Arrays.binarySearch;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname = "";
    private String passwd = "";
    private String [] friends_list = new String[]{};
    private String [] topics_list = new String[]{};

    public String[] getLikes_list() {
        return likes_list;
    }

    public void setLikes_list(String[] likes_list) {
        this.likes_list = likes_list;
    }

    private String [] likes_list = new String[]{};
    private int sex = 0;
    private int age = 0;

    public int getFriendTag() {
        return friendTag;
    }

    public void setFriendTag(int friendTag) {
        this.friendTag = friendTag;
    }

    private int friendTag = 0;//not in database

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token = "";

    public String[] getTodo_list() {
        return todo_list;
    }

    public void setTodo_list(String[] todo_list) {
        this.todo_list = todo_list;
    }

    private String [] todo_list = new String []{};//relation deal list

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    private String address = "";
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
        if (res.iterator().hasNext()) {
            Document tmp = res.iterator().next();
            setUid((String)tmp.get("uid"));
            return true;
        }
        else
            return false;
    }

    public static User find(String uid) {
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                new Document().append("uid", uid)
        );
        if (res.iterator().hasNext()) {
            Document tmp = res.iterator().next();
            User user = new User();
            user.setAddress((String) tmp.get("address"));
            user.setAge((Integer) tmp.get("age"));
            user.setEmail((String) tmp.get("email"));
            user.setNickname((String)tmp.get("nickname"));
            String [] tmp_arr = new String[] {};
            user.setFriends_list(((List<String>) tmp.get("friends_list")).toArray(tmp_arr));
            user.setTopics_list(((List<String>) tmp.get("topics_list")).toArray(tmp_arr));
            //TODO spj needed to delete
            if (tmp.get("likes_list") != null)
                user.setLikes_list(((List<String>)tmp.get("likes_list")).toArray(tmp_arr));
            else
                user.setLikes_list(new String[]{});
            user.setName((String) tmp.get("name"));
            user.setUid(uid);
            return user;
        }
        else
            return null;
    }

    public static List <User> puzzyFind(String username) {
        SearchResponse res = DbCon.esclient.prepareSearch("cloud").setTypes("users").setQuery(
                fuzzyQuery("name", username)
        ).execute().actionGet();
        LinkedList <User> queryres = new LinkedList<User>();
        for (SearchHit hit:res.getHits().getHits()) {
            String uid = (String)hit.getSource().get("uid");
            User user = find(uid);
            if (user == null)
                continue;
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
                .append("likes_list", asList(likes_list))
                .append("topics_list", asList(topics_list))
                .append("todo_list", asList(todo_list))
                .append("age", age)
                .append("sex", sex)
                .append("nickname", nickname)
                .append("address", address);
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
        mp.put("uid", uid);
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
        /*
        User user = new User();
        user.setName("slgu2");
        user.setEmail("sg3302@columbia.edu");
        System.out.println(user.insert());
        */
    }
}