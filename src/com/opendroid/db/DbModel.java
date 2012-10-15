package com.opendroid.db;

// TODO: Auto-generated Javadoc
/**
 * The Interface DbModel, to be implemented for mapping table with db table .
 */
public interface DbModel {
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId();

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id);
	
	/**
	 * returns table name to which the model corresponds.
	 *
	 * @return the table name
	 */
	public String getTableName();

	/**
	 * Gets the creates the statement for table.
	 *
	 * @return the creates the statement
	 */
	public String getCreateStatement();
}