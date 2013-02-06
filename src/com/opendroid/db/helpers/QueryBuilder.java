package com.opendroid.db.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.opendroid.db.DbModel;
import com.opendroid.db.dao.annotations.Column;
import com.opendroid.db.dao.annotations.Table;

/**
 * The Class QueryBuilder.
 */
public class QueryBuilder {

	/**
	 * Gets the creates the query from DbModel type class.
	 * 
	 * @param models
	 *            the models
	 * @return the creates the query from model
	 */
	public static List<String> getCreateQueryFromModel(
			List<Class<DbModel>> models) {
		String query = null;
		List<String> queries = new ArrayList<String>();
		for (Class clas : models) {
			query = null;
			Field[] fields = clas.getDeclaredFields();
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (query == null) {
					query = "CREATE TABLE " + clas.getAnnotation(Table.class)
							+ " (";
				} else {
					query += " " + ("".equals(column.name())?field.getName():column.name()) + " " + column.type();
				}
			}
			queries.add(query);
		}

		return queries;
	}

}
