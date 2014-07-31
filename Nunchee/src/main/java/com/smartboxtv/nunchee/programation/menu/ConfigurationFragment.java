package com.smartboxtv.nunchee.programation.menu;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.programation.delegates.MenuConfigurationDelegate;

/**
 * Created by Esteban- on 09-05-14.
 */
public class ConfigurationFragment extends Fragment {

    private MenuConfigurationDelegate configurationDelegate;

    public ConfigurationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.action_bar_configuration ,container, false);

        ImageView exit = (ImageView) (rootView != null ? rootView.findViewById(R.id.exit) : null);
        Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Bold.ttf");

        RelativeLayout r1 = (RelativeLayout) rootView.findViewById(R.id.config_auto_post);
        RelativeLayout r2 = (RelativeLayout) rootView.findViewById(R.id.config_tutorial);
        RelativeLayout r3 = (RelativeLayout) rootView.findViewById(R.id.config_acerca_de);
        RelativeLayout r4 = (RelativeLayout) rootView.findViewById(R.id.confif_terminos_y_condiciones);

        TextView txtAutoPost = (TextView) rootView.findViewById(R.id.auto_post_principal);
        TextView txtTutorial = (TextView) rootView.findViewById(R.id.tutorial_principal);
        TextView txtAcercaDe = (TextView) rootView.findViewById(R.id.acerca_de_principal);
        TextView txtTerminos = (TextView) rootView.findViewById(R.id.termino_principal);

        TextView txtAutoPost2 = (TextView) rootView.findViewById(R.id.auto_post_secundario);
        TextView txtTutorial2 = (TextView) rootView.findViewById(R.id.tutorial_secundario);
        TextView txtAcercaDe2 = (TextView) rootView.findViewById(R.id.acerca_de_secundario);
        TextView txtTerminos2 = (TextView) rootView.findViewById(R.id.termino_secundario);

        txtAcercaDe.setTypeface(bold);
        txtAutoPost.setTypeface(bold);
        txtTerminos.setTypeface(bold);
        txtTutorial.setTypeface(bold);

        txtAcercaDe2.setTypeface(light);
        txtAutoPost2.setTypeface(light);
        txtTerminos2.setTypeface(light);
        txtTutorial2.setTypeface(light);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout r = (RelativeLayout) getActivity().findViewById(R.id.contenedor_preview);
                r.removeAllViews();


                if(configurationDelegate != null){
                    configurationDelegate.changedState(false);
                }
            }
        });
        return rootView;
    }

    public void setConfigurationDelegate(MenuConfigurationDelegate configurationDelegate) {
        this.configurationDelegate = configurationDelegate;
    }
}
