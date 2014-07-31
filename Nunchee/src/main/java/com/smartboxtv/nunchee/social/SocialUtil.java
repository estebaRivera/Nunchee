package com.smartboxtv.nunchee.social;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.TwitterHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Franklin Cruz on 20-03-14.
 */
public final class SocialUtil {

    AQuery aq;
    Context context;

    public SocialUtil(Context context) {
        this.context = context;
        aq = new AQuery(context);
    }

    public void fbshare(Activity activity, String text, String imageUrl,String url, String title, String description, final SocialUtilHandler handler) {

        Map<String, String> fbparams = new HashMap<String, String>();
        fbparams.put("message",text);
        fbparams.put("picture", imageUrl);
        fbparams.put("link", url);
        fbparams.put("description", description);
        fbparams.put("name", title);
        fbparams.put("caption", "Nunchee");

        FacebookHandle handle = new FacebookHandle(activity, "252503694880159", "publish_stream,email");

        aq.auth(handle).ajax("https://graph.facebook.com/me/feed", fbparams, JSONObject.class, new AjaxCallback<JSONObject>(){

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(status.getCode() == 200) {
                    handler.done(null);
                }
                else {
                    handler.done(new Exception(status.getMessage()));
                }

            }
        });

    }

    public void tweet(Activity activity, String tweet, final SocialUtilHandler handler) {

        if(tweet.length() > 140) {
            tweet = tweet.substring(0,140);
        }

        TwitterHandle handle = new TwitterHandle(activity, "ORUGhRg8O2ieZdWUnY98A", "XLuLuGHHVdYz0LkXYRiXybBVPTAUcDvnCxCH1cATE");

        String url = "https://api.twitter.com/1.1/statuses/update.json";

        Map<String,String> params = new HashMap<String, String>();
        params.put("status", tweet);

        aq.auth(handle).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    Log.d("Twitter", object.toString());

                    if(status.getCode() == 200) {
                        handler.done(null);
                    }
                    else {
                        handler.done(new Exception(status.getMessage()));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Twitter", e.toString());
                }

            }
        });
    }

    public abstract static class SocialUtilHandler {

        public void done(Exception e) {

        }

    }

}
