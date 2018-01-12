package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * Created by Dawson on 2017/8/17.
 * 系统日志
 */
@Entity
public class DBSystemLog {
    @Id
    public Long id;

    private String title;

    private String cotent;

    private Date createTime;

    public String getCotent() {
        return this.cotent;
    }

    public void setCotent(String cotent) {
        this.cotent = cotent;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Generated(hash = 1496450363)
    public DBSystemLog(Long id, String title, String cotent, Date createTime) {
        this.id = id;
        this.title = title;
        this.cotent = cotent;
        this.createTime = createTime;
    }

    @Generated(hash = 484051216)
    public DBSystemLog() {
    }
}
