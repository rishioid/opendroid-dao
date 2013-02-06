package com.opendroid.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * DBHelper v3.0 helper class for database CRUD operation on sqlite database
 * 
 * @author Rishi K
 * 
 */
public class DbHelper {

	/** The database name. */
	private static String DATABASE_NAME;

	/** The Constant DATABASE_VERSION. */
	private final static int DATABASE_VERSION = 1;

	/** The Constant TAG. */
	private static final String TAG = "DbHelper";

	/** The models. */
	private static List<DbModel> models;

	/** The context. */
	private Context context;

	/** The db. */
	private SQLiteDatabase db;

	/** The insert stmt. */
	private SQLiteStatement insertStmt;

	/** The open helper. */
	private static OpenHelper openHelper;

	/** The database path. */
	private static String databasePath = null;

	/** The db helper. */
	private static DbHelper dbHelper = null;

	private static boolean autoCreateTables = false;

	/**
	 * Instantiates a new db helper.
	 * 
	 * @param context
	 *            the context
	 */
	private DbHelper(Context context) {
		this.context = context;

		if (this.DATABASE_NAME == null || this.models == null) {
			Log.d(TAG, "Initialized an empty helper.");
		} else {
			openHelper = new OpenHelper(this.context);
			openHelper.close();
			if (db != null && db.isOpen()) {
				db.close();
				openHelper.close();
			}
			if (databasePath == null) {
				db = openHelper.getWritableDatabase();
			} else {
				db = SQLiteDatabase.openDatabase(databasePath + DATABASE_NAME,
						null, SQLiteDatabase.OPEN_READWRITE);

			}
		}

	}

	/**
	 * Inits a new databasehelper instance, need to be done just once while
	 * startup of application
	 * 
	 * @param context
	 *            the context
	 * @param dbConfiguration
	 *            the database configuration
	 */
	public synchronized static void init(DbConfiguration dbConfiguration) {
		models = dbConfiguration.getModels();
		databasePath = dbConfiguration.getDatabasePath();
		autoCreateTables = dbConfiguration.isOrm();
		// return new DbHelper(context);
	}

	/**
	 * Gets the single instance of DbHelper.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of DbHelper
	 * @throws InstantiationException if this method is called before init method.
	 */
	public static DbHelper getInstance(Context context) throws InstantiationException {

		if (DATABASE_NAME != null) {
			if (dbHelper == null) {
				dbHelper = new DbHelper(context);
			}
		}
		else{
			throw new InstantiationException("OBJECT NOT INITIALIZED : Please initialize DbHelper with init() method first."); 
		}

		return dbHelper;
	}

	/**
	 * Close.
	 */
	public void close() {
		if (db != null) {
			db.close();
		}
	}

	/**
	 * Gets the SQLite database instance.
	 * 
	 * @return the SQLite database instance
	 */
	public SQLiteDatabase getSQLiteDatabase() {
		if (db != null && !db.isOpen()) {
			if (databasePath == null) {
				db = openHelper.getWritableDatabase();
			} else {
				db = SQLiteDatabase.openDatabase(databasePath + DATABASE_NAME,
						null, SQLiteDatabase.OPEN_READWRITE);

			}
		}
		return db;
	}

	/**
	 * Method to insert values to database.
	 * 
	 * @param query
	 *            = string query as per JDBC prepared statement
	 * @param values
	 *            = values substituted for ? in query specified
	 * @return returns true if insert is successful else returns false
	 */
	public boolean insert(String query, String[] values) {
		this.insertStmt = this.db.compileStatement(query);
		for (int i = 0; i < values.length; i++) {
			this.insertStmt.bindString(i + 1, values[i]);
		}
		return this.insertStmt.executeInsert() > 0 ? true : false;
	}

	/**
	 * Update.
	 * 
	 * @param table
	 *            = name of table
	 * @param columns
	 *            = String[] for columns
	 * @param values
	 *            = values substituted for ? in query specified
	 * @param whereClause
	 *            = WHERE condition
	 * @param whereArgs
	 *            = arguments if where parameter is in prepared statement format
	 * @return returns true if update is successful else returns false
	 * @author ritesh
	 * 
	 *         Method to update values to database
	 */

	public boolean update(String table, String columns[], String[] values,
			String whereClause, String whereArgs[]) {

		int size = columns.length;
		ContentValues cv = new ContentValues(size);
		for (int i = 0; i < size; i++) {
			cv.put(columns[i], values[i]);
		}

		return this.db.update(table, cv, whereClause, whereArgs) > 0 ? true
				: false;

	}

