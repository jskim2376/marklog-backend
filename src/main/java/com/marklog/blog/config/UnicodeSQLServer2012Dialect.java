package com.marklog.blog.config;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;

public class UnicodeSQLServer2012Dialect extends SQLServer2012Dialect {
	public UnicodeSQLServer2012Dialect() {
		super();
		registerColumnType(Types.VARCHAR, "nvarchar(MAX)");
	}
}
