package servlet.topic;
import config.Config;
import db.DbCon;
import db.Topic;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.ByteBuf;
import org.bson.Document;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.common.util.CancellableThreads;
import org.elasticsearch.index.mapper.ParseContext;
import util.AwsUtil;
import util.HttpUtil;
import util.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.nio.file.StandardOpenOption.READ;

/**
 * Created by slgu1 on 11/7/15.
 */

@WebServlet("/topic/create")
@MultipartConfig
public class TopicCreateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user_id = HttpUtil.checkLogin(req);
        System.out.println(user_id);
        if (user_id == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String lat = req.getParameter("lat");
        String lon = req.getParameter("lon");
        System.out.println(title);
        System.out.println(desc);
        System.out.println(lat);
        System.out.println(lon);
        if (title == null || desc == null || lat == null || lon == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        if (!Util.checkFloat(lat) || !Util.checkFloat(lon)) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        Part filePart = req.getPart("file"); // Retrieves <input type="file" name="file">
        if (!filePart.getContentType().equals("video/mp4")) {
            HttpUtil.writeResp(resp, 4);
            return;
        }
        InputStream fileContent = filePart.getInputStream();
        /* get video and store */
        //store video in gridfs and return an uid
        String video_uid = AwsUtil.uploadS3(filePart);
        if (video_uid == null) {
            HttpUtil.writeResp(resp, 5);
            return;
        }
        Topic topic = new Topic();
        topic.setLat(lat);
        topic.setLon(lon);
        topic.setTitle(title);
        topic.setDesc(desc);
        topic.setVideo_uid(Config.S3_VIDEO_URL + video_uid);
        topic.setUser_uid(user_id);
        //store topic
        HashMap <String, String> mp = new HashMap<String, String>();
        if (topic.insert()) {
            //store into user list
            try {
                DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                        new Document("uid", user_id),
                        new Document("$push", new Document("topics_list", topic.getUid()))
                );
            }
            catch (Exception e) {
                HttpUtil.writeResp(resp, 7);
                return;
            }
            //send to sqs for worker to process
            AwsUtil.sendTopicToSQS(topic);
            mp.put("uid", topic.getUid());
            mp.put("status", "0");
        }
        else {
            HttpUtil.writeResp(resp, 6);
        }
    }
}