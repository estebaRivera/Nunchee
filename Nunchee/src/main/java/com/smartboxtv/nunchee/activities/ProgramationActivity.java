package com.smartboxtv.nunchee.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.Session;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.animation.ManagerAnimation;
import com.smartboxtv.nunchee.data.clean.DataClean;
import com.smartboxtv.nunchee.data.database.DataBaseUser;
import com.smartboxtv.nunchee.data.database.UserNunchee;
import com.smartboxtv.nunchee.data.image.ScreenShot;
import com.smartboxtv.nunchee.data.models.Channel;
import com.smartboxtv.nunchee.data.models.FeedJSON;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.data.models.TrendingChannel;
import com.smartboxtv.nunchee.data.models.UserFacebook;
import com.smartboxtv.nunchee.data.preference.UserPreference;
import com.smartboxtv.nunchee.programation.categories.CategoryFragment;
import com.smartboxtv.nunchee.programation.favorites.FavoriteFragment;
import com.smartboxtv.nunchee.programation.horary.HoraryFragment;
import com.smartboxtv.nunchee.programation.menu.About;
import com.smartboxtv.nunchee.programation.menu.DialogError;
import com.smartboxtv.nunchee.programation.menu.FavoriteMenuFragment;
import com.smartboxtv.nunchee.programation.menu.FeedFragment;
import com.smartboxtv.nunchee.programation.menu.NotificationFragment;
import com.smartboxtv.nunchee.programation.menu.Politica;
import com.smartboxtv.nunchee.programation.preview.PreviewFragment;
import com.smartboxtv.nunchee.programation.preview.TriviaMinFragment;
import com.smartboxtv.nunchee.services.DataLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esteban- on 18-04-14.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ProgramationActivity extends ActionBarActivity{

    private Button horario;
    private Button favorito;
    private Button categoria;
    private ImageButton menu;

    private DataBaseUser dataBaseUser;
    private UserNunchee userNunchee;

    private EditText search;
    private ImageView iconSearch;

    private Fragment fg;
    private FragmentTransaction ft;
    private RelativeLayout contenedorMenu;
    private RelativeLayout contenedorMenuBar;
    private List<FeedJSON> listFeed;
    private List<UserFacebook> friends;
    private List<TrendingChannel> lista = new ArrayList<TrendingChannel>();
    private HoraryFragment horaryFragment;

    // Fragmentos pequeños

    private TriviaMinFragment fragmentoTriviaP;
    // Loading
    private View viewLoading;
    private LayoutInflater inflaterPrivate;
    private RelativeLayout contenedorLoading;
    private RelativeLayout contenedorActionbarOption;
    private boolean isConfiguration = false;
    private boolean isMessage = false;
    private boolean isNotification = false;
    private boolean isSlideMenuOpen = false;
    private boolean fbActivate;
    private boolean showSearch = false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)


    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataClean.garbageCollector("Recommended Activity");
        Resources res = getResources();
        Drawable background = res.getDrawable(R.drawable.back_nav_s);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(background);

        setContentView(R.layout.programation_fragment);
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/SegoeWP-Bold.ttf");

        inflaterPrivate = LayoutInflater.from(getApplicationContext());
        contenedorLoading = (RelativeLayout) findViewById(R.id.contenedor_preview);
        contenedorActionbarOption = (RelativeLayout) findViewById(R.id.contenedor_action_bar);
        contenedorMenuBar = (RelativeLayout) findViewById(R.id.contenedor_menu_bar);

        horario = (Button) findViewById(R.id.boton_horario);
        horario.setTypeface(bold);
        horario.setBackgroundResource(R.drawable.effect);
        horario.setSelected(true);

        categoria = (Button) findViewById(R.id.boton_categoria);
        categoria.setTypeface(bold);
        categoria.setBackgroundResource(R.drawable.effect);

        favorito = (Button) findViewById(R.id.boton_favorito);
        favorito.setTypeface(bold);
        favorito.setBackgroundResource(R.drawable.effect);

        horaryFragment = new HoraryFragment();

        dataBaseUser = new DataBaseUser(getApplicationContext(),"",null,0);
        userNunchee = dataBaseUser.select(UserPreference.getIdFacebook(getApplicationContext()));
        fbActivate = userNunchee.isFacebookActive;

        ft =  getSupportFragmentManager().beginTransaction();
        ft.add(R.id.contenedor, horaryFragment);
        ft.commit();
        fg = horaryFragment;

        DataLoader dataLoader = new DataLoader(getApplication());
        dataLoader.getTrendingChannel( new DataLoader.DataLoadedHandler<TrendingChannel>(){
            @Override
            public void loaded(List<TrendingChannel> data) {
                super.loaded(data);

                lista = data;
                setDataTrendinChannel();
            }
            @Override
            public void error(String error) {
                super.error(error);
            }
        });

        horario.setOnClickListener(new View.OnClickListener() {                                             // Declaración de evento de Listener por boton
            @Override
            public void onClick(View view) {

                if(!horario.isSelected()){

                    horario.setSelected(true);
                    categoria.setSelected(false);
                    favorito.setSelected(false);

                    int id;
                    if(fg != null){

                        id = fg.getId();
                        ft =  getSupportFragmentManager().beginTransaction();
                        ft.commit();
                    }
                    else{
                        id = R.id.contenedor;
                    }

                    HoraryFragment horaryFragment = new HoraryFragment();

                    ft =  getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.contenedor, horaryFragment);
                    ft.commit();
                    fg = horaryFragment;
                }
            }
        });

        categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!categoria.isSelected()){

                    horario.setSelected(false);
                    categoria.setSelected(true);
                    favorito.setSelected(false);

                    int id;
                    if(fg != null){

                        id = fg.getId();
                        ft =  getSupportFragmentManager().beginTransaction();
                        ft.detach(fg);
                        ft.commit();
                    }
                    else{
                        id = R.id.contenedor;
                    }

                    CategoryFragment categoryFragment = new CategoryFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); // Llena el RelativeLayout (contenedor) con el XML correspondiente a la Clase

                    ft.replace(id, categoryFragment);
                    ft.commit();

                    fg = categoryFragment;
                }
            }
        });

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!favorito.isSelected()){

                    horario.setSelected(false);
                    categoria.setSelected(false);
                    favorito.setSelected(true);

                    int id;
                    if(fg != null){

                        id = fg.getId();
                        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();

                        ft.detach(fg);
                        ft.commit();
                    }
                    else{
                        id = R.id.contenedor;
                    }
                    FavoriteFragment favoriteFragment = new FavoriteFragment();
                    FragmentTransaction ft =  getSupportFragmentManager().beginTransaction(); // Llena el RelativeLayout (contenedor) con el XML correspondiente a la Clase

                    ft.replace(id, favoriteFragment);
                    ft.commit();

                    fg = favoriteFragment;
                }
            }
        });
        createActionBar();
    }

    public void setDataTrendinChannel(){

        AQuery aq;
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.lista_canales);

        for(int i = 0; i < lista.size(); i++){
            Program program = new Program();
            program.setTitle(lista.get(i).getNombrePrograma());
            program.setStartDate(lista.get(i).getFechaInicio());
            program.setEndDate(lista.get(i).getFechaFin());
            program.setIdProgram(lista.get(i).getIdPrograma());
            program.setPChannel(new Channel());
            program.getPChannel().setChannelID(lista.get(i).getIdChannel());
            final Program p = program;

            if(i%2 == 0){

                LayoutInflater inflater = LayoutInflater.from(this);
                View channel = inflater.inflate(R.layout.slide_menu_trending_channel, null);

                aq = new AQuery(channel);
                TextView name = (TextView) channel.findViewById(R.id.menu_nombre_canal);
                TextView position = (TextView) channel.findViewById(R.id.menu_posicion);

                ImageView image = (ImageView) channel.findViewById(R.id.menu_foto_canal);
                ImageView topOne = (ImageView) channel.findViewById(R.id.top_one);

                if(i == 0)
                    topOne.setVisibility(View.VISIBLE);

                name.setText(lista.get(i).getNombreCanal());
                position.setText(lista.get(i).getPosicion());

                aq.id(image).image(lista.get(i).getFotoCanal());

                linearLayout.addView(channel);
                channel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //hideSlideMenu();
                        hideSlideMenu2(p);
                    }
                });
            }
            else{
                LayoutInflater inflater = LayoutInflater.from(this);
                View channel = inflater.inflate(R.layout.slide_menu_trending_channel_2, null);

                aq = new AQuery(channel);
                TextView name = (TextView) channel.findViewById(R.id.menu_nombre_canal);
                TextView position = (TextView) channel.findViewById(R.id.menu_posicion);

                ImageView image = (ImageView) channel.findViewById(R.id.menu_foto_canal);

                name.setText(lista.get(i).getNombreCanal());
                position.setText(lista.get(i).getPosicion());

                aq.id(image).image(lista.get(i).getFotoCanal());
                linearLayout.addView(channel);
                channel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        hideSlideMenu2(p);
                    }
                });
            }
        }
    }
    private void createActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        final LayoutInflater inflater = LayoutInflater.from(this);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        View view = inflater.inflate(R.layout.action_bar, null);

        search = (EditText) view.findViewById(R.id.item_buscar);
        iconSearch = (ImageView) view.findViewById(R.id.icon_search);

        menu = (ImageButton)view.findViewById(R.id.item_menu);

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showSearch){
                    showSearch = false;
                    search.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Buscando ...",Toast.LENGTH_LONG).show();
                }
                else{
                    showSearch = true;
                    search.setVisibility(View.VISIBLE);
                    ManagerAnimation.visibilityRight(search);
                }

            }
        });

        final ImageButton configuracion = (ImageButton) view.findViewById(R.id.item_configuracion);
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ManagerAnimation.selection(configuracion);

                if (isConfiguration) {

                    isConfiguration = false;
                    isMessage = false;
                    isNotification = false;

                    contenedorActionbarOption.removeAllViews();
                    contenedorMenuBar.removeAllViews();

                } else {

                    isConfiguration = true;
                    isMessage = false;
                    isNotification = false;

                    contenedorMenuBar.removeAllViews();
                    View containerConfiguration = inflater.inflate(R.layout.action_bar_configuration, null, false);
                    Typeface light = Typeface.createFromAsset(getAssets(), "fonts/SegoeWP.ttf");
                    Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/SegoeWP-Bold.ttf");

                    final RelativeLayout r1 = (RelativeLayout) containerConfiguration.findViewById(R.id.config_auto_post);
                    final RelativeLayout r2 = (RelativeLayout) containerConfiguration.findViewById(R.id.config_tutorial);
                    final RelativeLayout r3 = (RelativeLayout) containerConfiguration.findViewById(R.id.config_acerca_de);
                    final RelativeLayout r4 = (RelativeLayout) containerConfiguration.findViewById(R.id.confif_terminos_y_condiciones);

                    TextView txtAutoPost = (TextView) containerConfiguration.findViewById(R.id.auto_post_principal);
                    TextView txtTutorial = (TextView) containerConfiguration.findViewById(R.id.tutorial_principal);
                    TextView txtAcercaDe = (TextView) containerConfiguration.findViewById(R.id.acerca_de_principal);
                    TextView txtTerminos = (TextView) containerConfiguration.findViewById(R.id.termino_principal);

                    final TextView txtAutoPost2 = (TextView) containerConfiguration.findViewById(R.id.auto_post_secundario);
                    TextView txtTutorial2 = (TextView) containerConfiguration.findViewById(R.id.tutorial_secundario);
                    TextView txtAcercaDe2 = (TextView) containerConfiguration.findViewById(R.id.acerca_de_secundario);
                    TextView txtTerminos2 = (TextView) containerConfiguration.findViewById(R.id.termino_secundario);

                    txtAcercaDe.setTypeface(bold);
                    txtAutoPost.setTypeface(bold);
                    txtTerminos.setTypeface(bold);
                    txtTutorial.setTypeface(bold);

                    txtAcercaDe2.setTypeface(light);
                    txtAutoPost2.setTypeface(light);
                    txtTerminos2.setTypeface(light);
                    txtTutorial2.setTypeface(light);

                    final ImageView fb = (ImageView) r1.findViewById(R.id.fb_active);
                    if(!fbActivate){
                        fb.setAlpha((float)0.3);
                        txtAutoPost2.setText("Activa tu post en Facebook");
                    }
                    //final Drawable noFb = getResources().getDrawable(R.drawable.fb_logo_blue_50);
                    //final Drawable siFb = getResources().getDrawable(R.drawable.fb_logo_blue_50_active);

                    ImageView exit = (ImageView) containerConfiguration.findViewById(R.id.exit);

                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isConfiguration = false;
                            isMessage = false;
                            isNotification = false;
                            contenedorActionbarOption.removeAllViews();
                        }
                    });

                    r1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ManagerAnimation.selection(r1);
                            AnimatorSet set = new AnimatorSet();

                            if(fbActivate){
                                set.playTogether(
                                        ObjectAnimator.ofFloat(fb, "alpha", 0.3f),
                                        ObjectAnimator.ofFloat(fb, "scaleX", 1, 1.3f),
                                        ObjectAnimator.ofFloat(fb, "scaleY", 1, 1.3f));
                                set.setDuration(400);
                                set.start();
                                set.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        AnimatorSet aux = new AnimatorSet();
                                        aux.playTogether(
                                                ObjectAnimator.ofFloat(fb, "scaleX", 1.3f, 1f),
                                                ObjectAnimator.ofFloat(fb, "scaleY", 1.3f, 1f));
                                        aux.setDuration(400);
                                        aux.start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                                fbActivate = false;
                                txtAutoPost2.setText("Activa tu post en Facebook");
                                userNunchee.isFacebookActive = fbActivate;
                                dataBaseUser.updateFacebookActive(UserPreference.getIdFacebook(getApplicationContext()), userNunchee);

                            }
                            else{

                                set.playTogether(
                                        ObjectAnimator.ofFloat(fb, "alpha",  1f),
                                        ObjectAnimator.ofFloat(fb, "scaleX", 1, 1.3f),
                                        ObjectAnimator.ofFloat(fb, "scaleY", 1, 1.35f));
                                set.setDuration(400);
                                set.start();
                                set.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        AnimatorSet aux = new AnimatorSet();
                                        aux.playTogether(
                                                ObjectAnimator.ofFloat(fb, "scaleX", 1.3f, 1f),
                                                ObjectAnimator.ofFloat(fb, "scaleY", 1.3f, 1f));
                                        aux.setDuration(400);
                                        aux.start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {
                                    }
                                });
                                fbActivate = true;
                                txtAutoPost2.setText("Desactiva tu post en Facebook");
                                //UserPreference.setFacebookActive(fbActivate,getApplicationContext());
                                userNunchee.isFacebookActive = fbActivate;
                                dataBaseUser.updateFacebookActive(UserPreference.getIdFacebook(getApplicationContext()),userNunchee);
                            }

                        }
                    });
                    r2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ManagerAnimation.selection(r2);

                        }
                    });

                    r3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ManagerAnimation.selection(r3);
                            contenedorLoading.removeAllViews();
                            contenedorActionbarOption.removeAllViews();
                            contenedorMenuBar.removeAllViews();
                            isConfiguration = false;
                            isMessage = false;
                            isNotification = false;

                            About fg = new About();
                            fg.show(getSupportFragmentManager(),"");
                        }
                    });

                    r4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ManagerAnimation.selection(r4);
                            contenedorLoading.removeAllViews();
                            contenedorActionbarOption.removeAllViews();
                            contenedorMenuBar.removeAllViews();
                            isConfiguration = false;
                            isMessage = false;
                            isNotification = false;

                            //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                            Politica fg = new Politica();

                            /*ft.addToBackStack(null);
                            ft.replace(R.id.contenedor_action_bar, fg);
                            ft.commit();*/

                            fg.show(getSupportFragmentManager(),"");
                        }
                    });

                    contenedorActionbarOption.removeAllViews();
                    contenedorActionbarOption.addView(containerConfiguration);
                }
            }
        });
        ImageView imageProfile = (ImageView) view.findViewById(R.id.foto_perfil_actionbar);
        final ImageButton mensajes = (ImageButton) view.findViewById(R.id.item_mensajes);
        mensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // ManagerAnimation.selection(mensajes);
                if (isMessage) {

                    isConfiguration = false;
                    isNotification = false;
                    isMessage = false;
                    contenedorActionbarOption.removeAllViews();
                    contenedorMenuBar.removeAllViews();

                } else {

                    isConfiguration = false;
                    isNotification = false;
                    isMessage = true;

                    View containerMessage = inflater.inflate(R.layout.action_bar_message, null, false);
                    contenedorActionbarOption.removeAllViews();
                    contenedorMenuBar.removeAllViews();
                    contenedorActionbarOption.addView(containerMessage);
                    /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    MessageFragment dialogError = new MessageFragment();
                    ft.replace(R.id.contenedor_preview, dialogError);
                    ft.commit();*/
                }
            }
        });

        final ImageButton notificacion = (ImageButton) view.findViewById(R.id.item_notificaciones);
        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ManagerAnimation.selection(notificacion);
                if (isNotification) {

                    isConfiguration = false;
                    isMessage = false;
                    isNotification = false;
                    contenedorActionbarOption.removeAllViews();
                    contenedorMenuBar.removeAllViews();
                    /*RelativeLayout r = (RelativeLayout) findViewById(R.id.contenedor_preview);
                    r.removeAllViews();*/
                } else {

                    isNotification = true;
                    isConfiguration = false;
                    isMessage = false;

                    //View containerNotification = inflater.inflate(R.layout.action_bar_notification, null, false);
                    //contenedorActionbarOption.removeAllViews();
                    //ontenedorActionbarOption.addView(containerNotification);

                    contenedorActionbarOption.removeAllViews();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    NotificationFragment dialogError = new NotificationFragment();
                    ft.replace(R.id.contenedor_menu_bar, dialogError);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            }
        });

        contenedorMenu = (RelativeLayout) findViewById(R.id.contenedor_menu);

        AQuery aq = new AQuery(view);
        aq.id(imageProfile).image("http://graph.facebook.com/" + UserPreference.getIdFacebook(ProgramationActivity.this)
                + "/picture?type=square");

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSlideMenuOpen) {
                    hideSlideMenu();
                }
                else {
                    showSlideMenu();
                }
            }
        });

        // Menu
        final Button recomendado = (Button) findViewById(R.id.btn_recomendado);
        final Button historial = (Button) findViewById(R.id.btn_historial);
        final Button salir = (Button) findViewById(R.id.btn_salir);
        final Button favoritos = (Button) findViewById(R.id.btn_favoritos);

        historial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideSlideMenu();
                loading();
                final DataLoader dataLoader = new DataLoader(ProgramationActivity.this);
                dataLoader.friendsFacebookNunchee(new DataLoader.DataLoadedHandler<UserFacebook>() {
                    @Override
                    public void loaded(List<UserFacebook> data) {

                        friends = data;
                        dataLoader.feed(new DataLoader.DataLoadedHandler<FeedJSON>() {
                            @Override
                            public void loaded(List<FeedJSON> dataFeed) {

                                FeedFragment fragmentoFeed = new FeedFragment(dataFeed);
                                fragmentoFeed.show(getSupportFragmentManager(),"");
                                borraLoading();
                            }

                            @Override
                            public void error(String error) {
                                super.error(error);
                                Log.e("Program Activity error", " Historial error --> " + error);
                                borraLoading();

                                DialogError dialogError = new DialogError();
                                dialogError.show(getSupportFragmentManager(),"");

                            }
                        }, getIdFriends());
                    }

                    @Override
                    public void error(String error) {
                        borraLoading();
                        super.error(error);
                        Log.e("Program Activity error", " Historial(amiigos Face) error --> " + error);

                    }
                });
            }
        });

        favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ManagerAnimation.selection(favoritos);
                hideSlideMenu();
                loading();
                DataLoader dataLoader = new DataLoader(getApplication());
                dataLoader.getFavoriteTodos(new DataLoader.DataLoadedHandler<Program>() {
                    @Override
                    public void loaded(List<Program> data) {
                        for(int i = 0;i < data.size();i++){
                            Log.e("Nombre Favorito",data.get(i).Title);
                        }
                        FavoriteMenuFragment favoritosFragmento = new FavoriteMenuFragment(data);
                        favoritosFragmento.show(getSupportFragmentManager(),"");
                        borraLoading();
                    }

                    @Override
                    public void error(String error) {
                        super.error(error);
                        Log.e("Program Activity error", " Favoritos error --> " + error);
                        borraLoading();
                        DialogError dialogError = new DialogError();
                        dialogError.show(getSupportFragmentManager(), "");
                    }
                }, UserPreference.getIdNunchee(getApplication()));
            }
        });

        recomendado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ManagerAnimation.selection(recomendado);
                Intent i = new Intent(ProgramationActivity.this, RecommendedActivity.class);
                startActivity(i);
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ManagerAnimation.selection(salir);
                Session.getActiveSession().closeAndClearTokenInformation();
                Intent i = new Intent(ProgramationActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        actionBar.setCustomView(view,layout);

    }
    public List<String> getIdFriends(){
        List<String> lista = new ArrayList<String>();
        for (UserFacebook friend : friends) lista.add(friend.getIdUsuario());
        return lista;
    }
    public void loading(){

        viewLoading = inflaterPrivate.inflate(R.layout.progress_dialog_pop_corn, null);
        ImageView imgPopCorn = (ImageView) viewLoading.findViewById(R.id.pop_corn_centro);
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Animation animaPop = AnimationUtils.loadAnimation(getApplication(), R.anim.animacion_pop_hacia_derecha_centro);

        viewLoading.setLayoutParams(params);
        imgPopCorn.startAnimation(animaPop);

        contenedorLoading.addView(viewLoading);
        viewLoading.bringToFront();

        Log.e("Loading","Loading");

        contenedorLoading.bringToFront();
        contenedorLoading.setEnabled(false);

    }
    public void borraLoading(){

        Animation anim = AnimationUtils.loadAnimation(getApplication(),R.anim.deaparece);

        viewLoading.startAnimation(anim);
        contenedorLoading.removeView(viewLoading);
        contenedorLoading.setEnabled(true);
        //scrollPreview.setVisibility(View.VISIBLE);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void hideSlideMenu(){

        float slideMenuPosition;
        float menuButtonPosition;
        float searchViewPosition;
        float searchButtonPosition;

        slideMenuPosition = -290.0f;
        menuButtonPosition = 20.0f;
        searchViewPosition = menuButtonPosition + menu.getWidth()+ 35;
        searchButtonPosition = searchViewPosition + search.getWidth() + 15;

        isSlideMenuOpen = false;
        contenedorMenu.setVisibility(View.GONE);

        View slideMenuContainer = findViewById(R.id.menu_principal);

        ObjectAnimator animatorSlideMenu = ObjectAnimator.ofFloat(slideMenuContainer,"x",slideMenuPosition);
        ObjectAnimator animatorMenuButton = ObjectAnimator.ofFloat(menu,"x",menuButtonPosition);

        ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(search, "x", searchViewPosition);
        ObjectAnimator animatorSearchButton = ObjectAnimator.ofFloat(iconSearch, "x", searchButtonPosition);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animatorSlideMenu, animatorSearch, animatorMenuButton , animatorSearchButton);
        animSet.start();

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void hideSlideMenu2(final Program p){

        float slideMenuPosition;
        float menuButtonPosition;
        float searchViewPosition;
        float searchButtonPosition;

        slideMenuPosition = -290.0f;
        menuButtonPosition = 20.0f;

        searchViewPosition = menuButtonPosition + menu.getWidth()+ 35;
        searchButtonPosition = searchViewPosition + search.getWidth() + 15;

        isSlideMenuOpen = false;
        contenedorMenu.setVisibility(View.GONE);

        View slideMenuContainer = findViewById(R.id.menu_principal);

        ObjectAnimator animatorSlideMenu = ObjectAnimator.ofFloat(slideMenuContainer,"x",slideMenuPosition);
        ObjectAnimator animatorMenuButton = ObjectAnimator.ofFloat(menu,"x",menuButtonPosition);
        ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(search, "x", searchViewPosition);
        ObjectAnimator animatorSearchButton = ObjectAnimator.ofFloat(iconSearch, "x", searchButtonPosition);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.playTogether(animatorSlideMenu, animatorSearch, animatorMenuButton , animatorSearchButton);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                RelativeLayout r = (RelativeLayout) findViewById(R.id.view_parent);
                Bitmap screenShot = ScreenShot.takeScreenshot(r);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screenShot.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                byte[] byteArray = stream.toByteArray();

                try {
                    String filename = getCacheDir()
                            + File.separator + System.currentTimeMillis() + ".jpg";

                    File f = new File(filename);
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(byteArray);
                    fo.close();

                    Intent i = new Intent(ProgramationActivity.this, PreviewActivity.class);
                    i.putExtra("background", filename);
                    i.putExtra("programa", p);
                    startActivity(i);
                    overridePendingTransition(R.anim.zoom_in_preview, R.anim.nada);
                    //overridePendingTransition(R.anim.zoom_in_preview, R.anim.nada);
                    //context.startActivity(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSet.start();

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showSlideMenu(){

        float slideMenuPosition;
        float menuButtonPosition;
        float searchViewPosition;
        float searchButtonPosition;

        slideMenuPosition =  0.0f;
        menuButtonPosition = 258.0f;
        searchViewPosition = menuButtonPosition + menu.getWidth() + 35;
        searchButtonPosition = searchViewPosition + search.getWidth() + 15;

        View slideMenuContainer = findViewById(R.id.menu_principal);

        ObjectAnimator animatorSlideMenu = ObjectAnimator.ofFloat(slideMenuContainer,"x",slideMenuPosition);
        ObjectAnimator animatorMenuButton = ObjectAnimator.ofFloat(menu,"x",menuButtonPosition);
        ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(search, "x", searchViewPosition);
        ObjectAnimator animatorSearchButton = ObjectAnimator.ofFloat(iconSearch, "x", searchButtonPosition);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animatorSlideMenu, animatorSearch, animatorMenuButton , animatorSearchButton);
        animSet.start();

        contenedorMenu.setVisibility(View.VISIBLE);
        contenedorMenu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSlideMenu();
            }
        });

        isSlideMenuOpen = true;
    }

    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
