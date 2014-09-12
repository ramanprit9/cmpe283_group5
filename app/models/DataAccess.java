package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import play.Logger;
import play.db.DB;

public class DataAccess {
	Statement stmt = null;
	Connection connection = null;

	public int saveToServices(String uid, String servicename, String servicetype,String date, String json) throws SQLException{

		Logger.debug("------Entered into saveToServices("+uid+","+servicename+","+servicetype+","+date+","+json+") method in DataAccess------");
		int status = 0;
		connection = DB.getConnection();
		try {
			stmt = connection.createStatement();
			String sql;
			sql = "INSERT INTO services VALUES('"+ uid +"','"+ servicename +"','"+servicetype +","+ "'created'"+","+ date.toString() + json +")";
			Logger.debug("Inserting into services table: " + sql);
			status = stmt.executeUpdate(sql);
			connection.close();
		} catch (Exception e) {
			Logger.error("ERROR while saving website to service table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from saveToServices() method in DataAccess------");
		return status;
	}
	public Service getServiceByName(String name) throws SQLException{
		Logger.debug("------Entered into getServiceByName("+name+") method in DataAccess------");
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
		} catch (Exception e) {
			Logger.error("ERROR while fetching service details from services table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Entered into getServiceByName() method in DataAccess------");
		return service;
	}
	public int createUser(User user) throws SQLException {

		Logger.debug("------Entered into createUser() for "+user.getUsername());
		int status =0;
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql = "INSERT INTO users(uname,pwd) VALUES('"+user.getUsername()+"','"+user.getPassword()+"')";
			Logger.debug("inserting into users table: "+sql);
			status = stmt.executeUpdate(sql);
			connection.close();
		}catch (Exception e) {
			Logger.error("ERROR while creating new user to users table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from createUser() method in DataAccess");
		return status;
	}
	public User getUser(User user) throws SQLException{

		Logger.debug("------Entered into getUser() for "+user.getUsername());
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql = "SELECT * FROM users WHERE uname='" + user.getUsername() +"' AND pwd='"+ user.getPassword()+"'";
			Logger.debug("inserting into users table: "+sql);
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				//Retrieve by column name
				user.setUid(rs.getInt("uid"));
				user.setUsername(rs.getString("uname"));
			}
			rs.close();
			
			connection.close();
		}catch (Exception e) {
			Logger.error("ERROR while fetching user details from users table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from getUser() method in DataAccess");
		return user;
	}
	public int saveToJob(String json) throws SQLException{

		Logger.debug("------Entered into saveToJob()--json: +"+json);
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
		}catch (Exception e) {
			Logger.error("ERROR while saving service to jobs table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from saveToJob() method in DataAccess");
		return status;
	}
	public int deleteJob(String jobid) throws SQLException{

		Logger.debug("------Entered into deleteJob()--jobid: +"+jobid);
		int status =0;
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String deleteSQL = "DELETE FROM JOBS WHERE jobid ="+jobid;
			status = stmt.executeUpdate(deleteSQL);
			Logger.debug("inserting into jobs table: "+deleteSQL);
			status = stmt.executeUpdate(deleteSQL);
			connection.close();
		}catch (Exception e) {
			Logger.error("ERROR while deleting service from jobs table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from deleteJob() method in DataAccess");
		return status;
	}

	public HashMap<String,ArrayList<String>> getAllServices(Integer uid) throws SQLException{

		Logger.debug("------Entered into getAllServices() method in DataAccess -------fetch all existing services for user");
		ArrayList<String> webservice = new ArrayList<String>();
		ArrayList<String> dbservice = new ArrayList<String>();
		HashMap<String,ArrayList<String>> allservices = new HashMap<String,ArrayList<String>>();

		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql = "SELECT * FROM services WHERE uid="+uid;
			Logger.debug("getting all services: "+sql);
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
		}catch (Exception e) {
			Logger.error("ERROR while fetching all the services for the user from services table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from getAllServices() method in DataAccess -------");
		return allservices;
	}
	public int addResource(String resourcetype , Resource resource, Service service) throws SQLException{

		Logger.debug("------Exit from addResource() method in DataAccess -------");
		int status =0;
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql= "INSERT INTO resources VALUES("+service.getServiceid()+"'"+resourcetype+"'"+"'"+resource.getStatus()+"'"+service.getDatecreated()+"'"+resource.getJson()+"')";
			status = stmt.executeUpdate(sql);
			Logger.debug("inserting into resources table: "+sql);
			status = stmt.executeUpdate(sql);
			connection.close();
		}catch (Exception e) {
			Logger.error("ERROR while saving resource to resources table: "+ e.getMessage());
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from addResource() method in DataAccess -------");
		return status;
	}

}
