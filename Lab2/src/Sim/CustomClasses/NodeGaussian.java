package Sim.CustomClasses;

import java.util.Random;

import Sim.Event;
import Sim.Message;
import Sim.NetworkAddr;
import Sim.Node;
import Sim.SimEngine;
import Sim.SimEnt;
import Sim.TimerEvent;

public class NodeGaussian extends Node {
	private double mu; //Mean
	private double sigma; // Standard deviation
	
	private Random rand;
	
	public NodeGaussian(int network, int node, double mu, double sigma) {
		super(network, node);
		
		this.rand = new Random();
		this.mu = mu;
		this.sigma = sigma;
	}
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof TimerEvent)
		{			
			if (_stopSendingAfter > _sentmsg)
			{
				_sentmsg++;
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				
				//Normal Distribution with (5) mean and (2) in st.d.
				send(this, new TimerEvent(),_timeBetweenSending + rand.nextGaussian(mu,sigma));
				
				System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
				_seq++;
			}
		}
		if (ev instanceof Message)
		{
			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
			
		}
	}
}
