/**
 * Created by slgu1 on 12/22/15.
 */
import java.net.URL;
public class TestUrl {
    public static void main(String [] args) {
        try {
            URL url = new URL("http://s3.amazonaws.com/slgucloud/object-8f8d9e0a-eb67-4ec3-bd93-a64508380433.png");
            byte [] buffer = new byte[4000];
            System.out.println(url.openConnection().getInputStream().read(buffer));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}