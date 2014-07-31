package com.smartboxtv.nunchee.programation.menu;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smartboxtv.nunchee.R;
import com.smartboxtv.nunchee.activities.PreviewActivity;
import com.smartboxtv.nunchee.data.database.DataBase;
import com.smartboxtv.nunchee.data.database.Reminder;
import com.smartboxtv.nunchee.data.image.ScreenShot;
import com.smartboxtv.nunchee.data.models.Channel;
import com.smartboxtv.nunchee.data.models.Program;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aurelienribon.tweenengine.equations.Linear;

/**
 * Created by Esteban- on 12-05-14.
 */
public class NotificationFragment extends Fragment {

    private List<Reminder> reminderList = new ArrayList<Reminder>();
    private List<Reminder> updateReminderList = new ArrayList<Reminder>();
    private View rootView;

    public NotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.action_bar_notification, container, false);
        DataBase dataBase = new DataBase(getActivity(),"",null,1);
        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP.ttf");
        Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeWP-Light.ttf");
        AQuery aq = new AQuery(rootView);

        reminderList =  dataBase.getReminder();
        Log.e("Cantidad de Reminder","--> "+reminderList.size());

        for(int i = 0 ;i < reminderList.size();i++){
            Log.e("Reminder name",reminderList.get(i).name);
            Log.e("Reminder id",reminderList.get(i).id);
            Log.e("Reminder id channel",reminderList.get(i).idChannel);
            Log.e("Reminder URL",reminderList.get(i).image);
            Log.e("Reminder fecha inicio", reminderList.get(i).strDate.replace(" ", ""));
            Log.e("Reminder fecha fin", reminderList.get(i).endDate.replace(" ", ""));
        }
        updateReminder();
        if(updateReminderList.size() > 0){
            RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.container_message);
            RelativeLayout n = (RelativeLayout) rootView.findViewById(R.id.no_notification);
            LinearLayout l = (LinearLayout) rootView.findViewById(R.id.list_notifications);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE HH:mm");
            LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r.setVisibility(View.VISIBLE);
            n.setVisibility(View.GONE);
            Log.e("update","reminder +1");
            LinearLayout[] list = new LinearLayout [updateReminderList.size()];
            for(int i = 0 ; i < updateReminderList.size();i++){

                final int finalI = i;
                list[i] = new LinearLayout(getActivity());
                View notification = inf.inflate(R.layout.element_notification, null);

                TextView name = (TextView) notification.findViewById(R.id.txt_nombre_notification);
                TextView date = (TextView) notification.findViewById(R.id.txt_fecha_notification);

                ImageView image = (ImageView) notification.findViewById(R.id.img_notifications);

                if(updateReminderList.get(i).image != null)
                    aq.id(image).image(updateReminderList.get(i).image);
                date.setTypeface(light);
                name.setTypeface(normal);
                name.setText(updateReminderList.get(i).getName());
                date.setText(dateFormat.format(new Date(Long.parseLong(updateReminderList.get(i).getStrDate().replace(" ","")))));

                notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Program p = new Program();
                        p.EndDate = new Date(Long.parseLong(updateReminderList.get(finalI).endDate.replace(" ", "")));
                        p.StartDate = new Date(Long.parseLong(updateReminderList.get(finalI).strDate.replace(" ","")));
                        p.IdProgram = updateReminderList.get(finalI).id;
                        p.PChannel = new Channel();
                        p.PChannel.channelID =updateReminderList.get(finalI).idChannel;

                        rootView.setVisibility(View.INVISIBLE);
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

                            Intent intent = new Intent(getActivity(), PreviewActivity.class);
                            intent.putExtra("background", filename);
                            intent.putExtra("programa",p );
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.zoom_in_preview, R.anim.nada);
                            rootView.setVisibility(View.VISIBLE);
                            onDestroy();
                            //context.startActivity(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                list[i].addView(notification);
                l.addView(list[i]);
            }

        }
        return rootView;
    }
    public void updateReminder(){
        long now = new Date().getTime();
        long starDate;
        Log.e("update","reminder");
        for(int i = 0 ;i < reminderList.size();i++){
            starDate = Long.parseLong(reminderList.get(i).getStrDate().replace(" ",""));
            if(starDate > now ){
                updateReminderList.add(reminderList.get(i));
            }
        }
    }
}
