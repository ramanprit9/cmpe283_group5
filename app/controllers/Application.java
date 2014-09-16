package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.BackgroundScheduler;
import models.DataAccess;
import models.DatabaseHandler;
import models.Services;
import models.User;
import models.Website;
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
		//BackgroundScheduler backgroundScheduler = new BackgroundScheduler();
		//backgroundScheduler.jobs();
		Logger.debug("------Entered into index() method in Application Controller------");
		return ok(" ");
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
	public static Result showcreateWebsite(){
		Logger.debug("------Entered into showcreateWebsite() method in Application Controller------");

		Logger.debug("------Exit from showcreateWebsite() method in Application Controller------");
		return ok(createWebsite.render(" ", session("username"),session("uid")));
	}
	public static Result createWebsite() {

		String name = null;
		String plan = null;
		Logger.debug("------Entered into createWebsite() method in Application Controller------");
		//WebsiteHandler job = null;
		DataAccess db = new DataAccess();
		Services service = new Services();
		Date date = new Date();
		Form<Website> WebsiteForm = Form.form(Website.class);
		Website website = WebsiteForm.bindFromRequest().get();
		ObjectNode json = Json.newObject();
		json.put("name", website.getName());
		json.put("plan", website.getPlan());
		Logger.debug("json request is:"+ json);
		//retrieving user session
		String user = session("username");
		String uid = session("uid");

		try{

			Logger.debug("UserName: "+ user);
			Logger.debug("name: "+ name);
			Logger.debug("plan: "+ plan);

			if(json != null) {
				name = json.findPath("name").textValue();
				service.setServicename(name);
				plan = json.findPath("plan").textValue();
				service.setServicetype(plan);

				int status = db.saveToServices(uid, service.getServicename(), service.getServicetype(),date.toString(), json.toString());
				if(status == 0){
					throw new Exception("Could not create service");
				}
				service = db.getServiceByName(service.getServicename());
				if(service == null){
					throw new Exception("service doesnot exist");
				}
				Logger.debug("getservice exit in app controller");


				ObjectNode jsonservice = Json.newObject();
				jsonservice.put("serviceid", service.getServiceid());
				jsonservice.put("servicename", service.getServicename());
				jsonservice.put("uid", service.getUid());
				jsonservice.put("servicetype", service.getServicetype());
				jsonservice.put("status", service.getStatus());
				jsonservice.put("datecreated", service.getDatecreated());

				WebsiteHandler job = new WebsiteHandler(service);
				int jobstatus = job.save(jsonservice.toString());
				if(jobstatus ==0){
					throw new Exception("Could not create job");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return redirect("/dashboard");
		}

		Logger.debug("------Exit from createWebsite() method in Application Controller------");
		return redirect("/dashboard");
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
	public static Result logout(){
		session().clear();
		return redirect("/login");
	}
	public static Result startVM(){
		session().clear();
		return redirect("/");
	}
	public static Result stopVM(){
		session().clear();
		return redirect("/");
	}
	public static Result createDatabase() {
		Logger.debug("------Entered into createDatabase() method in Application controller------");
		String name = null;
		DatabaseHandler job = null;
		int status = 0;
		DataAccess db = new DataAccess();
		Services service = new Services();
		Date date = new Date();
		JsonNode json = request().body().asJson();
		Logger.debug("json request is:"+ json);


		//retrieving user session
		String user = session("username");
		String uid = session("uid");
		Logger.debug("User ID: "+ user);
		try{
			if(json != null) {
				name = json.findPath("DatabaseName").textValue();
				System.out.println(name);
				service.setServicename(name);
			}
			service.setServicetype("DatabaseService");
			status = db.saveToServices(uid, service.getServicename(), service.getServicetype(),date.toString(), json.toString());
			if(status==0)
			{
				throw new Exception("Could not create service");
			}
			service = db.getServiceByName(service.getServicename());
			if(service==null)
			{
				throw new Exception("Service does not exist");
			}
			String jobjson = Json.toJson(service).toString();
			job = new DatabaseHandler(service);
			int jobstatus = job.save(jobjson);
			if(jobstatus == 0)
			{
				throw new Exception("Could not create service");
			}
		}
		catch(SQLException sqle){
			
		}
		catch (Exception e) {
			Logger.error("ERROR while fetching or saving service details from database: "+ e.getMessage());
			return ok(createWebsite.render(e.getMessage(), user , uid));
		}
		Logger.debug("------Exit from createDatabase() method in Application Controller------");
		return redirect("routes.Application.dashboard()");

	}


	public static Result fetchcreateDatabasepage(){
		Logger.debug("------Entered into fetchcreateDatabasepage() method in Application controller------");
		Logger.debug("------Exit from fetchcreateDatabasepage() method in Application controller------");

		return ok(createDatabase.render("Hello World"));

	}  
}
