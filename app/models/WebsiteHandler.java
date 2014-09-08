package models;

import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;


public class WebsiteHandler {
	
	Service service = new Service();
	
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
		
		OSClient os = OSFactory.builder()
                .endpoint("http://192.168.0.19:5000/v2.0")
                .credentials("admin","test123")
                .tenantName("admin")
                .authenticate();
		Tenant tenant = os.identity().tenants().getByName("admin");
		
		Tenant tenant = os.identity().tenants().getByName("admin");
		System.out.println("tenant id: " + tenant.getId());
		
		
		Flavor flavor = os.compute().flavors().get("1");
		System.out.println("flavor id is:" + flavor.getName());
		
		
		List<? extends Server> servers = os.compute().servers().list();
		
		Network network = os.networking().network().get("e219602b-dad5-4c85-b571-4059c186a2f8"); 
		Network network1 = os.networking().network().get("319a8b77-086c-4b20-84f3-400861472f89");
		
		
		ArrayList<String> networks1 = new ArrayList<String>();
		networks1.add(network.getId());
		networks1.add(network1.getId());
		
		ServerCreate sc = Builders.server()
                .name("kuhf1") //uid+website == 1:ghdk
                .flavor("1")
                .image("3fe4b8c0-d90e-47c2-be10-81f14b83e71b")
                .networks(networks1)
                .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
                .build();
	
		Server server = os.compute().servers().boot(sc);
		
		os.compute().servers().action(server.getId(), Action.START);
		
		
	
		
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
