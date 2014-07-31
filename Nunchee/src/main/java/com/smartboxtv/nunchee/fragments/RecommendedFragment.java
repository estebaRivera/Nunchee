package com.smartboxtv.nunchee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smartboxtv.nunchee.data.clean.DataClean;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.data.preference.UserPreference;
import com.smartboxtv.nunchee.delgates.RecommendedDelegate;
import com.smartboxtv.nunchee.services.DataLoader;

/**
 * Created by Esteban- on 18-04-14.
 */
public class RecommendedFragment extends Fragment {

    private Animation translate;
    private Animation zoomPress;
    private Program programa;
    private Button buttonDislike;
    private Button buttonLike;

    private RelativeLayout relative;
    private RecommendedDelegate delegate;

    public RecommendedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recommended_fragment, container, false);
        translate = AnimationUtils.loadAnimation(getActivity(), R.anim.derecha_bounce);
        zoomPress = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in_press);

        //Animation zoomNotPress = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in_not_press);

        buttonDislike = (Button) rootView.findViewById(R.id.btn_recomendacion_no);
        buttonLike = (Button) rootView.findViewById(R.id.btn_recomendacion_si);

        relative = (RelativeLayout) rootView.findViewById(R.id.sugerencia);

        buttonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(delegate!=null){

                    delegate.dislike(programa, RecommendedFragment.this);
                    relative.startAnimation(translate);

                    buttonDislike.startAnimation(zoomPress);
                    //buttonLike.startAnimation(zoomNotPress);

                }
            }
        });
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(delegate!=null){

                    DataLoader data = new DataLoader(getActivity());
                    data.actionLike(UserPreference.getIdNunchee(getActivity()), "2", programa.getIdProgram(), "-1");

                    delegate.like(programa, RecommendedFragment.this);
                    relative.startAnimation(translate);

                    buttonLike.startAnimation(zoomPress);
                    //buttonDislike.startAnimation(zoomNotPress);

                }
            }
        });
        return rootView;
    }

    public RecommendedDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(RecommendedDelegate delegate) {
        this.delegate = delegate;
    }

    public void setData(Program p){

        if(p == null){
            return;
        }

        this.programa = p;

        TextView texto = (TextView) getView().findViewById(R.id.nombre_recomendacion);
        texto.setText(p.getTitle());

        AQuery aq = new AQuery(getView());
        aq.id(R.id.foto_recomendacion).image(p.getListaImage().get(0).getImagePath());
    }
}
