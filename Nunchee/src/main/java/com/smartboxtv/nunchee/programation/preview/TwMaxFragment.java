package com.smartboxtv.nunchee.programation.preview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.data.models.Tweets;
import com.smartboxtv.nunchee.data.models.UserTwitterJSON;
import com.smartboxtv.nunchee.data.preference.UserPreference;
import com.smartboxtv.nunchee.services.DataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esteban- on 06-05-14.
 */
public class TwMaxFragment extends Fragment {

    private Button btnTimeline;
    private Button btnRelacionado;
    private Button btnTwittear;

    private TextView titulo2;
    private EditText texto;

    private View rootView;
    private View tws;
    private View cargando;

    private final Program programa;
    private LinearLayout contenedorTws;
    private LayoutInflater inflaterPrivate;

    private int cuentaClick = 0;
    private boolean isTimeline = false;
    private RelativeLayout contenedorLoading;
    //private RelativeLayout containerNoTw;
    private List<UserTwitterJSON> listTimeLine = new ArrayList<UserTwitterJSON>();
    private List<Tweets> listaRelacionado = new ArrayList<Tweets>();
    private static final int CONFIRM_ALERT = 2;

    public TwMaxFragment(Program program) {
        this.programa = program;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.preview_fg_tw_max,container,false);
        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");

        inflaterPrivate = inflater;     listTimeLine = null;
        contenedorLoading = (RelativeLayout) rootView.findViewById(R.id.contenedor_loading);
        contenedorTws = (LinearLayout) rootView.findViewById(R.id.tws_lista);
        //containerNoTw = (RelativeLayout) rootView.findViewById(R.id.container_no_tw);

        TextView titulo = (TextView) rootView.findViewById(R.id.tw_titulo);
        titulo2 = (TextView) rootView.findViewById(R.id.tw_titulo_2);
        btnTimeline = (Button) rootView.findViewById(R.id.tw_btn_timeline);
        btnTwittear = (Button) rootView.findViewById(R.id.tw_twittear);
        texto = (EditText) rootView.findViewById(R.id.tw_tws);

        btnTimeline.setBackgroundResource(R.drawable.evento_tw);
        btnRelacionado = (Button) rootView.findViewById(R.id.tw_btn_relacionado);
        btnRelacionado.setBackgroundResource(R.drawable.evento_tw);

        btnRelacionado.setSelected(true);
        btnTimeline.setSelected(false);

        titulo.setTypeface(normal);
        titulo2.setTypeface(normal);

        btnTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading();
                btnTimeline.setSelected(true);
                btnRelacionado.setSelected(false);
                ViewGroup.LayoutParams timelineParams = btnTimeline.getLayoutParams();

                timelineParams.height = 40;
                btnTimeline.setLayoutParams(timelineParams);

                ViewGroup.LayoutParams relacionadoParams = btnRelacionado.getLayoutParams();
                relacionadoParams.height  = 35;

                btnRelacionado.setLayoutParams(relacionadoParams);
                titulo2.setText("#TIMELINE");

                if (btnTwittear.getVisibility() == View.GONE) {
                    btnTwittear.setVisibility(View.VISIBLE);
                }

                contenedorTws.removeAllViews();