	/**
	 * Delete.
	 * 
	 * @param table
	 *            = name of table
	 * @param whereClause
	 *            = WHERE condition
	 * @param whereArgs
	 *            = arguments if where parameter is in prepared statement format
	 * @return returns true if delete is successful else returns false
	 * @author ritesh
	 * 
	 *         Method to delete row(s) from table
	 */

	public boolean delete(String table, String whereClause, String whereArgs[]) {

		return this.db.delete(table, whereClause, whereArgs) > 0 ? true : false;
	}

	/**
	 * Clears complete database.
	 */
	public void delete() {
		for (DbModel query : models) {
			this.db.delete(query.getTableName(), null, null);
		}
	}

	/**
	 * Parses the cursor to list.
	 * 
	 * @param cursor
	 *            the cursor
	 * @return the list
	 */
	public List<Object[]> parseCursorToList(Cursor cursor) {
		List<Object[]> list = new ArrayList<Object[]>();
		String columns[] = cursor.getColumnNames();
		if (cursor.moveToFirst()) {
			do {
				int i = 0;
				Object row[] = new Object[columns.length];
				for (String column : columns) {
					row[i] = cursor.getString(cursor.getColumnIndex(column));
					Log.d("size", String.valueOf(row[i]));
					i++;
				}
				list.add(row);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	/**
	 * Selects all columns from table.
	 * 
	 * @param table
	 *            = name of table from which records need to be specified
	 * @return returns List of Object[], each element in List represent row of
	 *         table in Object[] form
	 */
	public List<Object[]> select(String table) {
		Cursor cursor = this.db.query(table, new String[] { "*" }, null, null,
				null, null, null);
		List<Object[]> list = parseCursorToList(cursor);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * Selects records from table.
	 * 
	 * @param table
	 *            = name of table
	 * @param columns
	 *            = String[] for columns
	 * @param where
	 *            = WHERE condition
	 * @param whereargs
	 *            = arguments if where parameter is in prepared statement format
	 * @param groupby
	 *            = GROUP BY column(s)
	 * @param having
	 *            = HAVING condition
	 * @param orderby
	 *            = ORDER BY column witrh asc, desc specification
	 * @return the list
	 */
	public List<Object[]> select(String table, String columns[], String where,
			String whereargs[], String groupby, String having, String orderby) {

		Cursor cursor = this.db.query(table, columns, where, whereargs,
				groupby, having, orderby);
		List<Object[]> list = parseCursorToList(cursor);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * Gets the max id.
	 * 
	 * @param tableName
	 *            the table name
	 * @return the max id
	 */
	public int getMaxID(String tableName) {
		String query = "SELECT MAX(_id) FROM " + tableName;
		Cursor cursor = db.rawQuery(query, null);

		int id = 0;
		if (cursor.moveToFirst()) {
			do {
				id = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return id;
	}

	/**
	 * Gets the count.
	 * 
	 * @param tableName
	 *            the table name
	 * @return the count
	 */
	public int getCount(String tableName) {
		String query = "SELECT count(*) FROM " + tableName;
		Cursor cursor = db.rawQuery(query, null);

		int id = 0;
		if (cursor.moveToFirst()) {
			do {
				id = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return id;
	}

	/**
	 * Checks if is not empty.
	 * 
	 * @param table
	 *            the table
	 * @return true, if is not empty
	 */
	public boolean isNotEmpty(String table) {
		Cursor c = null;
		try {
			c = db.rawQuery("select * from " + table, null);
			return c.moveToFirst();
		} catch (Exception e) {
			Log.e("Exception in isNotEmpty", e.getMessage());
			// throw new DAOException(e);
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}

	}

	/**
	 * Gets the models.
	 * 
	 * @return the models
	 */
	public List<DbModel> getModels() {
		return models;
	}

	/**
	 * Sets the models.
	 * 
	 * @param models
	 *            the new models
	 */
	private void setModels(List<DbModel> models) {
		this.models = models;
	}

	/**
	 * The Class OpenHelper.
	 */
	private class OpenHelper extends SQLiteOpenHelper {

		/** The Constant TAG. */
		private static final String TAG = "OpenHelper";

		/** The sql. */
		private String sql;

		/**
		 * Instantiates a new open helper.
		 * 
		 * @param context
		 *            the context
		 */
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			if (models != null) {
				for (DbModel query : models) {
					db.execSQL("CREATE TABLE IF NOT EXISTS "
							+ query.getCreateStatement());

					Log.i(TAG, "created table " + query.getTableName());
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("upgrading database", DATABASE_NAME);
			if (models != null) {
				for (DbModel query : models) {
					db.execSQL("DROP TABLE IF EXISTS " + query.getTableName());
				}
			}
			onCreate(db);
		}
	}

}