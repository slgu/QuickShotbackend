package db;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import config.Config;
import net.spy.memcached.*;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.compat.SyncThread;
import net.spy.memcached.transcoders.SerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import static org.elasticsearch.index.query.QueryBuilders.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/5/15.
 */
public class DbCon {
    public static MongoClient mongoclient = null;
    public static MongoDatabase mongodb = null;
    public static MemcachedClient memclient = null;
    public static Client esclient = null;
    public static GridFS gfsPhoto = null;
    static {
        try {
            mongoclient = new MongoClient(new ServerAddress(InetAddress.getByName(Config.MongoIp), Config.MongoPort));
            mongodb = mongoclient.getDatabase(Config.MongoDb);
            memclient = new MemcachedClient(AddrUtil.getAddresses(Config.MemCacheEP));
            esclient = TransportClient.builder().build()
                            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Config.EsIp), 9300));
            gfsPhoto = new GridFS(mongoclient.getDB(Config.MongoDb));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String [] args) throws IOException{
        while (true) {

        }
        /*
        File imageFile = new File("/Users/slgu1/Desktop/test.mp4");
        GridFS gfsPhoto = new GridFS(mongoclient.getDB(Config.MongoDb));
        GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);
        gfsFile.setFilename("test.mp4");
        gfsFile.save();
        String [] names = {
                "slgu nash",
                "fuck this game",
                "oh mg",
                "sgu nas",
                "nash",
                "like"
        };
        for (int i = 0; i < names.length; ++i) {
            HashMap <String, String> mp = new HashMap<String, String>();
            mp.put("name", names[i]);
            IndexResponse res = esclient.prepareIndex("cloud", "users").setSource(new Gson().toJson(mp)).get();
            System.out.println(res.getId());
        }
        HashMap <String, String> mp = new HashMap<String, String>();
        mp.put("name", "slgu");
        SearchResponse res = esclient.prepareSearch("cloud").setTypes("users").setQuery(
                fuzzyQuery("name", "sgu")
        ).execute().actionGet();
        System.out.println(res);
        */
    }
}