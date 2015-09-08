package com.sunrise.epark.database;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.util.SdCardUtil;


/**
 * db助手类
 * 
 * @author wenjian
 * 
 */
public class SQLiteDataBaseHelper implements DataBaseConstDefine {

	private SQLiteDatabase db;

	public SQLiteDatabase getDb() {
		return db;
	}

	public SQLiteDataBaseHelper(Context context) {
		// 方案1 开发测试阶段使用
		File file = new File(SdCardUtil.getSDCardPath() + "/" + Configure.DATABASE_NAME);
		if (file.exists()) {
			db = SQLiteDatabase.openOrCreateDatabase(file, null);
		}
		else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			db = SQLiteDatabase.openOrCreateDatabase(file, null);
			// 创建基础表结构
			db.execSQL(LOGS_CREATE_SQL);// 日志信息表
			db.execSQL(PROFILEINFO_CREATE_SQL);// 配置记录表
			db.execSQL(MESSAGEINFO_CREATE_SQL);// 消息记录表
		}
		// -----------------------------------------------------------------------------
		// 方案2 生产阶段使用
		if (db == null) {
			db = context.openOrCreateDatabase(Configure.DATABASE_NAME, Context.MODE_PRIVATE, null);
			if (Configure.DB_VERSION == db.getVersion()) {
				// 版本相同,不需要更新db文件
			}
			else {
				// 程序员手动设置本次发布版本的数据库
				// 先更新表结构 这个里面存放数据变更的代码 依据实际情况处理
				if (db.getVersion() != 0) {
					db.execSQL("Drop table LOGS ");// 日志信息表
					db.execSQL("Drop table PROFILEINFO ");// 配置记录表
					db.execSQL("Drop table MESSAGEINFO ");// 消息记录表
				}
				db.execSQL(LOGS_CREATE_SQL);// 日志信息表
				db.execSQL(PROFILEINFO_CREATE_SQL);// 配置记录表
				db.execSQL(MESSAGEINFO_CREATE_SQL);// 消息记录表
				db.setVersion(Configure.DB_VERSION);// 设置成本次版本
			}
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param objects
	 *            插入的数据
	 * @return 影响数据库的行数
	 */
	public long insert(String table, String[] columns, Object[] objects) {
		int size = columns.length;
		ContentValues values = new ContentValues();
		for (int i = 0; i < size; i++) {
			if (objects[i] == null) {
				objects[i] = "";
			}
			if (objects[i] instanceof Integer) {
				values.put(columns[i], (Integer) objects[i]);
			}
			else if (objects[i] instanceof Long) {
				values.put(columns[i], (Long) objects[i]);
			}
			else if (objects[i] instanceof String) {
				values.put(columns[i], objects[i].toString());
			}
			else {
				// 解决是null的情况
				values.put(columns[i], "");
			}
		}
		return db.insert(table, null, values);
	}

	/**
	 * 查询数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @return 装有数据的游标
	 */
	public Cursor query(String table, String[] columns) {
		Cursor cursor = db.query(table, columns, null, null, null, null, null);
		return cursor;
	}

	/**
	 * 查询数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param selection
	 *            条件
	 * @param selectionArgs
	 *            具体的条件值
	 * @return 装有数据的游标
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
		Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
		return cursor;
	}

	/**
	 * 查询数据ORDER BY
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param selection
	 *            条件
	 * @param selectionArgs
	 *            具体的条件值
	 * @return 装有数据的游标
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
		Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy);
		return cursor;
	}

	/**
	 * 更新数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段
	 * @param objects
	 *            更新的数据
	 * @param whereClause
	 *            条件
	 * @param whereArgs
	 *            具体的条件参数
	 * @return 影响数据的行数
	 */
	public int update(String table, String[] columns, Object[] objects, String whereClause, String[] whereArgs) {
		int size = columns.length;
		ContentValues values = new ContentValues();
		for (int i = 0; i < size; i++) {
			if (objects[i] instanceof Integer) {
				values.put(columns[i], (Integer) objects[i]);
			}
			else if (objects[i] instanceof Long) {
				values.put(columns[i], (Long) objects[i]);
			}
			else if (objects[i] instanceof String) {
				values.put(columns[i], objects[i].toString());
			}
			else {
				// 解决是null的情况
				values.put(columns[i], "");
			}
		}
		return db.update(table, values, whereClause, whereArgs);
	}

	/**
	 * 删除数据
	 * 
	 * @param table
	 *            表名
	 * @param whereClause
	 *            条件
	 * @param whereArgs
	 *            具体的条件参数
	 * @return 影响数据的行数
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}

	/**
	 * 开始事务
	 */
	public void beginTransaction() {
		db.beginTransaction();
	}

	/**
	 * 事物处理成功标志
	 */
	public void transactionSuccessful() {
		db.setTransactionSuccessful();
	}

	/**
	 * 结束事务
	 */
	public void endTransaction() {
		db.endTransaction();
	}
}