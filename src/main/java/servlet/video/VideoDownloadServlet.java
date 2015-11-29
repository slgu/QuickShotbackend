package servlet.video;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import util.AwsUtil;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by slgu1 on 11/29/15.
 */
public class VideoDownloadServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        S3Object object = AwsUtil.downloadS3(uid);
        if (object == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                object.getObjectContent()));
        Writer writer = new OutputStreamWriter(resp.getOutputStream());
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            writer.write(line + "\n");
        }
        writer.flush();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        S3Object object = AwsUtil.downloadS3(uid);
        if (object == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        resp.setContentType(object.getObjectMetadata().getContentType());
        resp.setContentLength((int) object.getObjectMetadata().getContentLength());
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", object.getKey());
        resp.setHeader(headerKey, headerValue);
        S3ObjectInputStream inStream = object.getObjectContent();
        // obtains response's output stream
        OutputStream outStream = resp.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close();
    }
}