package com.smartboxtv.nunchee.programation.delegates;

import java.util.Date;

/**
 * Created by Esteban- on 21-04-14.
 */
public abstract class BarDayDelegate {

    public abstract void updateDayBar(Date date, boolean right);                                                    // Actualiza la barra de dia

    public abstract void showButtonNow(boolean show, boolean isActive);                                             //Determina si muestra el boton de "AHORA"

    public abstract  void updateLimit(Date limitTop, Date limitBottom,Date firstTop,Date firstBottom);              //Actualiza los limites del bloque en vista

    public abstract void updateFlag( boolean b);                                                                    //Entrega Variable que verifica si hace scroll o carga más progamas

    public abstract  void updatePositionBar(Date d);
}
