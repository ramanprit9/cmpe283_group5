package models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import play.db.DB;

public class DataAccess {
	Statement stmt = null;
    Connection connection = null;
    
    public int saveWebsitetoServices(String user, String name, String servicetype, String json) throws SQLException{
    	int status = 0;
    	Date date = new Date();
    	connection = DB.getConnection();
        try {
			stmt = connection.createStatement();
			String sql;
	        sql = "INSERT INTO services VALUES('"+ user +"','"+servicetype +","+ "'created'"+","+ date.toString() + json +")";
	        System.out.println("sql statement:-->"+ sql);
	        status = stmt.executeUpdate(sql);
	        connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return status;
    }

}
