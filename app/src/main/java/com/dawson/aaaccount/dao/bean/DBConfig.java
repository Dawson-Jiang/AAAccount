package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 配置
 * Created by Dawson on 2017/7/8.
 */
@Entity
public class DBConfig {
    @Id
    public Long id;

    public String key;

    public String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 22523606)
    public DBConfig(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 1773307525)
    public DBConfig() {
    }
}
