package Sim.CustomEvents;

import Sim.Router;
import Sim.SimEnt;
import Sim.Event;
import Sim.Node;

public class ChangeNetworkEvent implements Event {
	private Router _homeAgent;
	private Node _source;
	
	public ChangeNetworkEvent(Router homeAgent, Node source) {
		_homeAgent = homeAgent;
		_source = source;
	}
	
	public Router homeAgent () {
		return _homeAgent;
	}
	
	public Node source () {
		return _source;
	}

	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}
}
