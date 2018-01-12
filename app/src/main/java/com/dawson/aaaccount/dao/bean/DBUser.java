package com.dawson.aaaccount.dao.bean;

import com.dawson.aaaccount.bean.User;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Dawson on 2017/7/12.
 */
@Entity
public class DBUser {
    @Id
    private String id;

    /**
     * 最后修改时间
     */
    private Date lastModifiedTime;
    private int number;
    private String name;

    @Generated(hash = 471061171)
    public DBUser(String id, Date lastModifiedTime, int number, String name) {
        this.id = id;
        this.lastModifiedTime = lastModifiedTime;
        this.number = number;
        this.name = name;
    }

    @Generated(hash = 138933025)
    public DBUser() {
    }

    @Keep
    public boolean equalsToUser(User obj) {
        if (obj == null) return false;
        return id.equals(obj.getId());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
