package servlet.video;

import com.mongodb.gridfs.GridFSDBFile;
import config.Config;
import db.DbCon;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.READ;

/**
 * Created by slgu1 on 11/8/15.
 */
public class VideoShowServlet extends HttpServlet{
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");
    private static final int BUFFER_LENGTH = 1024 * 16;
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String range = req.getHeader("range");
        String filename = req.getParameter("video_name");
        Matcher matcher = RANGE_PATTERN.matcher(range);
        List <GridFSDBFile> fileList =  DbCon.gfsPhoto.find(filename);
        if (fileList.size() == 0) {
            System.out.println("fuck");
            resp.getWriter().write("FUCK");
            return;
        }
        GridFSDBFile videoFile = fileList.get(0);
        int len = (int)videoFile.getLength();
        int start = 0;
        int end = len - 1;
        if (matcher.matches()) {
            String startGroup = matcher.group("start");
            start = startGroup.isEmpty() ? start : Integer.valueOf(startGroup);
            start = start < 0 ? 0 : start;
            String endGroup = matcher.group("end");
            end = endGroup.isEmpty() ? end : Integer.valueOf(endGroup);
            end = end > len - 1 ? len - 1 : end;
        }
        int contentLength = end - start + 1;
        resp.reset();
        resp.setBufferSize(BUFFER_LENGTH);
        resp.setHeader("Content-Disposition", String.format("inline;filename=\"%s\"", "test.mp4"));
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setDateHeader("Last-Modified", videoFile.getUploadDate().getTime());
        resp.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_TIME);
        resp.setContentType("video/mp4");
        resp.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, len));
        resp.setHeader("Content-Length", String.format("%s", contentLength));
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        int bytesRead;
        int bytesLeft = contentLength;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        InputStream inputStream = videoFile.getInputStream();
        inputStream.skip(start);
        OutputStream output = resp.getOutputStream();
        int byteLeft = contentLength;
        int byteRead;
        while ((byteRead = inputStream.read(buffer.array()))!= -1 && byteLeft > 0) {
            buffer.clear();
            output.write(buffer.array(), 0, byteLeft < byteRead ? byteLeft : byteRead);
            byteLeft -= byteRead;
        }
    }
}