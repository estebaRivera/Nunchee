package com.smartboxtv.nunchee.programation.horary;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.activities.PreviewActivity;
import com.smartboxtv.nunchee.animation.ManagerAnimation;
import com.smartboxtv.nunchee.data.image.ScreenShot;
import com.smartboxtv.nunchee.data.models.Channel;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.data.preference.UserPreference;
import com.smartboxtv.nunchee.programation.delegates.BarDayDelegate;
import com.smartboxtv.nunchee.programation.delegates.HorizontalScrollViewDelegate;
import com.smartboxtv.nunchee.programation.customize.HorizontalScrollViewCustom;
import com.smartboxtv.nunchee.programation.menu.DialogError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Esteban- on 21-04-14.
 */
public class HoraryFragmentPrograms extends Fragment {

    private BarDayDelegate barDayDelegate;
    private static final String SERVICES_URL = "http://190.215.44.18/wcfNunchee2/GLFService.svc/Programs/";

    private RelativeLayout containerLoading;

    private Program firstProgram;
    private Program lastProgram;

    private List<Channel> guide = new ArrayList<Channel>();

    private int DELTA;
    private final int THIRTY_MINUTES = 1800000;

    private final Date now = new Date();
    private Date dateActualShow = now;
    private Date dateActualProgram = now;
    private Date dateFirstProgram = now;
    private Date dateLastProgram = null;

    private Typeface normal;
    private Typeface light;

    private LinearLayout containerPrograms;
    private LinearLayout containerChannel;
    private LinearLayout listPrograms[];

    private View program;
    private View listChannel[];
    private View loading;
    private Button loadMore;
    private Button chargeLess;


    private TextView txtName;
    private TextView txtDate;

    private HorizontalScrollViewCustom horizontalScrollView;
    private AQuery aq;
    private Resources res;

    private Long end;
    private Long delta;
    private final Long begin = System.currentTimeMillis();
    private String url;
    private LayoutInflater inf;

    private boolean activeButtonNow = true;
    private boolean[] llistBoolean;

    public Date LIMIT_TOP;
    public Date LIMIT_BOTTOM;
    public Date FIRST_LIMIT_TOP;
    public Date FIRST_LIMIT_BOTTOM;
    private Animation mueveDerecha;
    //private MyClass task;
    private HashMap <String,String> programasCreados = new HashMap <String,String>();

