package util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import config.Config;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by slgu1 on 11/16/15.
 */

public class AwsUtil {
    public static AmazonS3 s3 = new AmazonS3Client();
    /*return store key*/
    private static String makeName(String uid) {
        return "object-" + uid + ".mp4";
    }
    public static String uploadS3(Part filePart) {
        String key = makeName(Util.uuid());
        try {
            ObjectMetadata omd = new ObjectMetadata();
            omd.setContentType(filePart.getContentType());
            omd.setContentLength(filePart.getSize());
            omd.setHeader("filename", filePart.getName());
            s3.putObject(new PutObjectRequest(Config.videoBucketName, key , filePart.getInputStream(), omd));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            return null;
        }
        return key;
    }
    public static S3Object downloadS3(String key) {
        S3Object object =  s3.getObject(new GetObjectRequest(Config.videoBucketName, key));
        return object;
    }
}
