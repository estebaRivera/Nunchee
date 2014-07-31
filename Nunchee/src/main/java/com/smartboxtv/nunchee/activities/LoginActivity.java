package com.smartboxtv.nunchee.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.smartboxtv.nunchee.animation.AnimationClass;
import com.smartboxtv.nunchee.data.clean.DataClean;
import com.smartboxtv.nunchee.data.database.DataBaseUser;
import com.smartboxtv.nunchee.data.database.UserNunchee;
import com.smartboxtv.nunchee.data.preference.UserPreference;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.services.DataLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Esteban- on 18-04-14.
 */
public class LoginActivity extends ActionBarActivity {

    private final ArrayList<AnimationClass> listAnimation = new ArrayList<AnimationClass>();

    private TextView fraseInicialAdelante;
    private TextView fraseInicialTrasera;
    private TextView p1;
    private TextView p2;
    private TextView p3;

    private Button btnLoginFb;
    private Button btnLogo;

    private ImageView imagenAdelante;
    private ImageView imagenTrasera;
    private Drawable drwBackground;
    private Drawable drwBackground2;
    private Drawable drwBackground3;

    private String initialPhrase;
    private String initialPhrase2;
    private String initialPhrase3;

    private Timer timer;
    private int contador;
    private int indice;
    private Animation aparece;
    private DataBaseUser dataBaseUser;
    private UserNunchee userNunchee;
    private String idString;
    private String imageString;
    private String nameString;

    private boolean block;
    //private Animation puntos;
    //private Animation nada;