    public HoraryFragmentPrograms() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.horary_fragment_programs, container, false);

        containerPrograms = (LinearLayout) rootView.findViewById(R.id.list_programs);
        containerChannel = (LinearLayout) rootView.findViewById(R.id.list_channel);
        containerLoading = (RelativeLayout) getActivity().findViewById(R.id.contenedor_preview);
        horizontalScrollView = (HorizontalScrollViewCustom) rootView.findViewById(R.id.horizontal_scroll_view);

        mueveDerecha = AnimationUtils.loadAnimation(getActivity(), R.anim.derecha_out_ahora);

        loadMore = (Button) getActivity().findViewById(R.id.btn_more);
        chargeLess = (Button) getActivity().findViewById(R.id.btn_back);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activeButtonNow = false;
                guide.clear();
                programasCreados.clear();
                containerChannel.removeAllViews();
                containerPrograms.removeAllViews();

                 //if(activeButtonNow)
                    dateActualShow = LIMIT_TOP;
                 //else
                 //   dateActualShow = now;
                loadMore.setVisibility(View.GONE);
                chargeLess.setVisibility(View.GONE);
                loadProgramation(LIMIT_TOP);
                if(barDayDelegate != null){
                    //barDayDelegate.updatePositionBar(LIMIT_TOP);
                }
            }
        });

        chargeLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activeButtonNow = false;
                guide.clear();
                programasCreados.clear();
                containerChannel.removeAllViews();
                containerPrograms.removeAllViews();

                //if(activeButtonNow)
                Date date = new Date(LIMIT_BOTTOM.getTime() - 12600000);
                dateActualShow = date ; // -3,5 horas
                //else
                //   dateActualShow = now;
                loadMore.setVisibility(View.GONE);
                chargeLess.setVisibility(View.GONE);
                loadProgramation(date);

                if(barDayDelegate != null){
                    barDayDelegate.updatePositionBar(date);
                }
            }
        });

        normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Light.ttf");
        res = getResources();

        DELTA = UserPreference.getWIDTH_SCREEN(getActivity())/2;

        HorizontalScrollViewDelegate delegate = new HorizontalScrollViewDelegate() {
            @Override
            public void onScrollChanged(int l, int t) {

                if((dateActualProgram.getTime() > LIMIT_TOP.getTime() || dateActualProgram.getTime() == LIMIT_TOP.getTime())){
                    horizontalScrollView.moveLeft = false;
                    loadMore.setVisibility(View.VISIBLE);

                }
                else{
                    horizontalScrollView.moveLeft = true;
                    loadMore.setVisibility(View.GONE);
                }
                if((dateActualProgram.getTime() < LIMIT_BOTTOM.getTime() || dateActualProgram.getTime() == LIMIT_BOTTOM.getTime())){
                    horizontalScrollView.moveRight = false;
                    chargeLess.setVisibility(View.VISIBLE);

                }
                else{
                    horizontalScrollView.moveRight = true;
                    chargeLess.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrollEnd(int l, int t) {

                if(barDayDelegate != null){

                    //long maximo = dateLastProgram.getTime()-3600000;

                    /*if(dateActualProgram.getTime() > maximo){

                    }*/
                    if(dateActualProgram.getTime() > aproximaFecha(now.getTime()).getTime()){
                      barDayDelegate.showButtonNow(true, activeButtonNow);
                    }
                    else if(dateActualProgram.getTime() == aproximaFecha(now.getTime()).getTime()){
                        barDayDelegate.showButtonNow(false, activeButtonNow);
                    }
                    if(dateActualProgram.getTime() < aproximaFecha(LIMIT_BOTTOM.getTime()).getTime()){
                        barDayDelegate.showButtonNow(true, activeButtonNow);
                    }
                    if(horizontalScrollView.moveToLeft()){
                        dateActualProgram = new Date(dateActualProgram.getTime()+1800000);
                        barDayDelegate.updateDayBar(dateActualProgram, true);

                    }
                    else{
                        dateActualProgram = new Date(dateActualProgram.getTime()-1800000);
                        barDayDelegate.updateDayBar(dateActualProgram, false);

                    }
                }
            }
        };
        horizontalScrollView.setDel(delegate);

        aq = new AQuery(horizontalScrollView);

        Date ahora = aproximaFecha(new Date().getTime());
        FIRST_LIMIT_TOP = new Date(ahora.getTime()+ 10800000);// 3,5 horas
        FIRST_LIMIT_BOTTOM = new Date(ahora.getTime() - 1800000 );//-1800000
        loadProgramation(new Date(ahora.getTime() - 1800000 ));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadProgramation(Date d){

        loading();
        dateActualShow = d;
        final Date nowDate = d;

        LIMIT_TOP = new Date(nowDate.getTime() + 10800000);// 3 horas // 1260000
        LIMIT_BOTTOM = new Date(nowDate.getTime() - 1800000 );//-1800000

        if(barDayDelegate != null){
            barDayDelegate.updateLimit(LIMIT_TOP, LIMIT_BOTTOM,FIRST_LIMIT_TOP,FIRST_LIMIT_BOTTOM);

           if(d.getTime()> FIRST_LIMIT_BOTTOM.getTime() && d.getTime()< FIRST_LIMIT_TOP.getTime()){
                barDayDelegate.updateFlag(false);
            }
            else{
                barDayDelegate.updateFlag(true);
            }

        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZZ");
        String horas = "1";
        String parametros = format.format(nowDate)+";"+UserPreference.getIdNunchee(getActivity())+";"+horas+"";
        String parametros64 = Base64.encodeToString(parametros.getBytes(), Base64.NO_WRAP);
        String url = SERVICES_URL+parametros64;
        Log.e("URL",url);
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {

                    JSONArray jsonArray = object.getJSONArray("DoWorkResult");

                    listChannel =  new View[jsonArray.length()];
                    listPrograms = new LinearLayout[jsonArray.length()];

                    List<Date> dateStart = new ArrayList<Date>();
                    List<Date> dateEnd = new ArrayList<Date>();
                    for(int i = 0; i < jsonArray.length(); i++){
                        try{
                            JSONArray jProgramas = jsonArray.getJSONObject(i).getJSONArray("Programs");

                            if(jProgramas.length() > 0 ){

                                String starDate = jProgramas.getJSONObject(0).getString("StartDate");
                                String endDate = jProgramas.getJSONObject(jProgramas.length()-1).getString("EndDate");

                                starDate = starDate.substring(starDate.indexOf("(")+ 1, starDate.indexOf("-"));
                                endDate = endDate.substring(endDate.indexOf("(")+ 1, endDate.indexOf("-"));

                                dateStart.add(new Date(Long.parseLong(starDate)));
                                dateEnd.add(new Date(Long.parseLong(endDate)));
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    end = System.currentTimeMillis();
                    delta = end - begin;
                    //borraLoading();
                    defineParametros(dateStart,dateEnd);
                    int count = 0;

                    LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    for(int i = 0; i < jsonArray.length(); i++){

                        llistBoolean = new boolean[jsonArray.length()];
                        JSONArray jProgramas = jsonArray.getJSONObject(i).getJSONArray("Programs");

                        if(jProgramas.length()>0){

                            count++;
                            listChannel[i] = inf.inflate(R.layout.element_channel, null,false);

                            aq.id(listChannel[i].findViewById(R.id.img_channel)).image(jsonArray.getJSONObject(i).getString("ChannelImage2"));
                            containerChannel.addView(listChannel[i]);

                            listPrograms[i] = new LinearLayout(getActivity());
                            listPrograms[i].setOrientation(LinearLayout.HORIZONTAL);

                            for(int j = 0 ;j < jProgramas.length();j++){

                               // program = inf.inflate(R.layout.element_program, null,false);

                                if(count % 2 == 0){
                                    program = inf.inflate(R.layout.element_program, null,false);
                                    llistBoolean[count] = true;
                                }
                                else{
                                    program = inf.inflate(R.layout.element_program_impar, null,false);
                                    llistBoolean[count] = false;
                                }

                                String starDate = jProgramas.getJSONObject(j).getString("StartDate");
                                starDate = starDate.substring(starDate.indexOf("(")+1, starDate.indexOf("-"));

                                String endDate = jProgramas.getJSONObject(j).getString("EndDate");
                                endDate = endDate.substring(endDate.indexOf("(")+1, endDate.indexOf("-"));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(determinaPadding(new Date(Long.parseLong(starDate)),
                                        new Date(Long.parseLong(endDate))),90);

                                params.setMargins(0,5,0,5);

                                //Programs
                                final Program p = new Program();
                                try{

                                    p.IdProgram = jProgramas.getJSONObject(j).getString("IdProgram");
                                    p.Title = jProgramas.getJSONObject(j).getString("Title");
                                    p.PChannel = new Channel() ;
                                    p.PChannel.setChannelID(jsonArray.getJSONObject(i).getString("IdChannel"));
                                    if(i % 2 == 0){
                                        p.getPChannel().par = true;
                                    }
                                    else{
                                        p.getPChannel().par = false;
                                    }
                                    p.StartDate = new Date(Long.parseLong(starDate));
                                    p.EndDate = new Date(Long.parseLong(endDate));
                                    p.IsFavorite = jProgramas.getJSONObject(j).getBoolean("IsFavorite");

                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                    Log.e("Error",e.getMessage());
                                }

                                try{
                                    if(j == 0){
                                        params.setMargins(determinaPadding(dateFirstProgram, new Date(Long.parseLong(starDate)) ), 5, 0, 5);
                                    }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                    Log.e("Margin", e.getMessage());
                                }

                                txtName = (TextView) program.findViewById(R.id.txt_name);
                                txtName.setTypeface(normal);

                                int padding = determinaPadding(new Date(Long.parseLong(starDate)),new Date(Long.parseLong(endDate)));

                                if(padding < 125 && jProgramas.getJSONObject(j).getString("Title").length() > 12 ){
                                    txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,10)+"...");

                                    if(padding < 85 && jProgramas.getJSONObject(j).getString("Title").length() > 7 ){
                                        txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,5)+"...");

                                        if(padding < 65){
                                            txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,5)+"...");

                                            if(padding < 45 && jProgramas.getJSONObject(j).getString("Title").length() > 5){
                                                txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,4)+"...");

                                                if(padding < 35 && jProgramas.getJSONObject(j).getString("Title").length() > 5){
                                                    txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,3)+"...");
                                                }
                                                else{
                                                    txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,4)+"...");
                                                }
                                            }
                                            else{
                                                txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,5)+"...");
                                            }
                                        }
                                        else{
                                            txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,6)+"...");
                                        }
                                    }
                                    else{
                                        txtName.setText(jProgramas.getJSONObject(j).getString("Title").substring(0,10)+"...");
                                    }
                                }
                                 else{
                                        txtName.setText(jProgramas.getJSONObject(j).getString("Title"));
                                }


                                txtDate = (TextView) program.findViewById(R.id.txt_date);
                                txtDate.setTypeface(light);
                                String  duracion  = ""+determinaDuracionMinutos(new Date(Long.parseLong(starDate)),new Date(Long.parseLong(endDate)))+" Minutos";
                                String date = dateFormat.format(new Date(Long.parseLong(starDate)))+" - "+dateFormat.format(new Date(Long.parseLong(endDate)))+" | "+duracion;

                                if(padding < 125){
                                    txtDate.setText(date.substring(0,14)+"...");
                                    if(padding < 85){
                                        txtDate.setText(date.substring(0,10)+"...");
                                    }
                                }
                                else
                                    txtDate.setText(date);

                                if(determinaDuracionMinutos(p.StartDate,p.EndDate) < 7){
                                    txtName.setText("");
                                    txtDate.setText("");
                                }
                                // Repetir nombre por cada 90 minutos
                                if(determinaDuracionMinutos(p.StartDate, p.EndDate) > 90){

                                    int duracionTotal = determinaDuracionMinutos(p.StartDate, p.EndDate);
                                    int contador = 1;
                                    int cuentaTitulos = 0;
                                    int limite = duracionTotal % 75;


                                    TextView txtRepeat = (TextView) program.findViewById(R.id.txt_name_repeated);
                                    txtRepeat.setTypeface(normal);

                                    txtRepeat.setTextSize(15);
                                    txtRepeat.setAlpha((float)0.3);
                                    txtRepeat.setTextColor(Color.WHITE);

                                    if(!((p.StartDate.getTime() > now.getTime())  || (p.EndDate.getTime() < now.getTime()))){

                                        txtRepeat.setTextColor(Color.parseColor("#101010"));
                                        txtRepeat.setAlpha((float) 0.4);
                                    }

                                    while(contador < duracionTotal-1){

                                        if(contador % 75 != 0){
                                            txtRepeat.setText(txtRepeat.getText()+"  ");
                                        }
                                        else if(cuentaTitulos < limite){
                                            txtRepeat.setText(txtRepeat.getText()+p.getTitle());
                                            cuentaTitulos++;
                                        }
                                        contador++;
                                    }
                                }

                                // Actual Program
                                if(!((p.StartDate.getTime() > now.getTime())   ||  (p.EndDate.getTime() < now.getTime()))){
                                    ImageView margen = (ImageView) program.findViewById(R.id.margin_left);
                                    ImageView fondo = (ImageView) program.findViewById(R.id.img_back);
                                    Drawable margenDrawable = res.getDrawable(R.drawable.bord_foco_programa);

                                    fondo.setBackgroundColor(Color.parseColor("#A9A9A9"));
                                    margen.setImageDrawable(margenDrawable);
                                    txtName.setTextColor(Color.parseColor("#101010"));
                                    txtDate.setTextColor(Color.parseColor("#101010"));

                                }
                                // favorito
                                if(p.IsFavorite){
                                    //Log.e("Favorito",p.getTitle());
                                    ImageView favorite = (ImageView) program.findViewById(R.id.img_favorite);
                                    Drawable d = getResources().getDrawable(R.drawable.favorito_fw);
                                    favorite.setImageDrawable(d);
                                    favorite.setVisibility(View.VISIBLE);
                                }
                                program.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        RelativeLayout r = (RelativeLayout) getActivity().findViewById(R.id.view_parent);
                                        Bitmap screenShot = ScreenShot.takeScreenshot(r);

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        screenShot.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        try {
                                            String filename = getActivity().getCacheDir()
                                                    + File.separator + System.currentTimeMillis() + ".jpg";

                                            File f = new File(filename);
                                            f.createNewFile();
                                            FileOutputStream fo = new FileOutputStream(f);
                                            fo.write(byteArray);
                                            fo.close();

                                            Intent i = new Intent(getActivity(), PreviewActivity.class);
                                            i.putExtra("background", filename);
                                            i.putExtra("programa", p);
                                            startActivity(i);
                                            getActivity().overridePendingTransition(R.anim.zoom_in_preview, R.anim.nada);
                                            //context.startActivity(i);
                                        } catch (IOException e) {
                                            e.printStackTrace();

                                            Log.e("Preview","--> "+e.getMessage());

                                            DialogError dialogError = new DialogError();
                                            dialogError.show(getActivity().getSupportFragmentManager(),"");
                                        }


                                    }
                                });

                                listPrograms[i].addView(program);
                                program.setLayoutParams(params);
                                programasCreados.put(p.getPChannel().channelID+"|"+p.IdProgram+"|"+p.StartDate.getTime(),p.Title);
                            }
                            //borraLoading();
                            defineParametros(dateStart,dateEnd);
                            containerPrograms.addView(listPrograms[i]);

                        }
                    }
                    actualizaPosicion(nowDate,true);

                    end = System.currentTimeMillis();
                    delta = end - begin;
                    Log.e("Tiempo","tiempo transcurrido "+delta);
                    //borraLoading();

                   MyClass task = new MyClass();
                   task.execute("");
                }
                catch(Exception e){
                    borraLoading();
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }
            }
        });
    }

    public void clean(){
        activeButtonNow = true;
        guide.clear();
        programasCreados.clear();
        containerChannel.removeAllViews();
        containerPrograms.removeAllViews();
    }

    // Metodos Auxiliares
    public Date getDateLastProgram() {
        return dateLastProgram;
    }

    public void defineParametros(List<Date> dateStart,List<Date> dateEnd){

        Long minimo = dateStart.get(0).getTime();
        Long maximo = dateEnd.get(0).getTime();

        int contadorMinimo = 0;

        try{
            for(int i = 0; i < dateStart.size() && i < dateEnd.size(); i++){
                if(dateStart.size() > 1 && dateEnd.size() > 1){

                    if(minimo > dateStart.get(i).getTime()){
                        contadorMinimo = i;
                        minimo =  dateStart.get(i).getTime();
                    }

                    if(maximo <  dateEnd.get(i).getTime()){
                        maximo =  dateEnd.get(i).getTime();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Define paramatro",e.getMessage());
        }

        dateFirstProgram = new Date(minimo);
        dateLastProgram = new Date(maximo);
    }

    public void loading(){

        end = System.currentTimeMillis();
        delta = (end - begin);
        //Log.e("Inicio loading ",""+delta);

        loading = getLayoutInflater(null).inflate(R.layout.progress_dialog_pop_corn, null);
        ImageView pop = (ImageView) loading.findViewById(R.id.pop_corn_centro);

        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        Animation animaPop = AnimationUtils.loadAnimation(getActivity(), R.anim.animacion_pop_hacia_derecha_centro);

        loading.setLayoutParams(params);
        loading.setEnabled(false);
        pop.startAnimation(animaPop);

        //ManagerAnimation.anim(pop);

        containerLoading.addView(loading);
        containerLoading.setEnabled(false);

        loading.bringToFront();
        loading.setClickable(false);

        containerLoading.setClickable(false);
    }

    public void borraLoading(){

        Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.deaparece);
        loading.setClickable(true);
        loading.setEnabled(true);
        loading.startAnimation(anim);

        containerLoading.removeView(loading);
        containerLoading.setEnabled(true);

        end = System.currentTimeMillis();
        delta = (end - begin);
    }

    public void actualizaPosicion(int x){
        horizontalScrollView.setxScroll(x);
    }

    public void actualizaPosicion(Date d, boolean first){

        int ancho = determinaAncho(dateFirstProgram, aproximaFecha(d.getTime()));
        dateActualProgram = d;

        if(first){
            ancho = ancho - (DELTA - 122); // 122 es la suma del contenedor del canal más los margenes
            horizontalScrollView.setxScroll(ancho);
        }
        else{
            ancho = ancho - (DELTA - 122);
            horizontalScrollView.smoothScrollTo(ancho, 0);
        }
    }

    public int determinaAncho(Date inicio, Date fin){

        long diferencia = fin.getTime() - inicio.getTime();
        int duracion = (int) ((diferencia/60000));
        int ancho = duracion * 12;//8
        return ancho;
    }

    public Date aproximaFecha(long date){

        long residuo = date % (300000 * 6);
        date = date - residuo;
        return new Date(date);
    }

    public int determinaDuracionMinutos(Date inicio, Date fin){

        long diferencia = fin.getTime() - inicio.getTime();
        int duracion = (int) ((diferencia/60000));
        return duracion;
    }

    public int determinaPadding(Date inicio, Date fin){

        long diferencia = fin.getTime() - inicio.getTime();
        int duracion = (int) ((diferencia/60000));
        int ancho = duracion * 12;// 8
        return ancho;
    }

    // Get and Set

    public void setBarDayDelegate(BarDayDelegate barDayDelegate) {
        this.barDayDelegate = barDayDelegate;
    }


    class MyClass extends AsyncTask <String,Channel,Void>{

        @Override
        protected void onPreExecute() {

            //final Date now = ;
            final Date date = new Date(dateActualShow.getTime()+9000000);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZZ");
            String horas = "2";
            String parametros = format.format(date)+";"+UserPreference.getIdNunchee(getActivity())+";"+horas+"";
            String parametros64 = Base64.encodeToString(parametros.getBytes(), Base64.NO_WRAP);
            url = SERVICES_URL+parametros64;
            inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        int count = 0;
                        JSONArray jsonArray = object.getJSONArray("DoWorkResult");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONArray jProgramas = jsonArray.getJSONObject(i).getJSONArray("Programs");

                            if(jProgramas.length()>0){
                                count++;
                                Channel c = new Channel();
                                c.channelImageURL = jsonArray.getJSONObject(i).getString("ChannelImage2");
                                c.channelID = jsonArray.getJSONObject(i).getString("IdChannel");
                                c.channelCallLetter = jsonArray.getJSONObject(i).getString("CallLetter");
                                c.channelPrograms = new ArrayList<Program>();
                                c.position = i;
                                String starDate;
                                String endDate;

                                for(int j = 0 ;j < jProgramas.length();j++){
                                    Program p = new Program();
                                    p.IdProgram = jProgramas.getJSONObject(j).getString("IdProgram");
                                    p.Title = jProgramas.getJSONObject(j).getString("Title");
                                    p.PChannel = new Channel() ;
                                    p.PChannel.setChannelID(jsonArray.getJSONObject(i).getString("IdChannel"));
                                    starDate = jProgramas.getJSONObject(j).getString("StartDate");
                                    starDate = starDate.substring(starDate.indexOf("(")+1, starDate.indexOf("-"));
                                    endDate = jProgramas.getJSONObject(j).getString("EndDate");
                                    endDate = endDate.substring(endDate.indexOf("(")+1, endDate.indexOf("-"));
                                    p.StartDate = new Date(Long.parseLong(starDate));
                                    p.EndDate = new Date(Long.parseLong(endDate));
                                    p.IsFavorite = jProgramas.getJSONObject(j).getBoolean("IsFavorite");
                                    c.channelPrograms.add(p);
                                }

                                if(count % 2 == 0)
                                    c.par = true;
                                else
                                    c.par = false;

                                publishProgress(c);

                                end = System.currentTimeMillis();
                                delta = end - begin;
                                //Log.e("Tiempo canal "+i,"tiempo"+delta);
                                guide.add(c);
                                borraLoading();
                            }

                            //Log.e("Tamaño"," final "+guide.size());
                        }
                        end = System.currentTimeMillis();
                        delta = end - begin;
                        //Log.e("Asyntask","tiempo total"+delta);

                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.e("error",e.getMessage());
                    }
                }
            });
            return null;
        }


        @Override
        protected void onProgressUpdate(Channel... channel) {

            Channel c = channel[0];
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

            String key;
            int count = 0;

            for(int j = 0 ;j < c.channelPrograms.size();j++){

                Program p = c.channelPrograms.get(j);
                key = c.channelID+"|"+p.IdProgram+"|"+p.StartDate.getTime();

                if(!programasCreados.containsKey(key)){

                    if(c.par){
                        program = inf.inflate(R.layout.element_program, null,false);
                    }
                    else{
                        program = inf.inflate(R.layout.element_program_impar, null,false);
                    }
                    txtName = (TextView) program.findViewById(R.id.txt_name);
                    txtName.setTypeface(normal);

                    int padding = determinaPadding(p.StartDate,p.EndDate);

                    /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(padding,90);
                    params.setMargins(0,5,0,5);*/

                    if(padding < 125 && p.Title.length() > 12 ){
                        txtName.setText(p.Title.substring(0, 10)+"...");

                        if(padding < 85 && p.Title.length() > 7 ){
                            txtName.setText(p.Title.substring(0, 5)+"...");

                            if(padding < 65){
                                txtName.setText(p.Title.substring(0, 5)+"...");

                                if(padding < 45 && p.Title.length() > 5){
                                    txtName.setText(p.Title.substring(0, 4)+"...");

                                    if(padding < 35 && p.Title.length() > 5){
                                        txtName.setText(p.Title.substring(0, 3)+"...");
                                    }
                                    else{
                                        txtName.setText(p.Title.substring(0, 4)+"...");
                                    }
                                }
                                else{
                                    txtName.setText(p.Title.substring(0, 5)+"...");
                                }
                            }
                            else{
                                txtName.setText(p.Title.substring(0, 6)+"...");
                            }
                        }
                        else{
                            txtName.setText(p.Title.substring(0, 10)+"...");
                        }
                    }
                    else{
                        txtName.setText(p.Title);
                    }

                    txtDate = (TextView) program.findViewById(R.id.txt_date);
                    txtDate.setTypeface(light);
                    String  duracion  = ""+determinaDuracionMinutos(p.StartDate,p.EndDate)+" Minutos";
                    String date = dateFormat.format(p.StartDate)+" - "+dateFormat.format(p.EndDate)+" | "+duracion;

                    if(padding < 125){
                        txtDate.setText(date.substring(0,14)+"...");
                        if(padding < 85){
                            txtDate.setText(date.substring(0,10)+"...");
                        }
                    }
                    else
                        txtDate.setText(date);

                    // Repetir nombre por cada 90 minutos
                    if(determinaDuracionMinutos(p.StartDate, p.EndDate) > 90){

                        int duracionTotal = determinaDuracionMinutos(p.StartDate, p.EndDate);
                        int contador = 1;
                        int cuentaTitulos = 0;
                        int limite = duracionTotal % 75;

                        TextView txtRepeat = (TextView) program.findViewById(R.id.txt_name_repeated);
                        txtRepeat.setTypeface(normal);

                        txtRepeat.setTextSize(15);
                        txtRepeat.setAlpha((float)0.3);
                        txtRepeat.setTextColor(Color.WHITE);

                        if(!((p.StartDate.getTime() > now.getTime())  || (p.EndDate.getTime() < now.getTime()))){

                            txtRepeat.setTextColor(Color.parseColor("#101010"));
                            txtRepeat.setAlpha((float) 0.4);
                        }

                        while(contador < duracionTotal-1){

                            if(contador % 75 != 0){
                                txtRepeat.setText(txtRepeat.getText()+"  ");
                            }
                            else if(cuentaTitulos < limite){
                                txtRepeat.setText(txtRepeat.getText()+p.getTitle());
                                cuentaTitulos++;
                            }
                            contador++;
                        }
                    }

                    // favorito
                    if(p.IsFavorite){
                        ImageView favorite = (ImageView) program.findViewById(R.id.img_favorite);
                        Drawable d = getResources().getDrawable(R.drawable.favorito_fw);
                        favorite.setImageDrawable(d);
                        favorite.setVisibility(View.VISIBLE);
                    }
                    final Program p1 = p;
                    program.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            RelativeLayout r = (RelativeLayout) getActivity().findViewById(R.id.view_parent);
                            Bitmap screenShot = ScreenShot.takeScreenshot(r);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            screenShot.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                            byte[] byteArray = stream.toByteArray();

                            try {
                                String filename = getActivity().getCacheDir()
                                        + File.separator + System.currentTimeMillis() + ".jpg";

                                File f = new File(filename);
                                f.createNewFile();
                                FileOutputStream fo = new FileOutputStream(f);
                                fo.write(byteArray);
                                fo.close();

                                Intent i = new Intent(getActivity(), PreviewActivity.class);
                                i.putExtra("background", filename);
                                i.putExtra("programa", p1);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.zoom_in_preview, R.anim.nada);
                                //context.startActivity(i);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("Preview","--> "+e.getMessage());
                                DialogError dialogError = new DialogError();
                                dialogError.show(getActivity().getSupportFragmentManager(),"");
                            }
                        }
                    });

                    // Actual Program
                    if(!((p.StartDate.getTime() > now.getTime())   ||  (p.EndDate.getTime() < now.getTime()))){
                        ImageView margen = (ImageView) program.findViewById(R.id.margin_left);
                        ImageView fondo = (ImageView) program.findViewById(R.id.img_back);
                        Drawable margenDrawable = res.getDrawable(R.drawable.bord_foco_programa);

                        fondo.setBackgroundColor(Color.parseColor("#A9A9A9"));
                        margen.setImageDrawable(margenDrawable);
                        txtName.setTextColor(Color.parseColor("#101010"));
                        txtDate.setTextColor(Color.parseColor("#101010"));

                    }
                    listPrograms[c.position].addView(program);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(padding,90);
                    params.setMargins(0,5,0,5);
                    program.setLayoutParams(params);
                   // Log.e("Programa no creado","programa añadido");
                }
                else{
                   // Log.e("Programa creado","Programa Creado");
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // Log.e("zsdfd","sdfsdg");
            super.onPostExecute(aVoid);
        }
    }
}
