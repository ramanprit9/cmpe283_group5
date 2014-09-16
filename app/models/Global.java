package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.db.DB;
import play.libs.Akka;
import play.libs.Json;

public class Global extends GlobalSettings {

	/*OSClient os = OSFactory.builder()
			.endpoint("http://192.168.0.19:5000/v2.0")
			.credentials("admin","test123")
			.tenantName("admin")
			.authenticate();*/
	  @Override
	  public void onStart(Application app) {
		  Logger.info("Inside Global Class");
		  
	    FiniteDuration delay = FiniteDuration.create(0, TimeUnit.SECONDS);
	    FiniteDuration frequency = FiniteDuration.create(5, TimeUnit.SECONDS);

	    Runnable showTime = new Runnable() {
	       @Override
	       public void run() {
	          System.out.println("Time is now: " + new Date());
	          Connection connection = DB.getConnection();
	        	  try{
	              Statement stmt = connection.createStatement();
                  String sql = "SELECT * FROM jobs";
                  Logger.debug("************* About to get jobs **************");
                  ResultSet rs = stmt.executeQuery(sql);
                  while(rs.next()){
                      //Retrieve by column name
                      String jobclassname  = rs.getString("jobclassname");
                      int id = rs.getInt("jobid");
                      ObjectNode result = Json.newObject();
                      result = (ObjectNode) Json.parse(rs.getString("json"));
                      Class c = Class.forName(jobclassname);
                      Object object = c.newInstance();
                      object.getClass().getDeclaredMethod("process", int.class, String.class).invoke(object , id, result.get("servicename").asText());
                     
                      //sql = "DELETE FROM jobs WHERE jobid = " + id;
                  }
                  rs.close();
	        	  }catch(Exception e){
	        		  e.printStackTrace();
	        	  }
	        	  finally{
	        		  try {
						connection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	  }
	       }
      };
     
	    Akka.system().scheduler().schedule(delay, frequency, showTime, Akka.system().dispatcher());
	  }  
}
