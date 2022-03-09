package Sim;

import Sim.CustomEvents.ChangeInterfaceEvent;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;

	// When created, number of interfaces are defined
	
	Router(int interfaces)
	{
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
		{
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}
	
	public void printInterfaces(){
		System.out.println("");
		for ( int i = 0 ; i < _interfaces; i++){
			if(_routingTable[i] == null) {
				System.out.println("INTERFACE " + i + ": null");
			}else {
				int nodeId = ((Node)_routingTable[i].node()).getAddr().nodeId();
				int networkId = ((Node)_routingTable[i].node()).getAddr().networkId();
				System.out.println("INTERFACE " + i + ": " + networkId + "." + nodeId);
			}
		}
		System.out.println("");
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress)
	{
		SimEnt routerInterface=null;
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
		if (event instanceof Message)
		{
			System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(((Message) event).destination().networkId());
			System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());		
			send (sendNext, event, _now);
		}
		
		//If node wants to change interface
		if (event instanceof ChangeInterfaceEvent){
			printInterfaces();
			
			Node node = ((ChangeInterfaceEvent) event).ent();
			int newInterface = ((ChangeInterfaceEvent) event).newInterface();
			
			//Will crash if not found
			_routingTable[getInterfaceNumber(node.getAddr().networkId())] = null;
			connectInterface(newInterface, node._peer, node);
			
			System.out.println("Changing the router table");
			
			printInterfaces();
		}
	}
	
	private int getInterfaceNumber(int networkAddress)
	{
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					 return i;
				}
			}
		//Error handling
		return -1;
	}
}
