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
			    }catch (SQLException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        finally{
		        	try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }
				}
			};
			Timer timer = new Timer();
			long delay = 0;
			long intevalPeriod = 1 * 1000;
			// schedules the task to be run in an interval
			timer.scheduleAtFixedRate(task, delay,
			intevalPeriod);
	}*/
}


