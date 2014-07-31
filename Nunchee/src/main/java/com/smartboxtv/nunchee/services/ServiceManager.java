package com.smartboxtv.nunchee.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.smartboxtv.nunchee.data.models.Channel;
import com.smartboxtv.nunchee.data.models.FeedJSON;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.data.modelssm.CategorieChannelSM;
import com.smartboxtv.nunchee.data.modelssm.ChannelSM;
import com.smartboxtv.nunchee.data.modelssm.EpisodeSM;
import com.smartboxtv.nunchee.data.modelssm.Language;
import com.smartboxtv.nunchee.data.modelssm.ProgramSM;
import com.smartboxtv.nunchee.data.modelssm.ScheduleSM;
import com.smartboxtv.nunchee.data.modelssm.TweetSM;
import com.smartboxtv.nunchee.data.modelssm.datacategory.ChannelCategorySM;
import com.smartboxtv.nunchee.data.modelssm.datacategory.ProgramsCategorySM;
import com.smartboxtv.nunchee.data.modelssm.datafavorites.ChannelFavoriteSM;
import com.smartboxtv.nunchee.data.modelssm.datafavorites.ProgramFavoriteSM;
import com.smartboxtv.nunchee.data.modelssm.datafavorites.ScheduleFavoriteSM;
import com.smartboxtv.nunchee.data.modelssm.datahorary.DataResultSM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Esteban- on 16-06-14.
 */
public class ServiceManager {

    private static final String TAG = "SERVICE MANAGER";

    private static final String ERROR = "SERVICIO CAIDO";

    private final long EXPIRE = 10000;

    private final int PROGRAM_TYPE_BACKDROP = 0;

    private final int PROGRAM_TYPE_POSTER = 1;

    private final int PROGRAM_TYPE_SQUARE = 2;

    private final int CHANNEL_TYPE_ICON_LIGHT = 3;

    private final int CHANNEL_TYPE_ICON_DARK = 4;

    private static final String SERVICES_URL_TRIVIA = "http://190.215.44.18/wcfTrivia/TriviaService.svc/";

    private static final String SERVICES_URL_POLLS = "http://190.215.44.18/wcfPolls/TriviaService.svc/";

    private static final String SERVICES_URL = "http://192.168.1.173:8000/api/1.0/guide/";

    private static final String SERVICES_URL_TRENDING = "http://190.215.44.18/wcfNunchee2/GLFService.svc/Trending";

    private static final String URL_TWITTER = "https://api.twitter.com/1.1/statuses/";

    private static String URL_FEED, URL_FEED_PARAMETROS;

    private String URL;

    private final Context context;

    private AQuery aq;

    private long inicio, fin, delta;


    private List<FeedJSON> listaHistorial = new ArrayList<FeedJSON>();

    public ServiceManager(Context context) {
        this.context = context;
        aq = new AQuery(context);
    }

    public ServiceManager(Activity activity) {
        this.context = activity;
        aq = new AQuery(context);
    }

    // Login y Logout
    public void  loginStandar(){

    }

    public void  loginFacebook(){

    }

    public void logout(){

    }

    // Geo

    // Catedories

