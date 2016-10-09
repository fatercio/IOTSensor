package core.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.global.Globals;
import core.models.JSONResponse;
import core.models.feed;

/**
 * Created by Diogo on 13/07/2016.
 */
public class ServicesControl {

    public class SensorFeedAPI
    {
    private final String UrlAPIClientePost = "https://api.thingspeak.com/talkbacks/10253/commands.json";
    private final String UrlAPICliente = " https://thingspeak.com/channels/156889/feed.json";




        public SensorFeedAPI()
        {

        }

        public JSONResponse Get() throws IOException, JSONException {

            Gson gson = new Gson();

            //Get JSON String
            String mJsonString = JSONReader.readJsonStringFromUrl(this.UrlAPICliente);

            //Parse JSON String to Object
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            JSONResponse JSONResponse = gson.fromJson(mJson, JSONResponse.class);

            //Get Feeds
            JSONResponse.feeds = this.GetFeeds(gson,mJsonString);

            //Last Feed
            if(JSONResponse.channel != null)
              JSONResponse.lastFeed = this.GetLastFeed(JSONResponse.channel.last_entry_id,JSONResponse.feeds);

            return JSONResponse;
        }

        public JSONResponse Post(String command) throws IOException, JSONException {

            Gson gson = new Gson();

            //Get JSON String
            String mJsonString = JSONReader.readJsonStringFromUrlPost(command, this.UrlAPIClientePost);


            //Parse JSON String to Object
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            JSONResponse JSONResponse = gson.fromJson(mJson, JSONResponse.class);
            return JSONResponse;
        }

        public feed[] GetFeeds(Gson gson, String JSONText) throws IOException, JSONException {
            JSONObject obj = JSONReader.convertFromJSONObject(JSONText);
            feed[] feeds = gson.fromJson(obj.getString("feeds"), feed[].class);
            return feeds;
        }

        public feed GetLastFeed(String last_entry_id,feed[] feeds)
        {
             if(feeds != null)
            {
                int feedLength =  feeds.length ;
                for(int i=0; i< feedLength; i++) {
                    if (last_entry_id.compareTo(feeds[i].entry_id) == 0 && feeds[i].field1!=null)
                     return feeds[i];
                }
           }
          return null;
        }
    }


}