                if (listTimeLine == null) {

                    DataLoader dataLoader = new DataLoader(getActivity());
                    dataLoader.getTimeLine(new DataLoader.DataLoadedHandler<UserTwitterJSON>() {

                        @Override
                        public void loaded(List<UserTwitterJSON> data) {

                            super.loaded(data);
                            listTimeLine = data;

                            cargaTwsTimeLine();
                            borraLoading();
                        }

                        @Override
                        public void error(String error) {
                            super.error(error);
                            Log.e("Cargar ", "" + error);
                        }
                    });
                } else {
                    cargaTwsTimeLine();
                    borraLoading();
                }
            }
        });
        btnRelacionado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading();
                btnTimeline.setSelected(false);
                btnRelacionado.setSelected(true);

                int width_screen = UserPreference.getWIDTH_SCREEN(getActivity());

                ViewGroup.LayoutParams timelineParams = btnTimeline.getLayoutParams();

                Log.e("Width","ancho de pantalla"+width_screen);

                timelineParams.height = 35;
                btnTimeline.setLayoutParams(timelineParams);
                ViewGroup.LayoutParams relacionadoParams = btnRelacionado.getLayoutParams();

                timelineParams.height = 40;
                btnRelacionado.setLayoutParams(relacionadoParams);
                titulo2.setText("#RELACIONADO");
                contenedorTws.removeAllViews();

                if (btnTwittear.getVisibility() == View.VISIBLE) {
                    btnTwittear.setVisibility(View.GONE);
                }
                if (texto.getVisibility() == View.VISIBLE) {
                    texto.setVisibility(View.GONE);
                }
                cargaRelacionado();
                borraLoading();

            }
        });
        btnTwittear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cuentaClick++;
                if (texto.getVisibility() == View.GONE)
                    texto.setVisibility(View.VISIBLE);
                else
                    texto.setVisibility(View.GONE);

                String hashTag = programa.getHashtags();
                texto.setText("Estoy viendo " + programa.getTitle() + " en " + programa.getPChannel().getChannelCallLetter()
                        + " por #Nunchee @Nunchee " + hashTag);

                if (cuentaClick == 2) {
                    cuentaClick = 0;

                    DataLoader dataLoader = new DataLoader(getActivity());
                    dataLoader.updateStatusTw(new DataLoader.DataLoadedHandler<String>() {
                        @Override
                        public void loaded(String data) {

                            showAlertDialog(CONFIRM_ALERT, getActivity(), "Nunchee", "Tweet creado exitosamente!",
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }, "OK");

                            texto.setVisibility(View.GONE);
                            btnTwittear.setEnabled(false);
                        }

                        @Override
                        public void error(String error) {

                            showAlertDialog(CONFIRM_ALERT, getActivity(), "Nunchee", "Oops! el tweet no ha podido ser creado",
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }, "OK");
                        }
                    }, texto.getText().toString());
                }
            }
        });
        cargaRelacionado();
        return rootView;
    }
    public void cargaTwsTimeLine(){

        AQuery aq = new AQuery(rootView);

        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Bold.ttf");

        if(listTimeLine.size() > 0){

            Log.e("Carga TimeLine","Carga tws");

            for (UserTwitterJSON aListTimeLine : listTimeLine) {

                tws = getLayoutInflater(null).inflate(R.layout.element_tweets, null);

                ImageView imageView = (ImageView) tws.findViewById(R.id.foto_tw);
                TextView tw = (TextView) tws.findViewById(R.id.tw_texto);
                TextView nombre = (TextView) tws.findViewById(R.id.tw_nombre);
                TextView usuario = (TextView) tws.findViewById(R.id.tw_nombre_usuario);

                // set Typeface
                nombre.setTypeface(bold);
                usuario.setTypeface(normal);
                tw.setTypeface(normal);

                aq.id(imageView).image(aListTimeLine.getUsuario().getUrlImagen());
                tw.setText(aListTimeLine.getTexto());

                nombre.setText(aListTimeLine.getUsuario().getUsuario());
                usuario.setText("@" + aListTimeLine.getUsuario().getUsuario());
                contenedorTws.addView(tws);
            }
        }
        else{
            //containerNoTw.setVisibility(View.VISIBLE);
        }
    }
    public void cargaRelacionado(){

        AQuery aq = new AQuery(rootView);

        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Bold.ttf");

        Log.e("Carga Relacionado","Carga tws");
        if(programa.getTweets().size() > 0){

            for(int i = 0 ;i < programa.getTweets().size();i++){

                tws = getLayoutInflater(null).inflate(R.layout.element_tweets,null);

                ImageView imageView = (ImageView) tws.findViewById(R.id.foto_tw);
                TextView tw = (TextView) tws.findViewById(R.id.tw_texto);
                TextView nombre = (TextView) tws.findViewById(R.id.tw_nombre);
                TextView usuario = (TextView) tws.findViewById(R.id.tw_nombre_usuario);

                // set Typeface
                nombre.setTypeface(bold);
                usuario.setTypeface(normal);
                tw.setTypeface(normal);

                aq.id(imageView).image(programa.getTweets().get(i).getUrlImagen());
                tw.setText(programa.getTweets().get(i).getTw());
                nombre.setText(programa.getTweets().get(i).getNombre());
                usuario.setText("@" + programa.getTweets().get(i).getNombreUsuario());
                contenedorTws.addView(tws);
            }
        }
        else{
            //containerNoTw.setVisibility(View.VISIBLE);
        }
    }

    public static void confirmationAlert(Context ctx, String title, String message,
                                         DialogInterface.OnClickListener callBack) {
        showAlertDialog(CONFIRM_ALERT, ctx, title, message, callBack, "OK");
    }
    public static void showAlertDialog(int alertType, Context ctx, String title, String message,
                                       DialogInterface.OnClickListener posCallback, String... buttonNames) {
        if ( title == null ) title = ctx.getResources().getString(R.string.app_name);
        if ( message == null ) message = "default message";

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title)
                .setMessage(message)
                        // false = pressing back button won't dismiss this alert
                .setCancelable(false)
                        // icon on the left of title
                .setIcon(android.R.drawable.ic_dialog_alert);

        switch (alertType) {
            case CONFIRM_ALERT:
                builder.setPositiveButton(buttonNames[0], posCallback);
                break;
        }

        builder.setNegativeButton(buttonNames [buttonNames.length - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    public void loading(){

        cargando = inflaterPrivate.inflate(R.layout.progress_dialog_pop_corn, null);
        ImageView popDerecha = (ImageView) cargando.findViewById(R.id.pop_corn_centro);

        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Animation animaPop = AnimationUtils.loadAnimation(getActivity(), R.anim.animacion_pop_hacia_derecha_centro);

        cargando.setLayoutParams(params);
        popDerecha.startAnimation(animaPop);
        contenedorLoading.addView(cargando);
        cargando.bringToFront();
        //Log.e("Loading","Loading");
        contenedorLoading.bringToFront();
        contenedorLoading.setEnabled(false);
    }

    public void borraLoading(){
        Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.deaparece);
        cargando.startAnimation(anim);
        contenedorLoading.removeView(cargando);
        contenedorLoading.setEnabled(true);
    }
}
