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
		
		String ImageId = null;
		OSClient os = OSFactory.builder()
                .endpoint("http://192.168.0.19:5000/v2.0")
                .credentials("admin","test123")
                .tenantName("admin")
                .authenticate();
		Tenant tenant = os.identity().tenants().getByName("admin");
		
		
		if(service.getServicetype().contains("Website")){
			
			ImageId = "websiteVM";
		}
		if(service.getServicetype().equals("bigWebsite")){
			
			
		}
		
		Image img = os.compute().images().get("websiteVM");
		Flavor flavor = os.compute().flavors().get("1");
		ServerCreate sc = Builders.server()
                .name(service.getUid()+service.getServicename()) //uid+website == 1:ghdk
                .flavor("1")
                .image("imageId")
                .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
                .build();
		
		sc.getAvailabilityZone();
		List<? extends Subnet> subnets = os.networking().subnet().list();
		
		List<? extends Network> networks = os.networking().network().list();
		sc.addNetwork("net1","10.10.10.5");
		
		Port port = os.networking().port().create(Builders.port()
	              .name("port-1")
	              .networkId("networkId")
	              .fixedIp("192.168.0.101", "subnetId")
	              .build());
		/*Router router = os.networking().router().create(Builders.router()
                .name("ext_net")
                .adminStateUp(true)
                .externalGateway("networkId")
                .route("192.168.0.0/24", "10.20.20.1")
                .build());*/
		FloatingIP ip = os.compute().floatingIps().allocateIP("pool");
		sc.addNetwork("public", ip.getFloatingIpAddress());
		
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
