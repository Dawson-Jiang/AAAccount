package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.dawson.aaaccount.dao.DaoSession;
import com.dawson.aaaccount.dao.DBFamilyDao;
import com.dawson.aaaccount.dao.DBUserDao;

/**
 * Created by Dawson on 2017/7/12.
 */
@Entity
public class DBFamily {

    @Id
    private String id;

    /**
     * 最后修改时间
     */
    private Date lastModifiedTime;

    private int number;

    private String name;

    /**
     * 是否临时家庭
     */
    private boolean isTemp;

    /**
     * 家庭成员
     */
    @ToMany
    @JoinEntity(entity = JoinUserToFamily.class, sourceProperty = "fid", targetProperty = "uid")
    private List<DBUser> members;
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1358688666)
    public synchronized void resetMembers() {
        members = null;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 800112220)
    public List<DBUser> getMembers() {
        if (members == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBUserDao targetDao = daoSession.getDBUserDao();
            List<DBUser> membersNew = targetDao._queryDBFamily_Members(id);
            synchronized (this) {
                if(members == null) {
                    members = membersNew;
                }
            }
        }
        return members;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 954696771)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBFamilyDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 776317047)
    private transient DBFamilyDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
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
    @Generated(hash = 1151055126)
    public DBFamily(String id, Date lastModifiedTime, int number, String name, boolean isTemp) {
        this.id = id;
        this.lastModifiedTime = lastModifiedTime;
        this.number = number;
        this.name = name;
        this.isTemp = isTemp;
    }
    @Generated(hash = 1284632546)
    public DBFamily() {
    }
    public boolean getIsTemp() {
        return this.isTemp;
    }
    public void setIsTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }
}
