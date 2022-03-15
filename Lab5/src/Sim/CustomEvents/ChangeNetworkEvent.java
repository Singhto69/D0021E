package Sim.CustomEvents;

import Sim.Router;
import Sim.SimEnt;
import Sim.Event;
import Sim.Node;

public class ChangeNetworkEvent implements Event {
	private Router _homeAgent;
	private Router _foreignAgent;
	private Node _source;
	
	public ChangeNetworkEvent(Router homeAgent) {
		_homeAgent = homeAgent;
		//_foreignAgent = foreignAgent;
		//_source = source;
	}
	
	public Router homeAgent () {
		return _homeAgent;
	}
	
	public Router foreignAgent () {
		return _foreignAgent;
	}
	
	public Node source () {
		return _source;
	}

	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}
}
