package com.opendroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opendroid.db.dao.annotations.Column;
import com.opendroid.db.dao.annotations.Id;
import com.opendroid.db.dao.annotations.Table;

import java.lang.reflect.Field;
import java.util.List;

/**
 * DBHelper v3.0 helper class for database CRUD operation on sqlite database
 *
 * @author Rishi K
 */
public class DbHelper {

    private String DATABASE_NAME;
    private final static int DATABASE_VERSION = 1;
    private static final String TAG = "DbHelper";

    private List<DbModel> models;
    private Context context;
    private SQLiteDatabase db;
    private static OpenHelper openHelper;
    private String databasePath = null;
    private static DbHelper dbHelper = null;

    /**
     * Instantiates a new db helper.
     *
     * @param context         the context
     * @param dbConfiguration the db configuration
     * @author siddhesh
     */
    private DbHelper(Context context, DbConfiguration dbConfiguration) {
        this.context = context;
        if (dbConfiguration != null) {
            DATABASE_NAME = dbConfiguration.getDatabaseName();
            models = dbConfiguration.getModels();
            databasePath = dbConfiguration.getDatabasePath();
        }

        if (DATABASE_NAME == null || models == null) {
            throw new IllegalArgumentException("Seems like database name or models are not set while configuring DbConfiguration instance");
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
     * Gets the single instance of DbHelper.
     * {@link com.opendroid.db.DbHelper#init(android.content.Context, DbConfiguration)} needs to be set before calling this method
     *
     * @param context Android context
     * @return singleton instance of DbHelper
     */
    public static synchronized DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context, null);
        }
        return dbHelper;
    }

    /**
     * Initializes a new database, need to be done just once while initializing application
     *
     * @param context         Android context
     * @param dbConfiguration instance of {@link com.opendroid.db.DbConfiguration}
     * @return the db helper
     * @author siddhesh
     */
    public static synchronized DbHelper init(Context context, DbConfiguration dbConfiguration) {
        dbHelper = new DbHelper(context, dbConfiguration);
        return dbHelper;
    }

    /**
     * Closes database connection.
     */
    public void close() {
        if (db != null) {
            db.close();
        }
    }

    /**
     * Gets the SQLite database instance.
     *
     * @return the SQLiteDatabase instance
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
     * Get database models.
     *
     * @return list of {@link com.opendroid.db.DbModel}
     */
    public List<DbModel> getModels() {
        return models;
    }

    /**
     * Sets the models.
     *
     * @param models the new models
     */
    private void setModels(List<DbModel> models) {
        this.models = models;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        private static final String TAG = "OpenHelper";

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /* (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            if (models != null) {
                int idCounter = 0;
                for (DbModel query : models) {

                    Table table = query.getClass().getAnnotation(Table.class);
                    String tableName = null;
                    String fieldString = null;
                    if (table != null) {
                        tableName = table.name();

                        Field[] fields = query.getClass().getFields();
                        for (Field field : fields) {
                            Column column = field.getAnnotation(Column.class);

                            boolean isPrimary = (field.getAnnotation(Id.class) != null);

                            if (fieldString == null) {
                                fieldString = column.name() + " " + column.type() + "" + ((column.size() == -1) ? "" : "(" + column.size() + ")");
                            } else {
                                fieldString += " ," + column.name() + " " + column.type() + "" + ((column.size() == -1) ? "" : "(" + column.size() + ")");
                            }
                            if (isPrimary && idCounter == 0) {
                                idCounter++;
                                fieldString += " PRIMARY KEY";
                            }
                            else{
                                throw new IllegalStateException("Only one primary key can be defined, please correct column ["+column.name()+"]");
                            }
                        }
                    }

                    Log.d(TAG, "CREATE TABLE IF NOT EXISTS "
                            + tableName + fieldString);
                    db.execSQL("CREATE TABLE IF NOT EXISTS "
                            + tableName + fieldString);

                    Log.i(TAG, "created table " + tableName);
                }
            }
        }

        /* (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("upgrading database", DATABASE_NAME);
            if (models != null) {
                for (DbModel query : models) {
                    Table table = query.getClass().getAnnotation(Table.class);
                    if (table != null) {
                        Log.d(TAG, "DROP TABLE IF EXISTS " + table.name());
                        db.execSQL("DROP TABLE IF EXISTS " + table.name());
                    }
                }
            }
            onCreate(db);
        }
    }

}