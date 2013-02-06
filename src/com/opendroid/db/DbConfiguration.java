package com.opendroid.db;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConfiguration.
 */
public class DbConfiguration {
	
	static DbConfiguration dbConf;

	/** The table name. */
	final private String databaseName;
	
	final private String databasePath;

	/** The models. */
	final private List<DbModel> models;
	
	final private boolean orm;
	
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public String getDatabasePath() {
		return databasePath;
	}


	public List<DbModel> getModels() {
		return models;
	}
	
	/**
	 * Instantiates a new db configuration.
	 */
	private DbConfiguration(final Builder builder) {
		this.databaseName = builder.databaseName;
		this.models = builder.models;
		this.databasePath = builder.databasePath;
		this.orm = builder.orm;
	}
	


	public boolean isOrm() {
		return orm;
	}



	/**
	 * Builder pattern for setting all configurations
	 */
	public static class Builder {
		
		public Builder()
		{
			
		}
		
		/** The table name. */
		private String databaseName;
		
		/** The models. */
		private List<DbModel> models;

		private String databasePath;
		
		private boolean orm;

		/**
		 * Sets the table name.
		 *
		 * @param tableName the new table name
		 */
		public Builder setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
			return this;
		}
		
		/**
		 * Sets the table name.
		 *
		 * @param tableName the new table name
		 */
		public Builder setDatabasePath(String databasePath) {
			this.databasePath = databasePath;
			return this;
		}

		/**
		 * Sets the models.
		 *
		 * @param models the new models
		 */
		public Builder setModels(List<DbModel> models) {
			this.models = models;
			return this;
		}
		
		/**
		 * Allows user to set automatically create tables based on annotated columns and tables from model class.
		 * NOTE : set this flag only if models are annotated
		 * 
		 * @param orm boolean flag that allows auto-creation of tables.
		 */
		public Builder setOrm(boolean orm) {
			this.orm=orm;
			return this;
		}

		/**
		 * Builds configuration for database and returns object.
		 *
		 * @return the DbConfiguration object for specified configuration
		 */
		public DbConfiguration build() {
			return new DbConfiguration(this);

		}
	}

}
