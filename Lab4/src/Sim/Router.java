package Sim;

import java.util.HashMap;

import Sim.CustomEvents.ChangeInterfaceEvent;
import Sim.CustomEvents.ChangeNetworkEvent;

// This class implements a simple router

public class Router extends Node{

	//private Net
	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;
	private int _networkId = 0;
	
	private HashMap<NetworkAddr, NetworkAddr> bindings = new HashMap<NetworkAddr, NetworkAddr>();

	// When created, number of interfaces are defined
	
	Router(int interfaces, int networkId)
	{
		super(networkId, 0);
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
		_networkId = networkId;
	}
	
	public int networkId() {
		return _networkId;
	}
	
	//Connects to the first available interface
	public int connectNextInterface(SimEnt link, SimEnt node) {
		for(int i = 0; i < _routingTable.length; i++) {
			if(_routingTable[i] == null) {
				_routingTable[i] = new RouteTableEntry(link, node);
				return i;
			}
		}
		
		return -1;
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
	
	private void printInterfaces(){
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
	
	private SimEnt getInterface(NetworkAddr addr)
	{
		//Default gateway
		SimEnt routerInterface=_routingTable[0].link();
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				SimEnt ent = _routingTable[i].node();
				if (ent instanceof Node && ((Node) ent).getAddr().networkId() == addr.networkId())
				{
					if(((Node) ent).getAddr().nodeId() == addr.nodeId()) {
						return routerInterface = _routingTable[i].link();
					}
				}
			}
		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
		System.out.println("event instanceof Message: "+(event instanceof Message));
		if (event instanceof Message)
		{
			Message e = (Message) event;
			
			if(e.ttl > 5) return;
			e.ttl++;
			
			NetworkAddr destIp = e.destination();
			
			System.out.println("Router ("+ networkId() +") handles packet with seq: " + e.seq()+" from node: "+e.source().networkId()+"." + e.source().nodeId());
			if(bindings.containsKey(destIp)) 
			{
				NetworkAddr newIp = bindings.get(destIp);
				System.out.println("\nRedirects from " + destIp.networkId()+"."+destIp.nodeId()+" to "+newIp.networkId()+"."+newIp.nodeId()+"\n");
				destIp = bindings.get(destIp);
			}
			SimEnt sendNext = getInterface(destIp);
			System.out.println("Router ("+ networkId() +") sends to node: " + sendNext);		
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
		
		//If node wants to move network.
		if(event instanceof ChangeNetworkEvent) {
			Node mobileNode = (Node) source;
			Router homeAgent = ((ChangeNetworkEvent) event).homeAgent();
			
			Link newlink = new Link();
			NetworkAddr homeAddress = mobileNode.getAddr();
			int newNodeId = connectNextInterface(newlink, mobileNode);
			
			if (newNodeId == -1) {
				System.out.println("Connecting the mobile node to a new interface failed");
				return;
			};
			
			//Node need to be linked to the router, added to routing table, connected to Home Agent.
			mobileNode.setPeer(newlink);
			mobileNode._id = new NetworkAddr(_networkId, newNodeId);
			
			NetworkAddr CoA = mobileNode.getAddr();
			
			homeAgent.bindings.put(homeAddress, CoA);
			
			System.out.println("\nConnecting the mobile node to new network\n");
		}
	}
	
	public void moveNodeConn(Node node, SimEnt newConn) {
		_routingTable[getInterfaceNumber(node.getAddr().networkId())] = null;
		connectNextInterface(node._peer, node);
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
