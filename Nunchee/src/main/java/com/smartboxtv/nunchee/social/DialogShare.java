package com.smartboxtv.nunchee.social;


import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smartboxtv.nunchee.R;

/**
 * Created by Esteban- on 31-05-14.
 */
public class DialogShare extends DialogFragment {

    private String text;
    private String imageUrl;
    private String title;
    private String url;
    private String description;

    public DialogShare() {
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    public DialogShare(String text, String imageUrl, String title, String url) {
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, getTheme());
        this.description = text;
        this.imageUrl = imageUrl;
        this.title = title;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.facebook_compose, container, false);

        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Bold.ttf");


        TextView txtTitle  = (TextView) rootView.findViewById(R.id.header_textview);
        Button button = (Button) rootView.findViewById(R.id.post_button);

        txtTitle.setTypeface(bold);
        button.setTypeface(normal);

        AQuery aq = new AQuery(rootView);
        aq.id(R.id.image_preview).image(this.imageUrl);

        final EditText editText = (EditText)rootView.findViewById(R.id.post_edittext);
        //editText.setText(this.text);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText() != null){

                    SocialUtil socialUtil = new SocialUtil(getActivity());
                    socialUtil.fbshare(getActivity(), editText.getText().toString(), imageUrl ,url ,title,description, new SocialUtil.SocialUtilHandler() {
                        @Override
                        public void done(Exception e) {
                            dismiss();
                        }
                    });
                }
             }

        });
        return rootView;
    }
}
