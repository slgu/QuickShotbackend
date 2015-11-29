import java.util.HashMap;
import db.User;
import util.Util;

/**
 * Created by slgu1 on 11/29/15.
 */
public class TestUser {
    public static void addUser(HashMap <String, Object> mp) {
        User user = new User();
        user.setAddress((String)mp.get("address"));
        user.setAge((Integer) mp.get("age"));
        user.setEmail((String) mp.get("email"));
        user.setSex((Integer) mp.get("sex"));
        user.setName((String) mp.get("name"));
        System.out.println(user.insert());
    }
    public static void main(String [] args) {
        /*
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "kanghu@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "kanghu");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "kanzhj@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "kanzhj");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "blackhero98@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "shenlonggu");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "haha@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "kanzhu");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "kana12@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "liangjin");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "song98@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "songgao");
        }});
        */
    }
}
