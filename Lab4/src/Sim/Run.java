package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
		Link a = new Link();
		Link b = new Link();
		Link c = new Link();
		Link d = new Link();
		
		Link swLink1 = new Link();
		Link swLink2 = new Link();
		
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
		R1.connectInterface(3, r, R2);
		R2.connectInterface(3, r, R1);
		
		//Network 1
		Switch sw1 = new Switch(4);
		sw1.connectPort(0, swLink1, R1);
		sw1.connectPort(1, a, A);
		sw1.connectPort(2, b, B);
		sw1.connectPort(3, c, C);
		
		//Network 2
		Switch sw2 = new Switch(4);
		sw2.connectPort(0, swLink2, R2);
		sw2.connectPort(1, d, D);
		
		R1.connectInterface(0, swLink1, sw1);
		R2.connectInterface(0, swLink2, sw2);
		
		D.StartSending(B.getAddr().networkId(), B.getAddr().nodeId(), 5, 5, 1); 
		
		//B.send(R2, new ChangeNetworkEvent(R2, D), 12);
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
