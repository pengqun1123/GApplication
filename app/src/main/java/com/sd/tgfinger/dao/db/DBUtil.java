package com.sd.tgfinger.dao.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created By pq
 * on 2019/9/9
 */
public class DBUtil {

    private static final String DB_NAME = "UE875_DB.db";
    private static final String DEFAULT_DB_PASSWORD = "ue875ue";

    private DaoSession daoSession;
    private static DBUtil dbUtil = null;
    private MyGreenDaoDbHelper devOpenHelper;
    private AbstractDao mAbstractDao;
    private DbCallBack mCallBack;

    public DBUtil() {
    }

    public static DBUtil instance(Application application) {
        if (dbUtil == null) {
            synchronized (DBUtil.class) {
                if (dbUtil == null) {
                    dbUtil = new DBUtil(application, DB_NAME, DEFAULT_DB_PASSWORD);
//                    dbUtil = new DBUtil();
                }
            }
        }
        return dbUtil;
    }

    //    初始化数据库
    public void initDB(Context context) {
        devOpenHelper = new MyGreenDaoDbHelper(context, DB_NAME);
//        devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public synchronized DaoSession getDaoSession() {
        return daoSession;
    }

    //初始化数据库
    private DBUtil(Application application, String dbName, String passWord) {
        if (devOpenHelper == null) {
            devOpenHelper = new MyGreenDaoDbHelper(application, dbName);
//            devOpenHelper = new DaoMaster.DevOpenHelper(application, dbName);
        }
        DaoMaster daoMaster;
        if (passWord == null || passWord.isEmpty()) {
            daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        } else {
            daoMaster = new DaoMaster(devOpenHelper.getEncryptedReadableDb(passWord));
        }
        daoSession = daoMaster.newSession();

    }

    public static DBUtil getInstance(Application application) {
        return getInstance(application, DB_NAME);
    }

    public static DBUtil getInstance(Application application, String dbName) {
        return getInstance(application, dbName, DEFAULT_DB_PASSWORD);
    }

    public static DBUtil getInstance(Application application, String dbName, String passWord) {
        if (dbUtil == null) {
            synchronized (DBUtil.class) {
                if (dbUtil == null) {
                    dbUtil = new DBUtil(application, dbName, "");
                }
            }
        }
        return dbUtil;
    }

    /**
     * 数据库不加密
     *
     * @param entityClass 根据 entityClass 获取相应的 xxDao
     * @return mDbUtils
     */
    @Deprecated
    public DBUtil create(Class<?> entityClass) {
        if (devOpenHelper == null) {
            throw new NullPointerException("You need to init mHelper first!");
        }
        mAbstractDao = daoSession.getDao(entityClass);
        return dbUtil;
    }

    private <T> void setCurrentDao(Class<T> entityClass) {
        if (devOpenHelper == null) {
            throw new NullPointerException("You need to init mHelper first!");
        }
        mAbstractDao = daoSession.getDao(entityClass);
    }

    private <T> void setCurrentDaoOfList(Collection<T> entities) {
        if (entities != null && entities.size() > 1) {
            Iterator<T> iterator = entities.iterator();
            T next = iterator.next();
            setCurrentDao(next.getClass());
        }
    }