    public void getCategories(final ServiceManagerHandler<CategorieChannelSM> handler, String language, int id){
        inicio = System.currentTimeMillis();
        URL = SERVICES_URL+"getCategories?table="+id+"&language="+language;
        //Log.e(TAG,URL);

        aq.ajax(URL,JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {


                if(!object.isNull("data")){
                    //Log.e("DATA",object.toString());
                    //Toast.makeText(aq.getContext(), status.getCode() + ":" + object.toString(), Toast.LENGTH_LONG).show();
                    try {
                        JSONObject data = object.getJSONObject("data");
                        JSONArray categories = data.getJSONArray("categories");
                        List<CategorieChannelSM> list = new ArrayList<CategorieChannelSM>();

                        for(int i = 0; i<categories.length(); i++){
                            CategorieChannelSM cc = parseJsonObject(categories.getJSONObject(i), CategorieChannelSM.class);
                            //Log.e("Nombre categoria "+i,cc.name);
                            //Language language = parseJsonObject(categories.getJSONObject(i).getJSONObject("language"),Language.class);
                            //cc.setLanguage(new Language()) ;
                            //cc.setLanguage(language);
                            fin = System.currentTimeMillis();
                            delta = fin - inicio;
                            Log.e("Tiempo por categoria",cc.name+" --> "+delta);
                            list.add(cc);
                        }
                        Log.e("Tiempo total","--> "+delta);
                        handler.loaded(list);
                    } catch (Exception e) {
                        Log.e("ERROR --",e.getMessage());
                        e.printStackTrace();
                        handler.error(e.getMessage());
                    }
                }
                else if(object!= null){
                    Log.e(TAG,"Error 2 :" + status.getCode());
                    Toast.makeText(aq.getContext(), "Error 2 :" + status.getCode(), Toast.LENGTH_LONG).show();
                }
                else{
                    Log.e(TAG,"Error :" + status.getCode());
                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getProgramationByCategories(final ServiceManagerHandler<ProgramsCategorySM> handler, String userNunchee, String country, String language, String dateStart,
                                            String dateEnd, String deviceType, String idCategory ){

        userNunchee = "53a4397f25822c0710b299e9";
        inicio = System.currentTimeMillis();
       /* URL =   SERVICES_URL+"getPrograms?user="+userNunchee+"&country="+country+"&language="+language+"&start="+dateStart+"&end="+dateEnd+"&device_type"
                +"channel_image_type=3&program_image_type=1";*/
        //Log.e(TAG,URL);


        URL = "http://192.168.1.173:8000/api/1.0/guide/getPrograms?user="+userNunchee+"&category="+idCategory+"&start="+dateStart+"&end="+dateEnd+"&device_type=1&channel_image_type=4&program_image_type=0";
        Log.e("URL","---> "+URL);
        aq.ajax(URL,JSONObject.class,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(object != null){
                    try{
                            JSONObject data = object.getJSONObject("data");
                            JSONArray guide = data.getJSONArray("programs");
                            List<ProgramsCategorySM> list = new ArrayList<ProgramsCategorySM>();

                            if(guide != null){
                                for(int i = 0; i < guide.length();i++){
                                    ProgramsCategorySM p = parseJsonObject(guide.getJSONObject(i), ProgramsCategorySM.class);

                                    JSONObject episode = guide.getJSONObject(i).getJSONObject("episode");
                                    JSONObject channel = guide.getJSONObject(i).getJSONObject("channel");

                                    EpisodeSM e = parseJsonObject(episode, EpisodeSM.class);
                                    ChannelCategorySM c = parseJsonObject(channel, ChannelCategorySM.class);

                                    p.episode = e;
                                    p.channel = c;
                                    if(p.image != null){
                                        list.add(p);
                                    }
                                }
                                handler.loaded(list);
                            }
                        } catch (Exception e) {
                            handler.error(e.getMessage());
                            e.printStackTrace();

                    }
                }
                else{
                    Log.e(ERROR,"");
                    handler.error(ERROR);
                }
            }
        });
    }
    // Horary

    public void getProgramation(final ServiceManagerHandler<ChannelSM> handler, String userNunchee, String country, String language, String dateStart,
                                            String dateEnd, String deviceType, String idCategory ){

        userNunchee = "53a4397f25822c0710b299e9";
        inicio = System.currentTimeMillis();
       /* URL =   SERVICES_URL+"guideForUser?user="+userNunchee+"&country="+country+"&language="+language+"&start="+dateStart+"&end="+dateEnd+"&device_type"
                +"channel_image_type=3&program_image_type=1";*/
        //Log.e(TAG,URL);

        URL = "http://192.168.1.173:8000/api/1.0/guide/guideForUser?user="+userNunchee+"&country=CHL&language=ES&start=09%3A00%3A01%2020-04-2014&end=12%3A00%3A01%2020-04-2014&device_type=1&channel_image_type=4&program_image_type=2";

        aq.ajax(URL,JSONObject.class,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(object != null){
                    try{
                        JSONObject data = object.getJSONObject("data");
                        JSONArray guide = data.getJSONArray("channels_guide");
                        List<ChannelSM> list = new ArrayList<ChannelSM>();

                        if(guide != null){
                            for(int i = 0; i < guide.length();i++){
                                ChannelSM c = parseJsonObject(guide.getJSONObject(i), ChannelSM.class);
                                //Log.e("Nombre Canal "+i,c.name);
                                List<ScheduleSM> listSchedule = new ArrayList<ScheduleSM>();
                                //List<ProgramSM> listProgram = new ArrayList<ProgramSM>();
                                //List<EpisodeSM> listEpisode = new ArrayList<EpisodeSM>();

                                JSONArray schedule = guide.getJSONObject(i).getJSONArray("schedule");
                                for(int j = 0; j < schedule.length() ; j++){

                                    ScheduleSM s = parseJsonObject(schedule.getJSONObject(j), ScheduleSM.class);
                                    //Log.e("ScheduleSM",s.title);
                                    JSONObject episode = schedule.getJSONObject(j).getJSONObject("episode");
                                    JSONObject program = schedule.getJSONObject(j).getJSONObject("program");
                                    ProgramSM p = parseJsonObject(program, ProgramSM.class);
                                    EpisodeSM e = parseJsonObject(episode, EpisodeSM.class);
                                    s.program = p;
                                    s.episode = e;
                                    listSchedule.add(s);
                                }
                                c.listSchedule = listSchedule;
                                list.add(c);
                            }
                        }
                        int count = 0;
                        for(int i = 0; i < list.size();i++){
                            count = count + list.get(i).listSchedule.size();
                        }
                        Log.e("Tamaño lista final","--> "+count);
                        handler.loaded(list);
                    }
                    catch (Exception e){
                        Log.e("error",e.getMessage());
                        e.printStackTrace();
                        handler.error(e.getMessage());
                    }

                }
                else{
                    Log.e(ERROR,"getProgramation");
                    handler.error(ERROR);
                }
            }
        });
    }
    // Favorites

