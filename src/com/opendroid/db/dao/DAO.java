package com.opendroid.db.dao;
import android.content.ContentValues;
import android.database.Cursor;

public interface DAO<T> {
    
    public static final String ID = "_id";
    
    public T findByPrimaryKey(Long id) throws DAOException;
    public void create(T object) throws DAOException;
    public void update(T object) throws DAOException;
    public void createOrUpdate(T object) throws DAOException;
    public void delete(Long id) throws DAOException;
    public boolean exists(Long id) throws DAOException;
        
    public T fromCursor(Cursor c);
    public ContentValues values(T t);
}