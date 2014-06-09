Opendroid DAO
=============

A simple ORM library for android, that helps you to implement DAO pattern for your data access in your android project.

STEP 1:
Create Your Models for each table which implements `DbModel` interface

Implementing `DbModel` needs overriding four methods `getId()`, `setId()`, `getTableName()` and `getCreateStatement()`. Getter and setter related to id are for primary key purpose, `getTableName()` should return table name for respective binding in form of `String`, which can either reside in your current model class or any other `String` variable or constant within your project.

Following code illustrates a simple example to create a model.

```java
class MyTableModel implements DbModel{
    private int id;
	private String userImage;
	private String userName;
	private String postTitle;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	@Override
	public String getTableName() {
		return MyTableDAO.TABLE_NAME;
	}

	@Override
	public String getCreateStatement() {
		return MyTableDAO.CREATE_TABLE;
	}

}
```
2) Create your `DAO` classes for corresponding `Models`

```java
class MyTableDAO extends BaseDAO<MyTableModel>{
	public static final String ID = "_id";
	public static final String USER_IMAGE = "user_image";
	public static final String USER_NAME = "user_name";
	public static final String POST_TITLE = "post_title";
	public static final String TABLE_NAME = "postDB";
	public static final String CREATE_TABLE = TABLE_NAME + " (" 
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ USER_IMAGE + " TEXT, "
			+ USER_NAME + " TEXT, "
			+ POST_TITLE + " TEXT);";

	public MyTableDAO(Context context, SQLiteDatabase db) {
		super(context, db);
	}

	@Override
	public MyTableModel findByPrimaryKey(Long id) throws DAOException {
		//write your own logic if needed
		return null;
	}

	@Override
	public void delete(Long id) throws DAOException {
		//write your own logic if needed
	}

	@Override
	public boolean exists(Long id) throws DAOException {
		//write your own logic if needed
		return false;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public Datum fromCursor(Cursor c) {
		MyTableModel model = new MyTableModel();
		model.setId(CursorUtils.extractIntOrNull(c, WallPostDAO.ID));
		model.setUserImage(CursorUtils.extractStringOrNull(c, WallPostDAO.USER_IMAGE));
		model.setUserName(CursorUtils.extractStringOrNull(c, WallPostDAO.USER_NAME));
		model.setPostTitle(CursorUtils.extractStringOrNull(c, WallPostDAO.POST_TITLE));
		return model;
	}

	@Override
	public ContentValues values(Datum t) {
		ContentValues values = new ContentValues();
		values.put(USER_IMAGE, t.getUserImage());
		values.put(USER_NAME, t.getUserName());
		values.put(POST_TITLE, t.getPostTitle());
		return values;
	}

}
```
3) Initialise your Database in your `Launcher Activity`
```java
	private final void initDatabase(){
		List<DbModel> models = new ArrayList<DbModel>();
		models.add(new MyTableModel());
		models.add(new MyModelTwo());
		DbConfiguration.Builder config = new Builder()
		.setDatabaseName("MyDb.db")
		.setModels(models);
		DbHelper.init(this, config.build());//returns instance of DbHelper which is Singleton
	}
```

To make database operations you can get instance of `DbHelper` by using `DbHelper.getInstance(context);`

To perfom Operations on perticular table :
```java
MyTableDAO myTableDAO = new MyTableDAO(context,DbHelper.getInstance(context).getSQLiteDatabase());
```
on `myTableDao` you can call the method you need to perform any operation.