    // SDK Facebook
    private UiLifecycleHelper uiHelper;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        DataClean.garbageCollector("Login Activity");
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        try {
            PackageInfo info =     getPackageManager().getPackageInfo("com.smartboxtv.nunchee",     PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KEY HASH:", sign);
                //Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        fraseInicialAdelante = (TextView) findViewById(R.id.initial_phrase);
        fraseInicialTrasera = (TextView) findViewById(R.id.initial_phrase_2);

        block = true;

        btnLoginFb = (Button) findViewById(R.id.login_facebook);
        btnLogo = (Button) findViewById(R.id.logo);

        imagenAdelante = (ImageView) findViewById(R.id.back);
        imagenTrasera = (ImageView) findViewById(R.id.back2);

        p1 = (TextView) findViewById((R.id.p1));
        p2 = (TextView) findViewById((R.id.p2));
        p3 = (TextView) findViewById((R.id.p3));

        iniciaVariable();
        randomImage();

        listAnimation.add(new AnimationClass(drwBackground, initialPhrase));
        listAnimation.add(new AnimationClass(drwBackground2, initialPhrase2));
        listAnimation.add(new AnimationClass(drwBackground3, initialPhrase3));



        btnLoginFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Login Facebook SDK
                uiHelper = new UiLifecycleHelper(LoginActivity.this, new Session.StatusCallback() {
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {

                    }
                });

                uiHelper.onCreate(savedInstanceState);
                uiHelper.onResume();

                // start Facebook Login
                Session.openActiveSession(LoginActivity.this, true, new Session.StatusCallback() {
                    // callback when session changes state
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {

                        Log.e("State",""+ state+"- "+exception);
                        if (session.isOpened()) {
                            // make request to the /me API
                            Request.newMeRequest(session, new Request.GraphUserCallback() {

                                // callback after Graph API response with user object
                                @Override
                                public void onCompleted(GraphUser user, Response response) {

                                    if (user != null && block) {

                                        block = false;
                                        UserPreference.setIdFacebook(user.getId(), LoginActivity.this);
                                        UserPreference.setNombreFacebook(user.getName(), LoginActivity.this);
                                        String parametro = "0;"

                                                + user.getName() + ";"
                                                + "http://graph.facebook.com/" + UserPreference.getIdFacebook(LoginActivity.this) + "/picture?type=normal;"
                                                + user.getId() + ";"
                                                + user.getUsername() + ";"
                                                + user.getProperty("email");
                                                idString = user.getId();
                                                nameString = user.getName();
                                                imageString = "http://graph.facebook.com/" + UserPreference.getIdFacebook(LoginActivity.this) + "/picture?type=normal;";

                                        String parametroBase64 = Base64.encodeToString(parametro.getBytes(), Base64.NO_WRAP);
                                        DataLoader dataLogin = new DataLoader(LoginActivity.this);
                                        dataLogin.loginService(parametroBase64, new DataLoader.DataLoadedHandler<String>() {

                                            @Override
                                            public void loaded(String data) {
                                                super.loaded(data);

                                                    dataBaseUser = new DataBaseUser(getApplicationContext(),"",null,0);
                                                    userNunchee = dataBaseUser.select(idString);
                                                    if(userNunchee == null){
                                                        dataBaseUser.insertUser(idString,imageString,nameString);
                                                        Log.e("Nombre",nameString);
                                                        Log.e("id",idString);
                                                        Log.e("image",imageString);
                                                    }
                                                    Intent intent = new Intent(LoginActivity.this, RecommendedActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.animacion_arriba, R.anim.animacion_abajo);

                                            }

                                            @Override
                                            public void error(String error) {
                                                super.error(error);

                                            }
                                        });

                                    }

                                }

                            }).executeAsync();
                        }
                    }
                });

                uiHelper.onCreate(savedInstanceState);
                /*Intent intent = new Intent(LoginActivity.this, RecommendedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animacion_arriba, R.anim.animacion_abajo);*/

            }
        });

        timer.cancel();
        timer.purge();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public void iniciaVariable(){

        contador = 0;

        Resources res = getResources();

        drwBackground = res.getDrawable(R.drawable.slide_1);
        drwBackground2 = res.getDrawable(R.drawable.slide_2);
        drwBackground3 = res.getDrawable(R.drawable.slide_3);

        initialPhrase = "Interactúa con tus programas preferidos!";
        initialPhrase2 = "Recibe recomendaciones según tus preferencias";
        initialPhrase3 = "Ordena, descubre y comparte tus programas favoritos!!";

        aparece = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        //puntos = AnimationUtils.loadAnimation(this,R.anim.puntos);
        //nada = AnimationUtils.loadAnimation(this,R.anim.nada);

        Display display = getWindowManager().getDefaultDisplay();

        UserPreference.setWIDTH_SCREEN(display.getWidth(), this);
        UserPreference.setHEIGTH_SCREEN(display.getHeight(),this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void randomImage(){

        Collections.shuffle(listAnimation);

        Typeface normal = Typeface.createFromAsset(getAssets(), "fonts/SegoeWP.ttf");

        fraseInicialAdelante.setTypeface(normal);
        fraseInicialTrasera.setTypeface(normal);

        p1.setTypeface(normal);
        p2.setTypeface(normal);
        p3.setTypeface(normal);

        indice = 0;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeImage();

                    }
                });
            }
        },0,5000);

    }
    public void changeImage(){


        switch (contador){

            case 0:
                imagenAdelante.setImageDrawable(listAnimation.get(indice).getImageBackground());       // Set background y frase inicial
                fraseInicialAdelante.setText(listAnimation.get(indice).getInitialPhrase());
                imagenAdelante.startAnimation(aparece);                                            // Animaciones
                fraseInicialAdelante.startAnimation(aparece);
                imagenAdelante.bringToFront();                                                     //  Traer elementos hacia adelante
                btnLoginFb.bringToFront();
                fraseInicialAdelante.bringToFront();
                btnLogo.bringToFront();
                contador++;

                if(indice   ==  0){
                    p1.setTextColor(Color.parseColor("#FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  1){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  2){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#FFFFFF"));
                }


                p1.bringToFront();
                p2.bringToFront();
                p3.bringToFront();

                if(indice < 2){
                    indice++;
                }
                else{
                    indice = 0;
                }

                break;

            case 1:
                imagenTrasera.setImageDrawable(listAnimation.get(indice).getImageBackground());       // Set background y frase inicial
                fraseInicialTrasera.setText(listAnimation.get(indice).getInitialPhrase());
                imagenTrasera.startAnimation(aparece);
                fraseInicialTrasera.startAnimation(aparece);
                imagenTrasera.bringToFront();                                                     //  Traer elementos hacia adelante
                btnLoginFb.bringToFront();
                fraseInicialTrasera.bringToFront();
                btnLogo.bringToFront();
                contador++;

                if(indice   ==  0){
                    p1.setTextColor(Color.parseColor("#FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  1){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  2){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#FFFFFF"));
                }

                p1.bringToFront();
                p2.bringToFront();
                p3.bringToFront();

                if(indice < 2){
                    indice++;
                }
                else{
                    indice = 0;
                }

                break;

            case 2:
                imagenAdelante.setImageDrawable(listAnimation.get(indice).getImageBackground());       // Set background y frase inicial
                fraseInicialAdelante.setText(listAnimation.get(indice).getInitialPhrase());
                imagenAdelante.startAnimation(aparece);                                            // Animaciones
                fraseInicialAdelante.startAnimation(aparece);
                imagenAdelante.bringToFront();                                                     //  Traer elementos hacia adelante
                btnLoginFb.bringToFront();
                fraseInicialAdelante.bringToFront();
                btnLogo.bringToFront();
                contador++;

                if(indice   ==  0){
                    p1.setTextColor(Color.parseColor("#FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  1){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  2){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#FFFFFF"));
                }


                p1.bringToFront();
                p2.bringToFront();
                p3.bringToFront();

                if(indice < 2){
                    indice++;
                }
                else{
                    indice = 0;
                }

                break;

            case 3:
                imagenTrasera.setImageDrawable(listAnimation.get(indice).getImageBackground());       // Set background y frase inicial
                fraseInicialTrasera.setText(listAnimation.get(indice).getInitialPhrase());
                imagenTrasera.startAnimation(aparece);
                fraseInicialTrasera.startAnimation(aparece);
                imagenTrasera.bringToFront();                                                     //  Traer elementos hacia adelante
                btnLoginFb.bringToFront();
                fraseInicialTrasera.bringToFront();
                btnLogo.bringToFront();
                contador = 0;

                if(indice   ==  0){
                    p1.setTextColor(Color.parseColor("#FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  1){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#FFFFFF"));
                    p3.setTextColor(Color.parseColor("#55FFFFFF"));
                }
                if(indice   ==  2){
                    p1.setTextColor(Color.parseColor("#55FFFFFF"));
                    p2.setTextColor(Color.parseColor("#55FFFFFF"));
                    p3.setTextColor(Color.parseColor("#FFFFFF"));
                }

                p1.bringToFront();
                p2.bringToFront();
                p3.bringToFront();

                if(indice < 2){
                    indice++;
                }
                else{
                    indice = 0;
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        randomImage();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        block = true;
        super.onPostResume();
    }

    @Override
    public void onDestroy() {
        this.listAnimation.clear();
        this.timer.cancel();
        BitmapAjaxCallback.clearCache();
        AQUtility.cleanCacheAsync(this);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
