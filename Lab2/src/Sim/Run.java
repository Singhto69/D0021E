package Sim;

import Sim.CustomClasses.Lossylink;
import Sim.CustomClasses.NodeGaussian;
import Sim.CustomClasses.NodePoisson;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Lossylink link1 = new Lossylink(10, 1, 0d);
		Lossylink link2 = new Lossylink(5, 1, 0d);
		
		// Create two end hosts that will be
		// communicating via the router
		//CBR
		//Node host1 = new Node(1,1);
		//Node host2 = new Node(2,1);
		
		//Gaussian
		//NodeGaussian host1 = new NodeGaussian(1,1, 5, 2);
		//NodeGaussian host2 = new NodeGaussian(2,1, 5, 2);
		
		//Poisson
		NodePoisson host1 = new NodePoisson(1,1, 5);
		NodePoisson host2 = new NodePoisson(2,1, 5);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(2);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
		
		// Generate some traffic
		// host1 will send 3 messages with time interval 5 to network 2, node 1. Sequence starts with number 1
		host1.StartSending(2, 2, 3, 5, 1); 
		// host2 will send 2 messages with time interval 10 to network 1, node 1. Sequence starts with number 10
		host2.StartSending(1, 1, 2, 10, 10); 
		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}