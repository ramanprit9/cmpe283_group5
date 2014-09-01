package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;

import models.Website;
import models.WebsiteHandler;
import play.Logger;
import play.db.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your not so new application is ready."));
    }
    
    public static Result hello() throws SQLException {
    	Form<Website> websiteForm = Form.form(Website.class);
    	Website website = websiteForm.bindFromRequest().get();
        String name = website.getName();
        String plan = website.getPlan();
        Statement stmt = null;
        int status = 0;
        Connection connection = DB.getConnection();
        try {
			stmt = connection.createStatement();
			String sql;
	        sql = "INSERT INTO nameplan VALUES('"+ website.getName()+"','"+ website.getPlan()+"')";
	        System.out.println("sql statement:-->"+ sql);
	        status = stmt.executeUpdate(sql);
	       /* ResultSet rs = stmt.executeQuery(sql);
	        while(rs.next()){
	            //Retrieve by column name
	            int id  = rs.getInt("id");
	            int age = rs.getInt("age");
	            String first = rs.getString("first");
	            String last = rs.getString("last");

	            //Display values
	            System.out.print("ID: " + id);
	            System.out.print(", Age: " + age);
	            System.out.print(", First: " + first);
	            System.out.println(", Last: " + last);
	         }
	        rs.close();*/
	        if(status == 0){
	        	System.out.println("Successfully created");
	        }
	        connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        
        //DataSource ds = DB.getDatasource();
        
        return ok(Json.toJson(website));
    }
    public static Result helloAll() throws SQLException{
    	Statement stmt = null;
    	String name = "";
    	String plan = "";
    	Connection connection = DB.getConnection();
        try {
			stmt = connection.createStatement();
			String sql;
	        sql = "SELECT * FROM nameplan";
	        System.out.println("sql statement:-->"+ sql);
	        ResultSet rs = stmt.executeQuery(sql);
	        while(rs.next()){
	            //Retrieve by column name
	            name  = rs.getString("name");
	            plan = rs.getString("plan");

	            //Display values
	            System.out.print("NAME: " + name);
	            System.out.print(", PLAN: " + plan);
	            
	         }
	        rs.close();
	        connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	connection.close();
        }
        
        //DataSource ds = DB.getDatasource();
    	return ok( "Hello:"+name +"and" +plan);
    }
    public static Result createWebsite() throws SQLException{
    	
    	String name = null;
    	String plan = null;
    	String servicetype = null;
    	
    	JsonNode json = request().body().asJson();
    	Logger.debug("json request is:"+ json);
    	
    	//retrieving user session
    	String user = session("connected");
    	Logger.debug("User ID: "+ user);
   	
    	if(json != null) {
    	    name = json.findPath("name").textValue();
    	    plan = json.findPath("plan").textValue();
    	    //determining service type
    	    if(plan.equals("gold")){
    	    	servicetype = "smallWebsite";
    	    }else{
    	    	servicetype = "bigWebsite";
    	    }
    	    WebsiteHandler job = new WebsiteHandler(servicetype);
    	    job.save(json.toString());
    	  }
    	return ok("Hello " + name);
    }

}
