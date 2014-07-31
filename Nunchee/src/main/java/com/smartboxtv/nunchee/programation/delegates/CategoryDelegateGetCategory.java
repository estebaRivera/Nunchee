package com.smartboxtv.nunchee.programation.delegates;

import com.smartboxtv.nunchee.data.models.CategorieChannel;
import com.smartboxtv.nunchee.data.modelssm.CategorieChannelSM;

/**
 * Created by Esteban- on 20-04-14.
 */
public abstract class CategoryDelegateGetCategory {

    public abstract void  getCategory(CategorieChannel cc);

    public abstract  void getCategory(CategorieChannelSM cc);
}
