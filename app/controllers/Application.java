package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import models.DataAccess;
import models.Service;
import models.User;
import models.WebsiteHandler;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static Result index() {

		Logger.debug("------Entered into index() method in Application Controller------");
		ArrayList<String> webservices = new ArrayList<String>();
		ArrayList<String> dbservices = new ArrayList<String>();

		webservices.add("jason");
		webservices.add("website");

		dbservices.add("mysql");
		dbservices.add("postgres");
		Logger.debug("------Entered into index() method in Application Controller------");
		return ok(dashboard.render(webservices,dbservices , session("username")));
	}

	public static Result dashboard(){
		DataAccess db = new DataAccess();
		HashMap<String,ArrayList<String>> allservices = new HashMap<String,ArrayList<String>>();
		ArrayList<String> webservices = new ArrayList<String>();
		ArrayList<String> dbservices = new ArrayList<String>();
		int userid = Integer.parseInt(session("uid"));
		Logger.debug("user id is: "+ userid);
		try {
			allservices = db.getAllServices(userid);
			if(allservices != null){
				webservices = allservices.get("webservice");
				dbservices = allservices.get("dbservice");
			}
		} catch (Exception e) {
			Logger.error("ERROR While fetching services from database: "+ e.getMessage());
		}

		return ok(dashboard.render(webservices,dbservices , session("username")));
	}
	public static Result createWebsite()  {

		Logger.debug("------Entered into createWebsite() method in Application Controller------");
		String name = null;
		String plan = null;
		WebsiteHandler job = null;
		DataAccess db = new DataAccess();
		Service service = new Service();
		Date date = new Date();
		JsonNode json = request().body().asJson();
		Logger.debug("json request is:"+ json);
		//retrieving user session
		String user = session("username");
		String uid = session("uid");

		try{
			
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
				int status = db.saveToServices(uid, service.getServicename(), service.getServicetype(),date.toString(), json.toString());
				if(status == 0){
					throw new Exception("Could not create service");
				}
				service = db.getServiceByName(service.getServicename());
				if(service == null){
					throw new Exception("service doesnot exist");
				}
				String jobjson = Json.toJson(service).toString();
				job = new WebsiteHandler(service);
				int jobstatus = job.save(jobjson);
				if(jobstatus ==0){
					throw new Exception("Could not create job");
				}
			}
		}catch (Exception e) {
			Logger.error("ERROR while fetching or saving service details from database: "+ e.getMessage());
			return ok(createWebsite.render(e.getMessage(), user , uid));
		}

		Logger.debug("------Exit from createWebsite() method in Application Controller------");
		return ok(createWebsite.render("success", user, uid));
	}
	public static Result signup(){
		Logger.debug("------Entered into signup() method in Application Controller------");

		Logger.debug("------Exit from signup() method in Application Controller------");
		return ok(signup.render(" "));
	}
	public static Result createuser(){

		Logger.debug("------Entered into createuser() method in Application Controller------");
		Form<User> UserForm = Form.form(User.class);
		User user = UserForm.bindFromRequest().get();
		DataAccess da = new DataAccess();
		try {
			int status = 0;
			status = da.createUser(user);
			if(status == 0){
				throw new Exception("Could not create user");
			}
		} catch (Exception e) {
			Logger.error("ERROR while creating user: "+ e.getMessage());
			return ok(login.render(e.getMessage()));
		}
		Logger.debug("------Exit from createuser() method in Application Controller------");
		return ok(login.render("User Created"));
	}
	public static Result login(){
		Logger.debug("------Entered into login() method in Application Controller------");

		Logger.debug("------Exit from login() method in Application Controller------");
		return ok(login.render("Login Page"));
	}
	public static Result loginUser(){

		Logger.debug("------Entered into loginUser() method in Application Controller------");
		Form<User> UserForm = Form.form(User.class);
		User user = UserForm.bindFromRequest().get();
		DataAccess da = new DataAccess();
		
		/*webservices.add("website");

		dbservices.add("mysql");
		dbservices.add("postgres");*/
		User user1 = new User();
		try {
			user1 = da.getUser(user);
			if(user1!= null)
			{
				session("username", user.getUsername());
				session("uid", user.getUid().toString());
				return redirect("/dashboard");
			}else{
				throw new Exception("user doesnot exist");
			}
		} catch (Exception e) {
			Logger.error("ERROR while fetching user details from database: "+ e.getMessage());
		}
		Logger.debug("------Exit from loginUser() method in Application Controller------");
		return redirect("/dashboard");
	}
	 /*public static Result bigWebService() {
	        return ok(bigWebService.render("Hello World"));
	    }
	   
	    public static Result smallWebService() {
	        return ok(websiteDetails.render("Hello World"));
	    }
	   
	    public static Result dbService() {
	        return ok(dbService.render("Hello World"));
	    }*/
	

}
