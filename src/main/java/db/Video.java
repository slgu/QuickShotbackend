package db;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import config.Config;
import util.Util;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by slgu1 on 11/7/15.
 */
public class Video {
    private InputStream stream;
    public Video(InputStream stream) {
        this.stream = stream;
    }
    public String store() throws Exception{
        GridFSInputFile gfsFile = DbCon.gfsPhoto.createFile(stream);
        String uid = Util.uuid();
        try {
            gfsFile.setFilename(uid + ".mp4");
            gfsFile.save();
        }
        catch (Exception e) {
            throw new Exception("gfs store error");
        }
        return uid;
    }
}