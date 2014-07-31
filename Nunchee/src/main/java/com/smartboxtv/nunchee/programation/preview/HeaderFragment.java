package com.smartboxtv.nunchee.programation.preview;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.data.image.Type;
import com.smartboxtv.nunchee.data.image.Width;
import com.smartboxtv.nunchee.data.models.Image;
import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.programation.delegates.PreviewImageFavoriteDelegate;

import java.text.SimpleDateFormat;

/**
 * Created by Esteban- on 06-05-14.
 */
public class HeaderFragment extends Fragment {

    private final Program programa;
    private final Program programaHora;

    private TextView txtName;
    private TextView txtDate;
    private TextView txtDescription;
    private TextView txtChannel;

    private ImageView imgFavorite;

    private SimpleDateFormat formatHora;
    private SimpleDateFormat formatDia;

    private AQuery aq;

    // Un delegado cualquiera
    private PreviewImageFavoriteDelegate imageFavoriteDelegate;

    public HeaderFragment(Program program, Program previewProgram) {
        this.programa = program;
        this.programaHora = previewProgram;
    }
    // det del delegado cualquiera
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.preview_fg_header, container, false);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH$mm$ss");
        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Bold.ttf");
        Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Light.ttf");

        formatHora = new SimpleDateFormat("HH:mm");
        formatDia = new SimpleDateFormat("MMM dd");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        aq = new AQuery(rootView);

        // TextView
        txtName = (TextView) rootView.findViewById(R.id.preview_nombre);
        txtDate = (TextView) rootView.findViewById(R.id.preview_hora);
        txtDescription = (TextView) rootView.findViewById(R.id.preview_descripcion);
        txtChannel = (TextView) rootView.findViewById(R.id.preview_nombre_canal);

        txtChannel.setTypeface(normal);
        txtName.setTypeface(bold);
        txtDescription.setTypeface(light);
        txtDate.setTypeface(normal);

        // ImageView
        ImageView imgChannel = (ImageView) rootView.findViewById(R.id.preview_foto_canal);
        ImageView imgProgram = (ImageView) rootView.findViewById(R.id.preview_cabeza_foto);
        imgFavorite = (ImageView) rootView.findViewById(R.id.preview_image_favorito);
        imgFavorite.setVisibility(View.GONE);

        setData();

        return rootView;
    }
    public void muestraImagen(boolean muestaImgen){

        if(muestaImgen){
            imgFavorite.setVisibility(View.VISIBLE);
        }
        else{
            imgFavorite.setVisibility(View.GONE);
        }

    }

    public void setData(){

        txtName.setText(programa.getTitle());

        txtDate.setText(capitalize(formatDia.format(programaHora.getStartDate())) + ", "
                + formatHora.format(programaHora.getStartDate()) + " | " + formatHora.format(programaHora.getEndDate()));


        txtDescription.setText(programa.getDescription());

        txtChannel.setText(programa.getPChannel().getChannelCallLetter() + " " + programa.getPChannel().getChannelNumber());


        Image image  = programa.getImageWidthType(Width.ORIGINAL_IMAGE, Type.BACKDROP_IMAGE);
        if(image != null){
            aq.id(R.id.preview_cabeza_foto).image(image.getImagePath());
        }

        aq.id(R.id.preview_foto_canal).image(programa.getPChannel().getChannelImageURLLight());
        if(programa.isFavorite()){
            imgFavorite.setVisibility(View.VISIBLE);
        }
    }

    public void setImageFavoriteDelegate(PreviewImageFavoriteDelegate imageFavoriteDelegate) {
        this.imageFavoriteDelegate = imageFavoriteDelegate;
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);

    }
}
