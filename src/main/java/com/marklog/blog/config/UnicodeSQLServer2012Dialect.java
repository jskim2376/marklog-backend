package com.marklog.blog.config;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;

public class UnicodeSQLServer2012Dialect extends SQLServer2012Dialect {
	    public UnicodeSQLServer2012Dialect() {
	        super();

	        // Use Unicode Characters
	        registerColumnType(Types.VARCHAR, 255, "nvarchar($l)");
	        registerColumnType(Types.VARCHAR, 50, "nvarchar($l)");
	        registerColumnType(Types.CHAR, "nchar(1)");
	        registerColumnType(Types.CLOB, "nvarchar(max)");

	        // Microsoft SQL Server 2000 supports bigint and bit
	        registerColumnType(Types.BIGINT, "bigint");
	        registerColumnType(Types.BIT, "bit");
	    }
}
