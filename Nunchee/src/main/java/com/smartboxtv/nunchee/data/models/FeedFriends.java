package com.smartboxtv.nunchee.data.models;

import com.smartboxtv.nunchee.services.DataMember;

/**
 * Created by Esteban- on 18-04-14.
 */
public class FeedFriends {

    private String idUserFacebook;

    public FeedFriends() {

    }
    @DataMember( member = "0")
    public String getIdUserFacebook() {
        return idUserFacebook;
    }
    @DataMember( member = "0")
    public void setIdUserFacebook(String idUserFacebook) {
        this.idUserFacebook = idUserFacebook;
    }
}
