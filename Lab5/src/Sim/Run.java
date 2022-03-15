package Sim;

import Sim.CustomEvents.ChangeNetworkEvent;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
		Link a = new Link();
		Link b = new Link();
		Link c = new Link();
		Link d = new Link();
		
		Link r = new Link();
		
		Node A = new Node(1,1);
		Node B = new Node(1,2);
		Node C = new Node(1,3);
		
		Node D = new Node(2,1);

		//Connect links to hosts
		A.setPeer(a);
		B.setPeer(b);
		C.setPeer(c);
		D.setPeer(d);
		
		Router R1 = new Router(4, 1);
		Router R2 = new Router(4, 2);
		
		//Routers to each other
		R1.connectInterface(0, r, R2);
		R2.connectInterface(0, r, R1);
		
		//Network 1
		R1.connectInterface(1, a, A);
		R1.connectInterface(2, b, B);
		R1.connectInterface(3, c, C);
		
		//Network 2
		R2.connectInterface(1, d, D);
		
		D.StartSending(B.getAddr().networkId(), B.getAddr().nodeId(), 5, 5, 1);
		
		//B.StartSending(D.getAddr().networkId(), D.getAddr().nodeId(), 5, 5, 1); 
		
		B.send(R2, new ChangeNetworkEvent(R1), 5);
		
		//D.StartSending(1, 1, 2, 2, 1); 
		
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
