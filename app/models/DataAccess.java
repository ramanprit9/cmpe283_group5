package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import play.Logger;
import play.db.DB;

public class DataAccess {
	Statement stmt = null;
    Connection connection = null;
    
    public int saveWebsitetoServices(String uid, String servicename, String servicetype,String date, String json) throws SQLException{
    	int status = 0;
    	connection = DB.getConnection();
        try {
			stmt = connection.createStatement();
			String sql;
	        sql = "INSERT INTO services VALUES('"+ uid +"','"+ servicename +"','"+servicetype +","+ "'created'"+","+ date.toString() + json +")";
	        Logger.debug("Inserting into services table: " + sql);
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
    public Service getServiceByName(String name) throws SQLException{
    	int status = 0;
    	connection = DB.getConnection();
    	Service service = new Service();
        try {
			stmt = connection.createStatement();
			String sql;
	        sql = "SELECT * FROM services WHERE servicename="+name;
	        ResultSet rs = stmt.executeQuery(sql);
    		while(rs.next()){
    	         //Retrieve by column name
    	         String serviceid  = rs.getString("serviceid");
    	         Integer uid = rs.getInt("uid");
    	         service.setUid(uid.toString());
    	         service.setJson(rs.getString("json"));
    	         service.setDatecreated(rs.getString("datecreated"));
    	         service.setServiceid(serviceid);
    	         service.setServicetype(rs.getString("servicetype"));
    	         service.setStatus(rs.getString(status));
    	         service.setServicename(rs.getString("servicename"));
    	      }
    	      rs.close();
	        connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return service;
    }
    public int createUser(User user) throws SQLException{
    	int status =0;
    	connection = DB.getConnection();
    	try{
    		stmt = connection.createStatement();
    		String sql;
    		sql = "INSERT INTO users VALUES('"+user.getUsername()+"','"+user.getPassword()+")";
    		Logger.debug("inserting into users table: "+sql);
    		status = stmt.executeUpdate(sql);
    		connection.close();
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return status;
    }
    public User getUser(User user) throws SQLException{
    	connection = DB.getConnection();
    	try{
    		stmt = connection.createStatement();
    		String sql;
    		sql = "SELECT * FROM USERS WHERE username=" + user.getUsername() +"AND password="+ user.getPassword()+")";
    		Logger.debug("inserting into users table: "+sql);
    		ResultSet rs = stmt.executeQuery(sql);
    		while(rs.next()){
    	         //Retrieve by column name
    	         Integer id  = rs.getInt("uid");
    	         user.setUid(id.toString());
    	         user.setUsername(rs.getString("username"));
    	      }
    	      rs.close();
    		connection.close();
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return user;
    }
    public int saveWebsiteJob(String json) throws SQLException{
    	int status =0;
    	connection = DB.getConnection();
    	try{
    		stmt = connection.createStatement();
    		String sql;
    		sql= "INSERT INTO JOBS  VALUES('WebsiteHandler.class',"+json + ")";
			status = stmt.executeUpdate(sql);
    		Logger.debug("inserting into jobs table: "+sql);
    		status = stmt.executeUpdate(sql);
    		connection.close();
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return status;
    }
    public int deleteWebsiteJob(String jobid) throws SQLException{
    	int status =0;
    	connection = DB.getConnection();
    	try{
    		stmt = connection.createStatement();
    		String deleteSQL = "DELETE FROM JOBS WHERE jobid ="+jobid;
			status = stmt.executeUpdate(deleteSQL);
    		Logger.debug("inserting into jobs table: "+deleteSQL);
    		status = stmt.executeUpdate(deleteSQL);
    		connection.close();
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return status;
    }
    
    public HashMap<String,ArrayList<String>> getAll() throws SQLException{
    	
    	ArrayList<String> webservice = new ArrayList<String>();
    	ArrayList<String> dbservice = new ArrayList<String>();
    	HashMap<String,ArrayList<String>> allservices = new HashMap<String,ArrayList<String>>();
    	
    	connection = DB.getConnection();
    	try{
    		stmt = connection.createStatement();
    		String sql;
    		sql = "SELECT * FROM SERVICES";
    		ResultSet rs = stmt.executeQuery(sql);
    		while(rs.next()){
    	         //Retrieve by column name
    			
    	         String servicetype  = rs.getString("servicetype");
    	         String servicename = rs.getString("servicename");
    	         
    	         if(servicetype.contains("website")){
    	        	 webservice.add(servicename);
    	         }
    	         if(servicetype.contains("database")){
    	        	 dbservice.add(servicename);
    	         }
    	      }
    		allservices.put("webservice", webservice);
    		allservices.put("dbservice",dbservice);
    	      rs.close();
    		connection.close();
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        return allservices;
    }

}
