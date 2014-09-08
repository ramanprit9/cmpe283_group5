package models;

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
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;


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
		
		int status=0;
		DataAccess db = new DataAccess();
		try {
			db.saveWebsiteJob(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	//create openstack instance
	public void process(){
		
		String imageId = null;
		String serverName = null;
		Flavor flavor = os.compute().flavors().get("1");
		Image webImage = os.compute().images().get("webimageId");
		Image dbImage = os.compute().images().get("dbimageId");
		
		List<? extends Server> servers = os.compute().servers().list();
		
		Network network = os.networking().network().get("e219602b-dad5-4c85-b571-4059c186a2f8"); 
		Network network1 = os.networking().network().get("319a8b77-086c-4b20-84f3-400861472f89");
		
		
		ArrayList<String> networks1 = new ArrayList<String>();
		networks1.add(network.getId());
		networks1.add(network1.getId());
		
		if(service.getServicetype().equals("smallwebsite")){
			imageId = webImage.getId();
			serverName = service.getUid()+":smallWebsite:"+service.getServiceid();
			createVM(imageId, serverName, networks1);
			
		}else{
			imageId = webImage.getId();
			serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
			createVM(imageId, serverName, networks1);
			
			imageId = webImage.getId();
			serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
			createVM(imageId, serverName, networks1);
			
			imageId = dbImage.getId();
			serverName = service.getUid()+":bigWebsite:"+service.getServiceid();
			createVM(imageId, serverName, networks1);
			
		}
	}
	
	public String createVM(String imageId, String serverName, ArrayList<String> networks){
		ServerCreate sc = Builders.server()
                .name(serverName)
                .flavor("1")
                .image(imageId)
                .networks(networks)
                .addSecurityGroup("sample")
                .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
                .build();
		Server server = os.compute().servers().boot(sc);
		os.compute().servers().action(server.getId(), Action.START);
		return "";
	}
	
	//delete from jobs table
	public int delete(String jobid) {
		int status=0;
		DataAccess db = new DataAccess();
		try {
			status = db.deleteWebsiteJob(jobid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

}
