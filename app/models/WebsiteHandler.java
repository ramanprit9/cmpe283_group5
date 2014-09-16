package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.network.Network;
import org.openstack4j.openstack.OSFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.libs.Json;


public class WebsiteHandler {

	Services service = new Services();
	OSClient os = OSFactory.builder()
			.endpoint("http://localhost:5000/v2.0")
			.credentials("admin","test123")
			.tenantName("admin")
			.authenticate();

	public WebsiteHandler(){
		
	}
	public WebsiteHandler(Services service){
		this.service = service;

	}

	//save to jobs table
	public int save(String json){

		Logger.debug("------Entered into save() method in Website Handler -------");
		int status=0;
		DataAccess db = new DataAccess();
		try {
			status = db.saveToJob(json);
		} catch (Exception e) {
			Logger.error("ERROR while saving job : "+ e.getMessage());
		}
		Logger.debug("------Exit from save() method in Website Handler -------");
		return status;
	}
	public void processtest(int id){
		Logger.debug("inside process test: " + id);
	}

	//create openstack instance
	public void process(int id, String servicename) {

		
		Logger.debug("------Entered into process() method in Website Handler -------");
		String imageId = null;
		String serverName = null;
		DataAccess db = new DataAccess();
		ObjectNode result = Json.newObject();
		
		try {
			Logger.debug("------Inside try - process method-------");
			Image webImage = os.compute().images().get("3fe4b8c0-d90e-47c2-be10-81f14b83e71b");
			Image dbImage = os.compute().images().get("3fe4b8c0-d90e-47c2-be10-81f14b83e71b");

			Network fixed_network = os.networking().network().get("e219602b-dad5-4c85-b571-4059c186a2f8"); 
			Network public_network = os.networking().network().get("319a8b77-086c-4b20-84f3-400861472f89");


			ArrayList<String> networks = new ArrayList<String>();
			networks.add(fixed_network.getId());
			networks.add(public_network.getId());
			Logger.debug("------Added networks - process method -------");
			result.put("id", "");
			result.put("serviceid", service.getServiceid());
			result.put("servicename", service.getServicename());
			result.put("uid", service.getUid());
			result.put("datecreated",service.getDatecreated());
			result.put("resourcetype",Constants.RESOURCE_TYPE);
			result.put("fixednetwork", Constants.FIXED_NETWORK);
			result.put("floatingnetwork", Constants.FLOATING_NETWORK);
			result.put("securitygroup", Constants.SECURITY_GROUP);
			result.put("keypair", Constants.KEYPAIR);
			result.put("port", "eth1");
			Logger.debug("------1 -------");
			service = db.getServiceByName(servicename);
			if(service != null){
				
			if(service.getServicetype().equalsIgnoreCase("smallWebsite")){
				
				Logger.debug("------Entered into if call in small Website scope -------");
				imageId = webImage.getId();
				Resources resource = new Resources();
				serverName = service.getUid()+":smallWebsite:"+service.getServiceid();
				//FloatingIP ip = os.compute().floatingIps().allocateIP("public");

				String resource_status = createVM(imageId, serverName, networks);

				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);

				result.put("fixedip", "");
				result.put("floatingip", "");
				resource.setJson(result.asText());
				if(resource_status.equals("created")){
				Logger.debug("------Add to resources table -------");
				db.addResource("instance" , resource, service);
				}

			}else{
				Logger.debug("------Entered into else call in small Website scope -------");
				result.remove("fixedip");
				result.remove("floatingip");
				imageId = webImage.getId();
				Resources resource = new Resources();
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				String resource_status = createVM(imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", "");
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

				result.remove("fixedip");
				result.remove("floatingip");
				resource = new Resources();
				imageId = webImage.getId();
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				resource_status = createVM(imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", "");
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

				result.remove("fixedip");
				result.remove("floatingip");
				resource = new Resources();
				imageId = dbImage.getId();
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				resource_status = createVM(imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", "");
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

			}
			Logger.debug("------Before delete -------");
			delete(id);
			}
		} catch (SQLException se) {
			Logger.error("ERROR while adding resources: "+ se.getMessage());
		}
		Logger.debug("------Exit from process() method in Website Handler -------");
	}

	public String createVM(String imageId, String serverName, ArrayList<String> networks){

		Logger.debug("------Entered into createVM() method in Website Handler -------");
		String server_status =  "created";
		Logger.debug("------create vm 1 -------");
		Flavor flavor = os.compute().flavors().get("1");

		Logger.debug("------create vm 2 -------");
		ServerCreate sc = Builders.server()
				.name(serverName)
				.flavor(flavor.getId())
				.image(imageId)
				.networks(networks)
				.addSecurityGroup("sample")
				.addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
				.build();
		Logger.debug("------create 2.3 -------" + sc.getName());
		Server server = os.compute().servers().boot(sc);
		
		Logger.debug("------create vm 3 -------");
		

		/*List<? extends Server> servers = os.compute().servers().list(false);
		 * for(Server list_server : servers){
			if(list_server.getName().equals(serverName)){
				server_status = "created";
			}
		}*/

		Logger.debug("------Exit from createVM() method in Website Handler -------");
		return server_status;
	}

	//delete from jobs table
	public int delete(int jobid) {

		Logger.debug("------Entered from delete() method in Website Handler -------");
		int status=0;
		DataAccess db = new DataAccess();
		try {
			status = db.deleteJob(jobid);
		} catch (Exception e) {
			Logger.error("ERROR while deleting jobs: "+ e.getMessage());
		}
		Logger.debug("------Exit from delete() method in Website Handler -------");
		return status;
	}

}
