package com.smartboxtv.nunchee.delgates;

import com.smartboxtv.nunchee.data.models.Program;
import com.smartboxtv.nunchee.fragments.RecommendedFragment;

/**
 * Created by Esteban- on 19-04-14.
 */
public abstract class RecommendedDelegate {

    public abstract void like(Program p, RecommendedFragment fragment);
    public abstract void dislike(Program p, RecommendedFragment fragment);

}
