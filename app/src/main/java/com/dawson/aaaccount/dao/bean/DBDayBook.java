package com.dawson.aaaccount.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.dawson.aaaccount.dao.DaoSession;
import com.dawson.aaaccount.dao.DBDayBookDao;
import com.dawson.aaaccount.dao.DBUserDao;
import com.dawson.aaaccount.dao.DBFamilyDao;

/**
 * 账单
 * Created by Dawson on 2017/7/12.
 */
@Entity
public class DBDayBook {
    @Id
    private String id;

    /**
     * 最后修改时间
     */
    private Date lastModifiedTime;
    /**
     * 消费金额
     */
    private double money;


    private String creatorId;
    /**
     * 创建人
     */
    @ToOne(joinProperty = "creatorId")
    private DBUser creator;

    private String familyId;
    /**
     * 所属家庭 null表示属于creator
     */
    @ToOne(joinProperty = "familyId")
    private DBFamily family;

    private String payerId;
    /**
     * 付款人
     */
    @ToOne(joinProperty = "payerId")
    private DBUser payer;

    /**
     * 消费人员
     */
    @ToMany
    @JoinEntity(entity = JoinDayBookToUser.class, sourceProperty = "did", targetProperty = "uid")
    private List<DBUser> customers;

    /**
     * 消费日期
     */
    private Date date;


    /**
     * 是否结算 0未结算 1已经结算
     */
    private int settled;

    private String settleId;

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
    @Generated(hash = 608715293)
    public synchronized void resetCustomers() {
        customers = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 129615476)
    public List<DBUser> getCustomers() {
        if (customers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBUserDao targetDao = daoSession.getDBUserDao();
            List<DBUser> customersNew = targetDao._queryDBDayBook_Customers(id);
            synchronized (this) {
                if(customers == null) {
                    customers = customersNew;
                }
            }
        }
        return customers;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 230303589)
    public void setPayer(DBUser payer) {
        synchronized (this) {
            this.payer = payer;
            payerId = payer == null ? null : payer.getId();
            payer__resolvedKey = payerId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1038329193)
    public DBUser getPayer() {
        String __key = this.payerId;
        if (payer__resolvedKey == null || payer__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBUserDao targetDao = daoSession.getDBUserDao();
            DBUser payerNew = targetDao.load(__key);
            synchronized (this) {
                payer = payerNew;
                payer__resolvedKey = __key;
            }
        }
        return payer;
    }

    @Generated(hash = 2011925552)
    private transient String payer__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 81233452)
    public void setFamily(DBFamily family) {
        synchronized (this) {
            this.family = family;
            familyId = family == null ? null : family.getId();
            family__resolvedKey = familyId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 882807347)
    public DBFamily getFamily() {
        String __key = this.familyId;
        if (family__resolvedKey == null || family__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBFamilyDao targetDao = daoSession.getDBFamilyDao();
            DBFamily familyNew = targetDao.load(__key);
            synchronized (this) {
                family = familyNew;
                family__resolvedKey = __key;
            }
        }
        return family;
    }

    @Generated(hash = 505270660)
    private transient String family__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 935759326)
    public void setCreator(DBUser creator) {
        synchronized (this) {
            this.creator = creator;
            creatorId = creator == null ? null : creator.getId();
            creator__resolvedKey = creatorId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2063422250)
    public DBUser getCreator() {
        String __key = this.creatorId;
        if (creator__resolvedKey == null || creator__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBUserDao targetDao = daoSession.getDBUserDao();
            DBUser creatorNew = targetDao.load(__key);
            synchronized (this) {
                creator = creatorNew;
                creator__resolvedKey = __key;
            }
        }
        return creator;
    }

    @Generated(hash = 1780134435)
    private transient String creator__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1486474624)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBDayBookDao() : null;
    }

    /** Used for active entity operations. */
    @Generated(hash = 451617926)
    private transient DBDayBookDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getSettleId() {
        return this.settleId;
    }

    public void setSettleId(String settleId) {
        this.settleId = settleId;
    }

    public int getSettled() {
        return this.settled;
    }

    public void setSettled(int settled) {
        this.settled = settled;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPayerId() {
        return this.payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getFamilyId() {
        return this.familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
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

    @Generated(hash = 443306936)
    public DBDayBook(String id, Date lastModifiedTime, double money, String creatorId, String familyId,
            String payerId, Date date, int settled, String settleId) {
        this.id = id;
        this.lastModifiedTime = lastModifiedTime;
        this.money = money;
        this.creatorId = creatorId;
        this.familyId = familyId;
        this.payerId = payerId;
        this.date = date;
        this.settled = settled;
        this.settleId = settleId;
    }

    @Generated(hash = 1500464206)
    public DBDayBook() {
    }
}
