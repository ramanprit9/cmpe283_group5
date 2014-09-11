package models;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import play.Logger;
import play.db.DB;


public class BackgroundScheduler {

	/*public void jobs(){

		Logger.debug("------Entered into jobs() method in background scheduler------");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Statement stmt = null;
				Connection connection = null;
				try{
					connection = DB.getConnection();
					stmt = connection.createStatement();
					String sql;
					sql = "SELECT * FROM JOBS";
					ResultSet rs = stmt.executeQuery(sql);
					while(rs.next()){
						//Retrieve by column name
						String jobclassname  = rs.getString("jobclassname");

						Class c = Class.forName(jobclassname);
						c.newInstance().getClass().getDeclaredMethod("process",null).invoke(null);
						//c.getDeclaredMethod("process").invoke(null);
					}
					rs.close();
					connection.close();
				}catch (SQLException sqle) {
					Logger.error("Error while fetching jobs from database: "+ sqle.getMessage());
				} catch (IllegalAccessException e) {
					Logger.error("ERROR while creating instance for job class: "+ e.getMessage());
				} catch (IllegalArgumentException e) {
					Logger.error("ERROR while invoking jobclass method: "+ e.getMessage());
				} catch (InvocationTargetException e) {
					Logger.error("ERROR while invoking jobclass method: "+ e.getMessage());
				} catch (NoSuchMethodException e) {
					Logger.error("ERROR while invoking method process: "+ e.getMessage());
				} catch (SecurityException e) {
					Logger.error("ERROR while invoking method process: "+ e.getMessage());
				} catch (InstantiationException e) {
					Logger.error("ERROR while creating instance for job class: "+ e.getMessage());
				}catch (ClassNotFoundException e){
					Logger.error("Class Not Found: "+ e.getMessage());
				}
				finally{
					try {
						connection.close();
					} catch (SQLException se) {
						Logger.error("ERROR while closing connection: "+ se.getMessage());
					}
				}
			}
		};
		Timer timer = new Timer();
		long delay = 0;
		long intevalPeriod = 1 * 1000;
		// schedules the task to be run in an interval
		timer.scheduleAtFixedRate(task, delay,intevalPeriod);
		Logger.debug("------Exit from jobs() method in background scheduler------");
	}*/
}