    public void getFavoriteForDay( final ServiceManagerHandler<ProgramFavoriteSM> handler, String userNunchee, String startDate, String endDate){

        userNunchee = "53a4397f25822c0710b299e9";

        URL = SERVICES_URL+"getFavoriteForDay?user="+userNunchee+"&date="+startDate+"&device_type=1&channel_image_type=4&program_image_type=2";
        Log.e("URL favorite",URL);
        //URL = "http://192.168.1.173:8000/api/1.0/guide/getFavoriteForDay?user=53a4397f25822c0710b299e9&date=24-06-2014&device_type=1&channel_image_type=4&program_image_type=2";

        AjaxCallback<JSONObject> ajaxCallback = new AjaxCallback<JSONObject>(){
            @Override
            public void callback( String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);

                if(status.getCode() == AjaxStatus.NETWORK_ERROR){
                    Log.e("Status",""+status.getTime());
                    Log.e("Status",""+status.getMessage());

                }
                if(object != null){
                    try {

                        JSONArray programs = object.getJSONObject("data").getJSONArray("programs");
                        List<ProgramFavoriteSM> list = new ArrayList<ProgramFavoriteSM>();

                        for(int i = 0; i < programs.length() ;i++){

                            ProgramFavoriteSM p = parseJsonObject(programs.getJSONObject(i),ProgramFavoriteSM.class);
                            JSONArray schedule = programs.getJSONObject(i).getJSONArray("schedule");
                            List<ScheduleFavoriteSM> listSchedule = new ArrayList<ScheduleFavoriteSM>();
                            //Log.e("Programa",p.name);
                            for(int j = 0 ; j < schedule.length() ;j++){
                                ScheduleFavoriteSM s = parseJsonObject(schedule.getJSONObject(j),ScheduleFavoriteSM.class);
                                ChannelFavoriteSM c = parseJsonObject(schedule.getJSONObject(j).getJSONObject("channel"),ChannelFavoriteSM.class);
                                //Log.e("Schedule",s.name);
                                //Log.e("Nombre canal",c.callLetter);
                                //Log.e("image",c.image);
                                //Log.e("Numero",""+c.id );
                                s.channel = c;
                                listSchedule.add(s);
                                p.schedule = listSchedule;
                            }
                            list.add(p);
                        }
                        handler.loaded(list);

                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.error(e.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                }
                else{
                    Log.e(ERROR,"getFavoriteForDay");
                }
            }
        };

        ajaxCallback.setTimeout(15000);
        aq.ajax(URL,JSONObject.class, ajaxCallback);
    }
    // Preview

    public void getPreview(final ServiceManagerHandler<Program> handler,String userNunchee, String idProgram, String idEpisode, String nombreCanal, String imageCanal, Date start, Date end){

        userNunchee = "53a4397f25822c0710b299e9";
        URL = SERVICES_URL+"getProgram?user="+userNunchee+"&program="+idProgram+"&device_type=1";
        //URL = "http://192.168.1.173:8000/api/1.0/guide/getProgram?user=53a4397f25822c0710b299e9&program=1763&device_type=1"; /// program

        final Program p = new Program();
        p.PChannel = new Channel();
        p.PChannel.channelCallLetter = nombreCanal;
        if(imageCanal != null)
            p.PChannel.channelImageURL = imageCanal;
        else
            p.PChannel.channelImageURL = "zdsvnzflkv";
        p.EndDate = end;
        p.StartDate = start;

        /**
         * Nombre canal
         * Foto canal
         *
         * Foto Programa
         * Fecha inicio
         * Fecha termino
         * Descripción episodio
         *
         * Favorito
         * Like
         * Check in
         *
         * */

        aq.ajax(URL,JSONObject.class, new AjaxCallback<JSONObject>(){

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if(object != null){
                    try{
                        JSONObject program = object.getJSONObject("data").getJSONObject("program");

                        //p.IsFavorite = program.getBoolean("IsFavorite");
                        //p.ILike = program.getBoolean("ILike");
                        //p.ICheckIn = program.getBoolean("ICheckIn");

                        JSONArray images = program.getJSONArray("image");
                        if(images.length()> 0 ){
                            String img = images.getString(0);
                            p.urlImage = img;
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.e("Error preview","--> "+e.getMessage());
                    }
                }
                else{
                    Log.e(ERROR, "getPreview");
                }
            }
        });

        if(idEpisode == null){
            idEpisode = "1";
        }
        else if(idEpisode.isEmpty()){
            idEpisode = "1";
        }
        URL = SERVICES_URL+"getEpisode?user="+userNunchee+"&episode="+idEpisode;
        Log.e("URL episodes","--> "+URL);
        //URL = "http://192.168.1.173:8000/api/1.0/guide/getEpisode?user=53a4397f25822c0710b299e9&episode=5144";

        aq.ajax(URL,JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(object != null){
                    try{
                        JSONObject  episode = object.getJSONObject("data").getJSONObject("episode");
                        p.EpisodeTitle = episode.getString("name");
                        p.Description = episode.getJSONObject("program").getString("description");
                        p.descriptionEpisode = episode.getString("description");
                        p.Title = episode.getJSONObject("program").getString("name");
                        p.Hashtags = episode.getString("hashtag")+";"+episode.getJSONObject("program").getString("hashtag");
                        String e = "Preview";
                        Log.e(e,p.Title);
                        //Log.e(e,p.PChannel.channelCallLetter);
                        Log.e(e,p.PChannel.channelImageURL);
                        Log.e(e,p.EndDate.toString());
                        Log.e(e,p.StartDate.toString());
                        Log.e(e,p.descriptionEpisode);
                        Log.e(e,p.EpisodeTitle );

                        handler.loaded(p);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.e(ERROR,"Nos fuimos a la B ");
                    }
                }
                else{
                    Log.e(ERROR,"getEpisode");
                }
            }
        });
    }
    // Tweets
    public void getTweets(final ServiceManagerHandler<TweetSM> handler, String searchValues, String limit){

        URL = SERVICES_URL+"getTwitt?search_values="+searchValues+"&retweet=false&limit="+limit;
        URL = "http://192.168.1.173:8000/api/1.0/guide/getTwitt?search_values=%23tvn&retweet=false";

        aq.ajax(URL, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(object!= null){
                    try{
                        JSONArray tweets = object.getJSONObject("data").getJSONArray("tweets");
                        List<TweetSM> listTw = new ArrayList<TweetSM>();

                        for(int i = 0 ; i < tweets.length(); i++){
                            TweetSM t = parseJsonObject(tweets.getJSONObject(i),TweetSM.class);
                            listTw.add(t);
                        }

                        handler.loaded(listTw);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        handler.error(e.getMessage());
                        Log.e("Error tw","-->"+e.getMessage());
                    }
                }
                else{
                    Log.e(ERROR,"getTwwts");
                }

            }
        });
    }
    // Trivia
    // Polls
    // Action Social
    // Recommendations
    // Search
    public void search(final ServiceManagerHandler<DataResultSM> handler, String userNunchee, String text){

        URL = "http://192.168.1.173:8000/api/1.0/guide/search?user=1&country=CL&title="+text+"&language=0&limit=5&device_type=1&image_type=0";

        aq.ajax(URL,JSONObject.class,new AjaxCallback<JSONObject>(){

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if(object != null){
                    try{
                        List<DataResultSM> listResult = new ArrayList<DataResultSM>();
                        JSONArray results = object.getJSONObject("data").getJSONArray("results");

                        for(int i = 0; i < results.length() ;i++){
                            DataResultSM dataResultSM = parseJsonObject(results.getJSONObject(i), DataResultSM.class);
                            listResult.add(dataResultSM);
                        }
                        handler.loaded(listResult);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        handler.error(e.getMessage());
                        Log.e("Error","--> "+e.getMessage());
                    }
                }
                else{
                    Log.e(ERROR,"search");
                }

            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////O/////T/////R/////O/////S////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String formatDate ( Date date)  {

        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZZ" );

        return  ""+targetFormat.format(date);

    }

    private <T> JSONObject encodeJsonObject(T obj)
            throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, JSONException {

        JSONObject result = new JSONObject();

        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                String variableName = method.getAnnotation(DataMember.class)
                        .member();
                Object value = method.invoke(obj);

                result.put(variableName, value);
            }
        }

        return result;
    }

    public <T> T parseJsonObject(JSONObject jsonObj, Class<T> type)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, JSONException {

        T result = type.newInstance();

        for (Method method : type.getMethods()) {
            if (method.getName().startsWith("set")) {
                DataMember dataMember = method.getAnnotation(DataMember.class);

                if (dataMember == null) {
                    continue;
                }

                String variableName = dataMember.member();
                Object value = jsonObj.isNull(variableName) ? null : jsonObj
                        .get(variableName);

                if (value != null) {
                    @SuppressWarnings("rawtypes")
                    Class[] params = method.getParameterTypes();
                    if (params[0] == String.class) {
                        method.invoke(result, value.toString());
                    }
                    else if(params[0]== Date.class){
                        method.invoke(result, cambiaFormatoFecha(value.toString()));
                    }
                    else {
                        method.invoke(result, value);
                    }
                }
            }
        }

        return result;
    }

    public Date cambiaFormatoFecha(String fecha)  {


        String format = "yyyy-MM-dd'T'HH:mm";
        DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        Date d = null;
        try {
            d = df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(d == null){
            Log.e("Formato Date","Null");
            return null;
        }
        return d;
    }
    public static class ServiceManagerHandler<T> {

        public void loaded(T data) {

        }

        public void loaded(List<T> data) {

        }

        public void error(String error) {

        }
    }

}
