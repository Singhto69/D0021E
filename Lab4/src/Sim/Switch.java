package Sim;

import Sim.CustomEvents.ChangeNetworkEvent;

// This class implements a simple switch

public class Switch extends SimEnt{

	private SwitchTableEntry [] _switchTable;
	private int _ports;
	
	// When creating the switch, the number of ports must be specified
	
	Switch(int ports)
	{
		_switchTable = new SwitchTableEntry[ports];
		_ports=ports;
	}
	
	// This method connects links to the switch and also informs the 
    // switch of the host connects to the other end of the link
	
	public void connectPort(int portNumber, SimEnt link, SimEnt node)
	{
		if (portNumber<_ports)
		{
			_switchTable[portNumber] = new SwitchTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in switch");
		
		((Link) link).setConnector(this);
	}
	
	public void connectNextPort(SimEnt link, SimEnt node)
	{
		for(int i = 1; i<_switchTable.length; i++)
		{
			if(_switchTable[i] == null)
			{
				_switchTable[i] = new SwitchTableEntry(link, node);
				((Link) link).setConnector(this);
				return;
			}
		}
		
		System.out.println("\nSwitch is full! Can not connect more ports\n");
	}

	// This method searches for an entry in the switch-table that matches
	// the host number in the destination field of a frame. The link
	// that connects host the switch port is returned 
	
	private int getPort(int nodeAddress)
	{
		int port = 0;
		for(int i=0; i<_ports; i++)
			if (_switchTable[i] != null)
			{
				if (((Node) _switchTable[i].node()).getAddr().nodeId() == nodeAddress)
				{
					port = i;
				}
			}
		return port;
	}
	
	
	// Called when a frame is received by the switch
	
	public void recv(SimEnt source, Event event)
	{
		if(event instanceof ChangeNetworkEvent) {
//			Node sourceNode = ((ChangeNetworkEvent) event).source();
//			int port = getPort(sourceNode.getAddr().nodeId());
//			if(port != 0) {
//				//TODO Ability to go back to original network
//				_switchTable[port] = null;
//
//				SimEnt sendNext = _switchTable[0].link();
//				send (sendNext, event, 0);
//			}
		}
		
		if (event instanceof Message)
		{
			System.out.println("Switch handles frame with seq: " + ((Message) event).seq() + " from node: "+ ((Message) event).source().nodeId());
			SimEnt link = _switchTable[getPort(((Message) event).destination().nodeId())].link();
			SimEnt sendNext = link == null ? _switchTable[0].link() : link;
			System.out.println("Switch forwards to host: " + ((Message) event).destination().nodeId());		
			send (sendNext, event, 0);
		}
	}
}
