package Sim.CustomEvents;

import Sim.Event;
import Sim.Node;
import Sim.SimEnt;

public class ChangeInterfaceEvent implements Event {
	private int _newInterface;
	private Node _ent;
	
	public void entering(SimEnt locale) {}
	
	public ChangeInterfaceEvent(Node ent, int newInterface) {
		_ent = ent;
		_newInterface = newInterface;
	}
	
	public int newInterface() {
		return _newInterface;
	}
	
	public Node ent() {
		return _ent;
	}
}
