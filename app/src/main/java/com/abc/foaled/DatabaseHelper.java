package com.abc.foaled;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.abc.foaled.DatabaseDescription.ChildrenTable;
import com.abc.foaled.DatabaseDescription.HorseTable;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for our application
    private static final String DATABASE_NAME = "foaled.db";
    // anytime there's a change to the database objects, update this to update everything
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the HorseTable table
    private Dao<HorseTable, Integer> horseDao = null;
    private Dao<ChildrenTable, Integer> childrenDao = null;
    private RuntimeExceptionDao<HorseTable, Integer> horseRuntimeDao = null;
    private RuntimeExceptionDao<ChildrenTable, Integer> childrenRuntimeDao = null;

    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, HorseTable.class);
            TableUtils.createTable(connectionSource, ChildrenTable.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, HorseTable.class, true);
            TableUtils.dropTable(connectionSource, ChildrenTable.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our Database classes. It will create them or just give the cached
     * values.
     */
    public Dao<HorseTable, Integer> getHorseDao() throws SQLException {
        if (horseDao == null)
            horseDao = getDao(HorseTable.class);
        return horseDao;
    }

    public Dao<ChildrenTable, Integer> getChildrenDao() throws SQLException {
        if (childrenDao == null)
            childrenDao = getDao(ChildrenTable.class);
        return childrenDao;
    }



    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for database object classes. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<HorseTable, Integer> getHorseDataDao() {
        if (horseRuntimeDao == null)
            horseRuntimeDao = getRuntimeExceptionDao(HorseTable.class);
        return horseRuntimeDao;
    }

    public RuntimeExceptionDao<ChildrenTable, Integer> getChildrenDataDao() {
        if (childrenRuntimeDao == null)
            childrenRuntimeDao = getRuntimeExceptionDao(ChildrenTable.class);
        return childrenRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        horseDao = null;
        childrenDao = null;
        horseRuntimeDao = null;
        childrenRuntimeDao = null;
    }
}