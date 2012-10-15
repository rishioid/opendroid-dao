package com.opendroid.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.opendroid.db.DbModel;
import com.opendroid.db.StringUtils;

public abstract class BaseDAO<T extends DbModel> implements DAO<T> {

	private static final String TAG = "BaseDAO";
	protected final SQLiteDatabase db;
	protected final Context context;

	public BaseDAO(Context context, SQLiteDatabase db) {
		this.context = context;
		this.db = db;
	}

	public abstract String getTableName();

	public abstract T fromCursor(Cursor c);

	public abstract ContentValues values(T t);

	public boolean isNotEmpty() throws DAOException {
		Cursor c = null;
		try {
			c = db.rawQuery("select " + ID + " from " + getTableName(), null);
			return c.moveToFirst();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	public T findByPrimaryKey(int id) throws DAOException {
		Cursor c = null;
		T t = null;

		try {
			c = db.rawQuery("select * from " + getTableName() + " where " + ID
					+ " = ?", whereArgsForId(id));
			if (c.moveToFirst()) {
				t = fromCursor(c);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return t;
	}

	public T findFirstByField(String fieldName, String value)
			throws DAOException {
		Cursor c = null;
		T t = null;

		try {
			String q = "select * from " + getTableName() + " where "
					+ fieldName + " = ?";
			Log.d(TAG, q);
			c = db.rawQuery(q, new String[] { value });
			if (c.moveToFirst()) {
				t = fromCursor(c);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return t;
	}

	/**
	 * @param fieldName
	 *            - field name to search by
	 * @param value
	 *            - the value of the field
	 * @param orderConditions
	 *            - the "order by" sentence. May be <b>null</b>.
	 * */
	public List<T> selectAll() {
		Cursor c = null;
		List<T> result = null;
		try {
			c = db.rawQuery("select * from " + getTableName(), null);
			result = new ArrayList<T>();
			if (c.moveToFirst()) {
				do {
					Log.d(TAG, "COLUMN : " + c.getString(0)
							+ " 1: " + c.getString(1) + " 2: " + c.getString(2));
					result.add(fromCursor(c));
				} while (c.moveToNext());
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	/**
	 * @param fieldName
	 *            - field name to search by
	 * @param value
	 *            - the value of the field
	 * @param orderConditions
	 *            - the "order by" sentence. May be <b>null</b>.
	 * */
	public List<T> findAllByField(String fieldName, String value,
			String orderConditions) {
		Cursor c = null;
		List<T> result = null;
		try {
			c = db.rawQuery("select * from " + getTableName() + " where "
					+ fieldName + " = ? " + StringUtils.safe(orderConditions),
					new String[] { value });
			result = new ArrayList<T>();
			if (c.moveToFirst()) {
				do {
					result.add(fromCursor(c));
				} while (c.moveToNext());
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	public void create(T model) throws DAOException {

		int id = (int) db.insert(getTableName(), "0.0", values(model));
		if (id != -1) {
			// TODO: change type of ID field to Long
			model.setId(id);
		}
	}

	public void update(T model) throws DAOException {

		db.update(getTableName(), values(model), ID + " = ?",
				whereArgsForId(model.getId()));
	}

	public void createOrUpdate(T model) throws DAOException {
		if (exists(model.getId())) {
			update(model);
		} else {
			create(model);
		}
	}

	public void update(ArrayList<T> models) throws DAOException {
		for (T model : models) {
			update(model);
		}
	}

	public void delete(int id) throws DAOException {
		db.delete(getTableName(), " " + ID + " = ?", whereArgsForId(id));
	}

	public void deleteAll() throws DAOException {
		db.delete(getTableName(), null, null);
	}

	public void deleteByField(String fieldName, String fieldValue) {
		db.delete(getTableName(), " " + fieldName + " = ?",
				new String[] { fieldValue });
	}

	public boolean exists(int id) throws DAOException {
		Cursor c = null;

		try {
			c = db.rawQuery("select _id from " + getTableName() + " where "
					+ ID + " = ?", whereArgsForId(id));
			return c.moveToFirst();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	public List<T> findAll() {
		Cursor c = null;
		List<T> result = null;
		try {
			c = db.rawQuery("select * from " + getTableName(), null);
			result = new ArrayList<T>();
			if (c.moveToFirst()) {
				do {
					result.add(fromCursor(c));
				} while (c.moveToNext());
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	protected List<T> findAll(String orderConditions) {
		Cursor c = null;
		List<T> result = null;
		try {
			c = db.rawQuery("select * from " + getTableName() + ' '
					+ StringUtils.safe(orderConditions), null);
			result = new ArrayList<T>();
			if (c.moveToFirst()) {
				do {
					result.add(fromCursor(c));
				} while (c.moveToNext());
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	protected String[] whereArgsForId(int id) {
		return new String[] { String.valueOf(id) };
	}

	/**
	 * Convert <code>{1, 2, 3}</code> to <code>"1,2,3"</code>.
	 */
	protected String idArrayToString(int[] ids) {
		StringBuilder sqlFragment = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			sqlFragment.append(ids[i]);
			if (i < ids.length - 1) {
				sqlFragment.append(',');
			}
		}
		return sqlFragment.toString();
	}

	/**
	 * Convert all cursor lines to a list of model objects.
	 */
	protected List<T> allFromCursor(Cursor cursor) {
		ArrayList<T> result = new ArrayList<T>();
		if (cursor.moveToFirst()) {
			do {
				result.add(fromCursor(cursor));
			} while (cursor.moveToNext());
		}
		return result;
	}
}
