package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Dawson on 2017/7/7.
 */
@Entity
public class JoinDayBookToUser {
    @Id
    private Long id;

    private String uid;

    private String did;

    public String getDid() {
        return this.did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1110049367)
    public JoinDayBookToUser(Long id, String uid, String did) {
        this.id = id;
        this.uid = uid;
        this.did = did;
    }

    @Generated(hash = 1007154772)
    public JoinDayBookToUser() {
    }
}
