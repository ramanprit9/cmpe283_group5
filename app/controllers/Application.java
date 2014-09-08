package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;

import models.DataAccess;
import models.Service;
import models.User;
import models.Website;
import models.WebsiteHandler;
import play.Logger;
import play.api.mvc.Session;
import play.db.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
    	
    	
    	ArrayList<String> webservices = new ArrayList<String>();
    	ArrayList<String> dbservices = new ArrayList<String>();
    	
    	webservices.add("jason");
    	webservices.add("website");
    	
    	dbservices.add("mysql");
    	dbservices.add("postgres");
    	
        return ok(index.render("Hello World!"));
    }
    
    public static Result hello() {
        return ok("Hello World");
    }
    
    public static Result dashboard(){
    	
    	DataAccess db = new DataAccess();
    	HashMap<String,ArrayList<String>> allservices = new HashMap<String,ArrayList<String>>();
    	ArrayList<String> webservices = new ArrayList<String>();
    	ArrayList<String> dbservices = new ArrayList<String>();
    	try {
			allservices = db.getAll();
			if(allservices != null){
				webservices = allservices.get("webservice");
				dbservices = allservices.get("dbservice");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ok(dashboard.render(webservices,dbservices));
    }
    public static Result createWebsite() throws SQLException {
    	
    	String name = null;
    	String plan = null;
    	WebsiteHandler job = null;
    	int status = 0;
    	DataAccess db = new DataAccess();
    	Service service = new Service();
    	Date date = new Date();
    	JsonNode json = request().body().asJson();
    	Logger.debug("json request is:"+ json);
    	
    	
    	//retrieving user session
    	String user = session("username");
    	String uid = session("uid");
    	Logger.debug("User ID: "+ user);
   	
    	if(json != null) {
    	    name = json.findPath("name").textValue();
    	    service.setServicename(name);
    	    plan = json.findPath("plan").textValue();
    	    //determining service type
    	    if(plan.equals("gold")){
    	    	service.setServicetype("smallWebsite");
    	    }else{
    	    	service.setServicetype("bigWebsite");
    	    }
    	    status = db.saveWebsitetoServices(uid, service.getServicename(), service.getServicetype(),date.toString(), json.toString());
    	    service = db.getServiceByName(service.getServicename());
    	    String jobjson = Json.toJson(service).toString();
    	    job = new WebsiteHandler(service);
    	    status = job.save(jobjson);
    	  }
    	return ok(createWebsite.render("Hello World"));
    }
    public static Result signup(){
    	return ok(signup.render("Hello World"));
    }
    public static Result createuser(){
    	
    	Form<User> UserForm = Form.form(User.class);
    	User user = UserForm.bindFromRequest().get();
        DataAccess da = new DataAccess();
        try {
        	int status = 0;
			status = da.createUser(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return ok(index.render("User Created"));
    }
    public static Result login(){
    	return ok(login.render("Hello World"));
    }
    public static Result loginUser(){
    	
    	Form<User> UserForm = Form.form(User.class);
    	User user = UserForm.bindFromRequest().get();
    	DataAccess da = new DataAccess();
    	User user1 = new User();
    	try {
			user1 = da.getUser(user);
			if(user1!= null)
			{
				session("username", user.getUsername());
				session("uid", user.getUid());
				return redirect("/dashboard");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return ok(login.render("Hello World"));
    }
    
}
