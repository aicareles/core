package com.heaton.musiclib.player.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.heaton.musiclib.BuildConfig;
import com.heaton.musiclib.R;
import com.heaton.musiclib.vo.MusicVO;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;


/**
 * 数据库工具
 * Created by DDL on 2016/5/20.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app

	private static final String DATABASE_NAME = "heaton.db";

	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 5;

	private Dao<MusicVO, Integer> musicDao = null;

	private RuntimeExceptionDao<MusicVO, Integer> mMusicRuntimeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.db_config);
	}


	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * <p/>
	 * the tables that will store your data.
	 */

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			if(BuildConfig.DEBUG){
				Log.i(DatabaseHelper.class.getName(), "onCreate");
			}
			TableUtils.createTable(connectionSource, MusicVO.class);
		} catch (SQLException e) {
			if(BuildConfig.DEBUG) {
				Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			}
			throw new RuntimeException(e);
		}
	}


	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * <p/>
	 * the various data to match the new version number.
	 */

	@Override

	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

		try {
			if(BuildConfig.DEBUG) {
				Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			}
			TableUtils.dropTable(connectionSource, MusicVO.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			if(BuildConfig.DEBUG) {
				Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			}
			throw new RuntimeException(e);
		}

	}

	public Dao<MusicVO, Integer> getDao() throws SQLException {
		if (musicDao == null) {
			musicDao = getDao(MusicVO.class);
		}
		return musicDao;
	}
	public RuntimeExceptionDao<MusicVO, Integer> getMusicDao() {
		if (mMusicRuntimeDao == null) {
			mMusicRuntimeDao = getRuntimeExceptionDao(MusicVO.class);
		}
		return mMusicRuntimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		musicDao = null;
		mMusicRuntimeDao = null;
	}

}