    /**
     * 设置DeBug的模式
     *
     * @param flag
     */
    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    private void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    private void closeHelper() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }

    public void closeData() {
        closeHelper();
        closeDaoSession();
    }

    public <T> Query<T> getQuery(Class<T> claz) {
        return getQueryBuilder(claz).build();
    }

    public <T> QueryBuilder<T> getQueryBuilder(Class<T> claz) {
        return daoSession.queryBuilder(claz);
    }

    /**
     * 获取数据库Item的数量
     *
     * @return
     */
    public <T> Long count(Class<T> entityClaz) {
        setCurrentDao(entityClaz);
        return mAbstractDao.count();
    }

    /**
     * 插入一条数据
     *
     * @param dbEntity
     */
    public <T> void insert(T dbEntity) {
        setCurrentDao(dbEntity.getClass());
        mAbstractDao.insert(dbEntity);
    }

    /**
     * 插入一条数据
     *
     * @param dbEntity
     */
    public <T> void insertOrReplace(T dbEntity) {
        setCurrentDao(dbEntity.getClass());
        mAbstractDao.insertOrReplace(dbEntity);
    }

    /**
     * 插入多条数据
     *
     * @param entities
     */
    public <T> void insertTx(List<T> entities) {

        if (entities == null || entities.size() < 1) {
            return;
        }
        setCurrentDaoOfList(entities);
        mAbstractDao.insertInTx(entities);
    }

    public <T> void insertOrReplaceInTx(List<T> entities) {
        if (entities == null || entities.size() < 1) {
            return;
        }
        setCurrentDaoOfList(entities);
        mAbstractDao.insertOrReplaceInTx(entities);
    }

    /**
     * 删除单条数据
     *
     * @param entity
     */
    public <T> void delete(T entity) {
        setCurrentDao(entity.getClass());
        mAbstractDao.delete(entity);
    }

    /**
     * 删除特定ID的数据
     *
     * @param id
     */
    public <T> void deleteById(Class<T> entityClaz, long id) {
        setCurrentDao(entityClaz);
        mAbstractDao.deleteByKey(id);
    }

    /**
     * 删除多条数据
     *
     * @param entities
     */
    public <T> void deleteList(List<T> entities) {
        setCurrentDaoOfList(entities);
        mAbstractDao.deleteInTx(entities);
    }

    /**
     * 全部删除
     */
    public <T> void deleteAll(Class<T> claz) {
        setCurrentDao(claz);
        mAbstractDao.deleteAll();
    }

    /**
     * 更新单条数据
     *
     * @param entity
     */
    public <T> void updateData(final T entity) {
        setCurrentDao(entity.getClass());
        mAbstractDao.update(entity);
    }

    /**
     * 更新多条数据
     *
     * @param entities
     */
    public <T> void updateListData(Collection<T> entities) {
        setCurrentDaoOfList(entities);
        mAbstractDao.updateInTx(entities);
    }

    /**
     * 查询特定ID的数据
     *
     * @param id
     * @return
     */
    public <T> T queryById(Class<T> claz, long id) {
        setCurrentDao(claz);
        return (T) mAbstractDao.load(id);
    }

    /**
     * 查询全部数据
     *
     * @return
     */
    public <T> List<T> queryAll(Class<T> claz) {
        setCurrentDao(claz);
        return mAbstractDao.loadAll();
    }

    public <T> List<T> queryAll(Class<T> claz, WhereCondition whereCondition) {
        setCurrentDao(claz);
        return daoSession.queryBuilder(claz)
                .where(whereCondition)
                .list();
    }

    public <T> List<T> queryAll(Class<T> claz, QueryBuilder<T> queryBuilder) {
        setCurrentDao(claz);
        return daoSession.queryBuilder(claz).list();
    }

    public <T> List<T> queryAll(Class<T> claz, Query<T> query) {
        setCurrentDao(claz);
        return query.list();
    }

    /**
     * 原生查询
     *
     * @param claz
     * @param whereString
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> queryRaw(Class<T> claz, String whereString, String[] params) {
        setCurrentDao(claz);
        return mAbstractDao.queryRaw(whereString, params);
    }

    /**
     * 异步操作的回调设置
     *
     * @param callBack
     * @param <T>
     * @return
     */
    public <T> DBUtil setDbCallBack(DbCallBack<T> callBack) {
        mCallBack = callBack;
        return this;
    }

    /**
     * 条件查询数据
     *
     * @param cls
     * @return
     */
    public <T> void queryAsync(Class<T> cls, WhereCondition whereCondition) {
        setCurrentDao(cls);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    //      List<T> list = new ArrayList<>();
                    //     list.add(((T) operation.getResult()));
                    mCallBack.onSuccess((List) operation.getResult());
                } else if (operation.isFailed()) {
                    mCallBack.onFailed();
                }
            }
        });
        Query query = daoSession.queryBuilder(cls).where(whereCondition).build();
        asyncSession.queryList(query);
    }

    public <T> void queryAsync(Class<T> cls, WhereCondition cond1, WhereCondition cond2,
                               WhereCondition... condMore) {
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onSuccess((List) operation.getResult());
                } else if (operation.isFailed()) {
                    mCallBack.onFailed();
                }
            }
        });
        Query query = daoSession.queryBuilder(cls).whereOr(cond1, cond2, condMore).build();
        asyncSession.queryList(query);
    }


    /**
     * 异步条件查询，通过使用 QueryBuilder 构造 Query
     *
     * @param claz
     * @param builder
     * @param <T>
     */
    public   <T>  void  queryAsyncAll(Class<T> claz, QueryBuilder<T> builder) {
        setCurrentDao(claz);
        String name = claz.getName();
        Logger.d("  类名 :" + name);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompleted() && mCallBack != null) {
                    List<T> result = (List<T>) operation.getResult();
                    mCallBack.onSuccess(result);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onFailed();
                }
            }
        });
        if (builder == null || builder.build() == null) {
            asyncSession.loadAll(claz);
        } else {
            asyncSession.queryList(builder.build());
        }
    }

    /**
     * 删除
     */
    public <T> void deleteAsyncSingle(T entry) {
        setCurrentDao(entry.getClass());
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                }
            }
        });
        asyncSession.delete(entry);
    }

    /**
     * 批量删除
     */
    public <T> void deleteAsyncBatch(Class<T> cls, final List<T> list) {
        setCurrentDao(cls);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                }
            }
        });
        asyncSession.deleteInTx(cls, list);
    }


    /**
     * 根据Id批量删除
     */
    public <T> void deleteByIdBatch(Class<T> claz, List<Long> longList) {
        setCurrentDao(claz);
        mAbstractDao.deleteByKeyInTx(longList);
    }

    /**
     * 删除所有数据
     */
    public <T> void deleteAsyncAll(Class<T> cls) {
        setCurrentDao(cls);
        final AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                    mCallBack.onSuccess(null);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                    mCallBack.onFailed();
                }
            }
        });
        asyncSession.deleteAll(cls);
    }

    /**
     * 插入一条数据
     */
    public <T> void insertAsyncSingle(final T entity) {
        setCurrentDao(entity.getClass());
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                    mCallBack.onSuccess(entity);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                    mCallBack.onFailed();
                }
            }
        });
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                daoSession.insertOrReplace(entity);
            }
        });
    }

    /**
     * 批量插入
     */
    public <T> void insertAsyncBatch(final Class<T> cls, final List<T> userList) {
        setCurrentDao(cls);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                    mCallBack.onSuccess(userList);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                    mCallBack.onFailed();
                }
            }
        });
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                for (T object : userList) {
                    daoSession.insertOrReplace(object);
                }
            }
        });
    }

    /**
     * 更新一个数据
     */
    public <T> void updateAsyncSingle(Class<T> cls, final T entry) {
        setCurrentDao(cls);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                    mCallBack.onSuccess(entry);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                    mCallBack.onFailed();
                }
            }
        });
        asyncSession.update(entry);
    }

    /**
     * 批量更新数据
     */
    public <T> void updateAsyncBatch(final Class<T> cls, final List<T> tList) {
        setCurrentDao(cls);
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isCompletedSucessfully() && mCallBack != null) {
                    mCallBack.onNotification(true);
                } else if (operation.isFailed() && mCallBack != null) {
                    mCallBack.onNotification(false);
                }
            }
        });
        asyncSession.updateInTx(cls, tList);
    }



}
