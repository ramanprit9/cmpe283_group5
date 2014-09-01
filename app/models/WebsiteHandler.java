package models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

import play.db.DB;

public class WebsiteHandler {
	
	String servicetype = null;
	
	public WebsiteHandler(String servicetype){
		this.servicetype = servicetype;
		
		if(servicetype.equals("smallWebsite"))
		{
			//assign default values based on servicetype
		}else{
			//assign default values based on servicetype
		}
		
	}
	
	//save to jobs table
	public int save(String json) throws SQLException{
		
		Statement stmt = null;
		int status=0;
		Connection conn = DB.getConnection();
		try {
			stmt = conn.createStatement();
			String deleteSQL = "INSERT INTO JOBS  VALUES('WebsiteHandler.class',"+json;
			status = stmt.executeUpdate(deleteSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return status;
	}
	
	//create openstack instance
	public String process(){
		OSClient os = OSFactory.builder()
                .endpoint("http://192.168.0.19:5000/v2.0")
                .credentials("admin","test123")
                .tenantName("admin")
                .authenticate();
		/*network + subnet -- combo
		flavor edit
		image
		add port
		instance flavor,network,image,floating ip
		router - add subnet interface(net1),set gateway public network, assign floating ip
		create public network + subnet ---combo

		first create instance --
		create network and subnet --assign to instance
		getflavor                 -- assign to instance
		getimage 		  -- assign to instance


		create router
		create public network and subnet -- assign to router
		create floating ip               -- assign to instance*/
		Tenant tenant = os.identity().tenants().getByName("admin");
		Image img = os.compute().images().get("imageId");
		Flavor flavor = os.compute().flavors().get("flavorId");
		ServerCreate sc = Builders.server()
                .name("Ubuntu 2")
                .flavor("flavorId")
                .image("imageId")
                .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
                .build();
		Network network = os.networking().network()
                .create(Builders.network().name("ext_network").tenantId(tenant.getId()).build());
		Subnet subnet = os.networking().subnet().create(Builders.subnet()
                .name("MySubnet")
                .networkId("networkId")
                .tenantId("tenantId")
                .addPool("192.168.0.1", "192.168.0.254")
                .ipVersion(IPVersionType.V4)
                .cidr("192.168.0.0/24")
                .build());
		Port port = os.networking().port().create(Builders.port()
	              .name("port-1")
	              .networkId("networkId")
	              .fixedIp("192.168.0.101", "subnetId")
	              .build());
		Router router = os.networking().router().create(Builders.router()
                .name("ext_net")
                .adminStateUp(true)
                .externalGateway("networkId")
                .route("192.168.0.0/24", "10.20.20.1")
                .build());
		FloatingIP ip = os.compute().floatingIps().allocateIP("pool");
		return "";
	}
	
	//delete from jobs table
	public int delelte(String jobid) throws SQLException {
		Statement stmt = null;
		int status=0;
		Connection conn = DB.getConnection();
		try {
			stmt = conn.createStatement();
			String deleteSQL = "DELETE FROM JOBS WHERE jobid ="+jobid;
			status = stmt.executeUpdate(deleteSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
		return status;
	}

}
