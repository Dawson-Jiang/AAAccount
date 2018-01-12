package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Dawson on 2017/7/7.
 */
@Entity
public class JoinUserToFamily {
    @Id
    private Long id;

    private String uid;

    private String fid;

    public String getFid() {
        return this.fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
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

    @Generated(hash = 354621413)
    public JoinUserToFamily(Long id, String uid, String fid) {
        this.id = id;
        this.uid = uid;
        this.fid = fid;
    }

    @Generated(hash = 1291052616)
    public JoinUserToFamily() {
    }
}
