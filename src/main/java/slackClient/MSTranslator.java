package slackClient;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
/*
 * Gson: https://github.com/google/gson
 * Maven info:
 *     groupId: com.google.code.gson
 *     artifactId: gson
 *     version: 2.8.1
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MSTranslator {

// **********************************************
// *** Update or verify the following values. ***
// **********************************************

// Replace the subscriptionKey string value with your valid subscription key.
    private static String subscriptionKey = "9449493227a3402fa8c5231a86a4d185";

    private static String host = "https://api.cognitive.microsofttranslator.com";
    private static String translatePath = "/translate?api-version=3.0";
    private static String dictLookupPath = "/dictionary/lookup?api-version=3.0";

    // Translate to English.
    private static String params = "&to=en&from=sk";

    private static class RequestBody {
        String Text;

        public RequestBody(String text) {
            this.Text = text;
        }
    }

    private static String post(URL url, String content) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", content.length() + "");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        connection.setRequestProperty("X-ClientTraceId", java.util.UUID.randomUUID().toString());
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        byte[] encoded_content = content.getBytes("UTF-8");
        wr.write(encoded_content, 0, encoded_content.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder ();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

   private static String translate(String inputString) throws Exception {
        URL url = new URL (host + translatePath + params);

        List<RequestBody> objList = new ArrayList<RequestBody>();
        objList.add(new RequestBody(inputString));
        String content = new Gson().toJson(objList);

        return post(url, content);
    }

    private static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    public static String callTranslate(String inputString) {
        try {
            String response = translate(inputString);
            //System.out.println(prettify (response));
            String response2 = response.substring(1, response.length()-1);
	        
            JsonParser jsonParser = new JsonParser();
            JsonElement text = jsonParser.parse(response2)
                .getAsJsonObject().getAsJsonArray("translations").get(0)
                .getAsJsonObject().get("text");
                
     
            return text.getAsString();  
        }
        catch (Exception e) {
            System.out.println (e);
        }
        
        return "";
    }
    
    public static String DictionaryLookup () throws Exception {
        URL url = new URL (host + dictLookupPath + params);

        List<RequestBody> objList = new ArrayList<RequestBody>();
        objList.add(new RequestBody("databáza"));
        String content = new Gson().toJson(objList);

        return post(url, content);
    }
    
    /*
    public static void main(String[] args) {
    	try {
            String response = DictionaryLookup ();
            System.out.println (prettify (response));
        }
        catch (Exception e) {
            System.out.println (e);
        }
    }
    */
}