package com.smartboxtv.nunchee.programation.menu;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.smartboxtv.nunchee.R;

/**
 * Created by Esteban- on 22-05-14.
 */
public class Politica extends DialogFragment {

    private final String URL = "http://nunchee.tv/privacidad.html";

    public Politica() {

        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.politica_privacidad, container,false);

        WebView  webView = (WebView) rootView.findViewById(R.id.webView);
        //webView = new WebView(getActivity());

        webView.loadUrl(URL);

        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return rootView;
    }
}
