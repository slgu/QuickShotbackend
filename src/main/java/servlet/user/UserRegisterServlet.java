package servlet.user;

import com.google.gson.Gson;
import config.Config;
import db.DbCon;
import db.User;
import util.AwsUtil;
import util.HttpUtil;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedSet;

/**
 * Created by slgu1 on 11/5/15.
 */

@MultipartConfig
public class UserRegisterServlet  extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String passwd = req.getParameter("passwd");
        String code = req.getParameter("verifycode");
        String sex_str = req.getParameter("sex");
        String age_str = req.getParameter("age");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        System.out.println(email);
        System.out.println(username);
        System.out.println(passwd);
        System.out.println(code);
        System.out.println(sex_str);
        System.out.println(age_str);
        System.out.println(name);
        if (email == null || username == null
                || passwd == null || code == null || sex_str == null
                || age_str == null || name == null || address == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        int sex = 0, age = 0;
        try {
            sex = Integer.parseInt(sex_str);
            age = Integer.parseInt(age_str);
            if (sex != 0 && sex != 1) {
                HttpUtil.writeResp(resp, 4);
                return;
            }
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 4);
            return;
        }
        /* check memcache verify */
        String val = (String)DbCon.memclient.get(email);
        System.out.println(val);
        System.out.println(code);
        if (val == null || !val.equals(code)) {
            HttpUtil.writeResp(resp, 2);
            return;
        }

        /* check img part */
        Part imgPart = req.getPart("img");
        if (imgPart == null || !imgPart.getContentType().equals("image/png")) {
            HttpUtil.writeResp(resp, 5);
            return;
        }

        //store img in s3 and return an uid
        String img_uid = AwsUtil.uploadS3(imgPart, "png");


        if (img_uid == null) {
            HttpUtil.writeResp(resp, 6);
            return;
        }

        /* add */
        User user = new User();
        user.setEmail(email);
        /* encrypt */
        user.setName(username);
        user.setPasswd(Util.encrypt(passwd));
        user.setSex(sex);
        user.setImg_uid(Config.S3_IMG_URL + img_uid);
        user.setAge(age);
        user.setNickname(name);
        user.setAddress(address);
        if (!user.insert()) {
            HttpUtil.writeResp(resp, 3);
        }
        else {
            HttpUtil.writeResp(resp, 0);
        }
    }
}