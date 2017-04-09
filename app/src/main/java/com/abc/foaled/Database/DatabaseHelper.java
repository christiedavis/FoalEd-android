package com.abc.foaled.Database;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for our application
    private static final String DATABASE_NAME = "foaled.db";
    // anytime there's a change to the database objects, update this to update everything
    private static final int DATABASE_VERSION = 2;

    // the DAO object we use to access the Horse table
    private Dao<Horse, Integer> horseDao = null;
    private Dao<Birth, Integer> birthsDao = null;
    private RuntimeExceptionDao<Horse, Integer> horseRuntimeDao = null;
    private RuntimeExceptionDao<Birth, Integer> birthsRuntimeDao = null;

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
            TableUtils.createTable(connectionSource, Horse.class);
            TableUtils.createTable(connectionSource, Birth.class);
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
/*        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Horse.class, true);
            TableUtils.dropTable(connectionSource, Birth.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);


        } catch (SQLException e) {
            //Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        } */
/*        RuntimeExceptionDao<Horse, Integer> horseDAO = getHorseDataDao();

        try {
            //Upgrading to version 3 of the database
            if (oldVersion < 3) {
                horseDao.executeRaw("ALTER TABLE 'horse' ADD COLUMN status INTEGER DEFAULT 0;");
                horseDao.updateRaw("UPDATE 'horse' SET status = 1 WHERE 'birthdate makes them a foal");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * Returns the Database Access Object (DAO) for our Database classes. It will create them or just give the cached
     * values.
     */
    public Dao<Horse, Integer> getHorseDao() throws SQLException {
        if (horseDao == null)
            horseDao = getDao(Horse.class);
        return horseDao;
    }

    public Dao<Birth, Integer> getBirthsDao() throws  SQLException {
        if (birthsDao == null)
            birthsDao = getDao(Birth.class);
        return birthsDao;
    }

public void addNewHorse(Birth birth, Horse horse) {

    try {

        getBirthsDao().create(birth);
        getHorseDao().create(horse);

    }
    catch (Exception ex){
     //TODO: handle errors
    }
}
    public List<Horse> refresh() {
        return getHorseDataDao().queryForAll();
    }
    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for database object classes. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Horse, Integer> getHorseDataDao() {
        if (horseRuntimeDao == null)
            horseRuntimeDao = getRuntimeExceptionDao(Horse.class);
        return horseRuntimeDao;
    }

    public RuntimeExceptionDao<Birth, Integer> getBirthsDataDao() {
        if (birthsRuntimeDao == null)
            birthsRuntimeDao = getRuntimeExceptionDao(Birth.class);
        return birthsRuntimeDao;
    }


    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        horseDao = null;
        birthsDao = null;
        horseRuntimeDao = null;
        birthsRuntimeDao = null;
    }
}