/**
 * This software is the confidential and proprietary information of

 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */
package com.dam.ps.util;

import java.sql.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

 import net.sf.json.JSONObject;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * This class will map the JDBC ResultSet to a JSON structure
 * 
 */
public class JSONMapper implements RowMapper<JSONObject> {
	public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
		String key = null;
		String value = null;
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		JSONObject result = new JSONObject();
	    for (int index = 1; index <= columnCount; index++) {
	    	 key = JdbcUtils.lookupColumnName(rsmd, index);
	    	 value = rs.getString(key);
	    	 result.accumulate(key, value);
        }
		return result;
	}

}
