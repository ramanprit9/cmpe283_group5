package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
			sql = "INSERT INTO services(servicename,uid,servicetype,status,datecreated,json) VALUES('"+ servicename +"',"+ uid +",'"+servicetype +"',"+ "'created'"+",'"+ date.toString() +"','"+json +"')";
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
	public Services getServiceByName(String name) throws SQLException{
		Logger.debug("------Entered into getServiceByName("+name+") method in DataAccess------");
		Connection conn = DB.getConnection();
		Services service = new Services();
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM services WHERE servicename='"+name+ "'";
			ResultSet rs = stmt.executeQuery(sql);
			Logger.debug("fetching service details: "+sql);
			while(rs.next()){
				//Retrieve by column name
				service.setServiceid(rs.getString("serviceid"));
				service.setServicename(rs.getString("servicename"));
				Integer uid = rs.getInt("uid");
				service.setUid(uid.toString());
				service.setServicetype(rs.getString("servicetype"));
				service.setStatus(rs.getString("status"));
				service.setDatecreated(rs.getString("datecreated"));
				service.setJson(rs.getString("json"));
			}
			rs.close();
			conn.close();
		} catch (Exception e) {
			Logger.error("ERROR while fetching service details from services table: "+ e.getMessage());
		}
		finally{
			conn.close();
		}
		Logger.debug("------Exit from getServiceByName() method in DataAccess------");
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
			sql= "INSERT INTO jobs(jobclassname,json) VALUES('models.WebsiteHandler','"+json + "')";
			status = stmt.executeUpdate(sql);
			Logger.debug("inserting into jobs table: "+sql);
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
	public int deleteJob(int jobid) throws SQLException{

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

				if(servicetype.contains("Website")){
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
	public int addResource(String resourcetype , Resources resource, Services service) throws SQLException{

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
	public int startResource(int userid, int serviceid, int resourceid) throws SQLException {

		Logger.debug("------Entered into startResource() method in DataAccess ------- to log start time for resource");

		int status =0;
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql= "INSERT INTO resource_usage VALUES("+userid+", "+serviceid+", "+resourceid+ ", CURRENT_TIMESTAMP, NULL)";
			status = stmt.executeUpdate(sql);
			Logger.debug("inserting into resource_usage table: "+sql);
			connection.close();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from startResource() method in DataAccess -------");
		return status;
	}

	public int stopResource(int resourceid) throws SQLException {

		Logger.debug("------Entered into stopResource() method in DataAccess -------to log stop time for resource");

		int status =0;
		connection = DB.getConnection();
		try{
			stmt = connection.createStatement();
			String sql;
			sql = "UPDATE resource_usage SET endtime=CURRENT_TIMESTAMP WHERE"+
					" resourceid="+resourceid+" AND endtime IS NULL";
			status = stmt.executeUpdate(sql);
			Logger.debug("updating resource_usage table: "+sql);
			status = stmt.executeUpdate(sql);
			connection.close();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			connection.close();
		}
		Logger.debug("------Exit from stopResource() method in DataAccess -------");
		return status;
	}   

	//TODO: Update the web pages to update the total hours and total cost of the resource
	public void refreshResource(int resourceid) throws SQLException {

		Logger.debug("------Entered into refreshResource() method in DataAccess -------to update resource running time and cost");

		Double hours = getTotalResourceHours(resourceid);
		Double cost = getTotalCost(hours);

		Logger.debug("*****Resource: Total hours = "+hours +"    Total cost = "+cost);
		Logger.debug("------Exit from refreshResource() method in DataAccess -------");

	}

	//TODO: Update the web pages to update the total hours and total cost of the service
	public void refreshService(int serviceid) throws SQLException {

		Logger.debug("------Entered into refreshService() method in DataAccess -------to update service running time and cost");

		Double hours = getTotalServiceHours(serviceid);
		Double cost = getTotalCost(hours);

		Logger.debug("Service usage: Total hours = "+hours +"    Total cost = "+cost);
		Logger.debug("------Exit from refreshService() method in DataAccess -------");
	}

	public Double getTotalResourceHours(int resourceid) throws SQLException {

		Logger.debug("------Entered into getTotalResourceHours() method in DataAccess -------to get total hours for the resource");

		String sql = "SELECT * FROM resource_usage WHERE resourceid="+resourceid;
		ResultSet rs = stmt.executeQuery(sql);
		Timestamp start;
		Timestamp end;
		long starttime;
		long endtime;
		long totaltime = 0;
		Double totalhours = 0.0;

		try {
			while(rs.next()){
				//Retrieve by column name
				start  = Timestamp.valueOf(rs.getString("starttime"));
				if ((rs.getString("endtime")) != null) {
					end = Timestamp.valueOf(rs.getString("endtime"));
				}
				else {
					//If service is still running, get the current timestamp
					Date date = new Date();
					end = new Timestamp(date.getTime());
				}

				//convert Timestamp to long
				starttime = start.getTime();
				endtime = end.getTime();

				Logger.debug("Resource Timestamp: start = "+start+"      end="+end);
				totaltime = totaltime + (endtime - starttime);

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

		/*
		 * totaltime is in milliseconds format, convert to hours format
		 * First, convert to seconds by dividing by 1000
		 * Second, convert to minutes by dividing by 60
		 * Third, convert to hours by dividing by 60
		 */
		totalhours = Long.valueOf(totaltime).doubleValue();
		totalhours = ((totalhours/1000)/60)/60;

		//round up totalhours to 2 decimal places
		totalhours = Math.round(totalhours*100.0)/100.0;

		Logger.debug("------Exit from getTotalResourceHours() method in DataAccess -------");
		return totalhours;
	}

	public Double getTotalServiceHours(int serviceid) throws SQLException {

		Logger.debug("------Entered into getTotalServiceHours() method in DataAccess -------to get total hours for the service");

		String sql = "SELECT * FROM resource_usage WHERE serviceid="+serviceid;
		ResultSet rs = stmt.executeQuery(sql);
		Timestamp start;
		Timestamp end;
		long starttime;
		long endtime;
		long totaltime = 0;
		Double totalhours = 0.0;

		try {
			while(rs.next()){
				//Retrieve by column name
				start  = Timestamp.valueOf(rs.getString("starttime"));
				if ((rs.getString("endtime")) != null) {
					end = Timestamp.valueOf(rs.getString("endtime"));
				}
				else {
					//If service is still running, get the current timestamp
					Date date = new Date();
					end = new Timestamp(date.getTime());
				}

				//convert Timestamp to long
				starttime = start.getTime();
				endtime = end.getTime();

				Logger.debug("Service Timestamp: start = "+start+"      end="+end);
				totaltime = totaltime + (endtime - starttime);

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

		/*
		 * totaltime is in milliseconds format, convert to hours format
		 * First, convert to seconds by dividing by 1000
		 * Second, convert to minutes by dividing by 60
		 * Third, convert to hours by dividing by 60
		 */
		totalhours = Long.valueOf(totaltime).doubleValue();
		totalhours = ((totalhours/1000)/60)/60;

		//round up totalhours to 2 decimal places
		totalhours = Math.round(totalhours*100.0)/100.0;

		Logger.debug("------Exit from getTotalServiceHours() method in DataAccess -------");
		return totalhours;
	}

	/*
	 * This method can either be used to get total cost of service or resource
	 * First either call getTotalServiceHours or getTotalResourceHours to get
	 * the hours then call this method to get the total cost.
	 */
	public Double getTotalCost(Double hours) {

		Logger.debug("------Entered into getTotalCost() method in DataAccess -------to get total cost for the service");

		Double total;
		total = hours * 25;

		//Round up total to 2 decimal places
		total = Math.round(total*100.0)/100.0;

		Logger.debug("------Exit from getTotalCost() method in DataAccess -------");
		return total;
	}

}
