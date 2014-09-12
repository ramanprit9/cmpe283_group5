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

	Service service = new Service();
	OSClient os = OSFactory.builder()
			.endpoint("http://192.168.0.19:5000/v2.0")
			.credentials("admin","test123")
			.tenantName("admin")
			.authenticate();

	public WebsiteHandler(Service service){
		this.service = service;

	}

	//save to jobs table
	public int save(String json){

		Logger.debug("------Entered into save() method in Website Handler -------");
		int status=0;
		DataAccess db = new DataAccess();
		try {
			db.saveToJob(json);
		} catch (Exception e) {
			Logger.error("ERROR while saving job : "+ e.getMessage());
		}
		Logger.debug("------Exit from save() method in Website Handler -------");
		return status;
	}

	//create openstack instance
	public void process() {

		Logger.debug("------Entered into process() method in Website Handler -------");
		String imageId = null;
		String serverName = null;
		DataAccess db = new DataAccess();
		ObjectNode result = Json.newObject();

		try {
			Image webImage = os.compute().images().get("webimageId");
			Image dbImage = os.compute().images().get("dbimageId");

			Network fixed_network = os.networking().network().get("e219602b-dad5-4c85-b571-4059c186a2f8"); 
			//Network public_network = os.networking().network().get("319a8b77-086c-4b20-84f3-400861472f89");


			ArrayList<String> networks = new ArrayList<String>();
			networks.add(fixed_network.getId());

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
			if(service.getServicetype().equals("smallwebsite")){

				imageId = webImage.getId();
				Resource resource = new Resource();
				serverName = service.getUid()+":smallWebsite:"+service.getServiceid();
				FloatingIP ip = os.compute().floatingIps().allocateIP("public");

				String resource_status = createVM(ip.getFloatingIpAddress().toString(),imageId, serverName, networks);

				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);

				result.put("fixedip", "");
				result.put("floatingip", ip.getFloatingIpAddress());
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

			}else{
				result.remove("fixedip");
				result.remove("floatingip");
				imageId = webImage.getId();
				Resource resource = new Resource();
				FloatingIP floating_ip1 = os.compute().floatingIps().allocateIP("public");
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				String resource_status = createVM(floating_ip1.getFloatingIpAddress(),imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", floating_ip1.getFloatingIpAddress());
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

				result.remove("fixedip");
				result.remove("floatingip");
				resource = new Resource();
				imageId = webImage.getId();
				FloatingIP floating_ip2 = os.compute().floatingIps().allocateIP("public");
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				resource_status = createVM(floating_ip2.getFloatingIpAddress(),imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", floating_ip1.getFloatingIpAddress());
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

				result.remove("fixedip");
				result.remove("floatingip");
				resource = new Resource();
				imageId = dbImage.getId();
				FloatingIP floating_ip3 = os.compute().floatingIps().allocateIP("public");
				serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
				resource_status = createVM(floating_ip3.getFloatingIpAddress(),imageId, serverName, networks);
				resource.setDatecreated(service.getDatecreated());
				resource.setServiceid(service.getServiceid());
				resource.setStatus(resource_status);
				result.put("fixedip", "");
				result.put("floatingip", floating_ip1.getFloatingIpAddress());
				resource.setJson(result.asText());
				db.addResource("instance" , resource, service);

			}
		} catch (SQLException se) {
			Logger.error("ERROR while adding resources: "+ se.getMessage());
		}
		Logger.debug("------Exit from process() method in Website Handler -------");
	}

	public String createVM(String floatingIp , String imageId, String serverName, ArrayList<String> networks){

		Logger.debug("------Entered into createVM() method in Website Handler -------");
		String server_status = null;

		Flavor flavor = os.compute().flavors().get("1");

		ServerCreate sc = Builders.server()
				.name(serverName)
				.flavor(flavor.getId())
				.image(imageId)
				.networks(networks)
				.addSecurityGroup("sample")
				.addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
				.build();
		sc.addNetwork("public", floatingIp);
		Server server = os.compute().servers().boot(sc);

		List<? extends Server> servers = os.compute().servers().list(false);

		for(Server list_server : servers){
			if(list_server.getName().equals(serverName)){
				server_status = "created";
			}
		}

		os.compute().servers().action(server.getId(), Action.START);

		Logger.debug("------Exit from createVM() method in Website Handler -------");
		return server_status;
	}

	//delete from jobs table
	public int delete(String jobid) {

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
