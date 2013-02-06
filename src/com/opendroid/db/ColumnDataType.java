package com.opendroid.db;

public enum ColumnDataType {
	NONE(null),
	TEXT("TEXT"),
	INTEGER("INT"),
	REAL("REAL");
	
	private final String value;
	ColumnDataType(String value)
	{
		this.value = value;
	}
};
