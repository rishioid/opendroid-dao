opendroid-dao
=============

DAO library for android

1) Initialise you database in your Application Class

`JAVA
private final void initDatabase(){
		List<DbModel> models = new ArrayList<DbModel>();
		models.add(new MyModelOne());
		models.add(new MyModelTwo());
		DbConfiguration.Builder config = new Builder()
		.setDatabaseName("MyDb.db")
		.setModels(models);
		DbHelper.init(this, config.build());
	}`
