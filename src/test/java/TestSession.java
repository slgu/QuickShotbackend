import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 12/6/15.
 */
public class TestSession {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    public static void main (String [] args) {
        HashMap <String, String> mp = new HashMap<String, String>();
        mp.put("username", "sg");
        mp.put("passwd", "123");
    }
}
