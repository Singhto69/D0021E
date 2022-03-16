package Sim;

import java.util.ArrayList;
import java.util.HashMap;

import Sim.CustomEvents.ChangeInterfaceEvent;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
    protected NetworkAddr _id;
    protected SimEnt _peer;
    protected int _ackedMsg = 0;
    protected int _seq = 0;


    public Node(int network, int node) {
        super();
        _id = new NetworkAddr(network, node);
    }


    // Sets the peer to communicate with. This node is single homed

    public void setPeer(SimEnt peer) {
        _peer = peer;

        if (_peer instanceof Link) {
            ((Link) _peer).setConnector(this);
        }
    }

    public boolean changeInterface(int newInterface) {
        send(_peer, new ChangeInterfaceEvent(this, newInterface), 0);

        return true;
    }

    public NetworkAddr getAddr() {
        return _id;
    }

    public String toString() {
        return "" + _id;
    }

//**********************************************************************************	
    // Just implemented to generate some traffic for demo.
    // In one of the labs you will create some traffic generators

    protected int _stopSendingAfter = 0; //messages
    protected int _timeBetweenSending = 10; //time between messages
    protected int _toNetwork = 0;
    protected int _toHost = 0;
    protected int _ackWindow = 0;
    protected int _resendTime = 0;
    
    HashMap<Integer, ExpectedAck> expectedAck = new HashMap<Integer, ExpectedAck>();

    public void StartSending(int network, int node, int number, int timeInterval, int startSeq, int ackWindow, int resendTime) {
        _stopSendingAfter = number;
        _timeBetweenSending = timeInterval;
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        _ackWindow = ackWindow;
        _resendTime = resendTime;
        send(this, new TimerEvent(), 0);
    }

    public void TCP(NetworkAddr dst) {
    	//System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " starts 3-way-handshake");
    	_seq = (int) (Math.random()*1000);
        Message msg = new Message(this.getAddr(), dst, _seq, true, false, 0);
        _seq++;
        expectedAck.put(_seq, new ExpectedAck(msg));
        
        System.out.println("3 way handshake Init");
        System.out.println("3WH: Sending SYN");
        TCP_status(msg);
        send(_peer, msg, 0);
    }
    
    public void TCP_status(Message msg) {
    	System.out.println("Node " + _id.networkId() + "." + _id.nodeId()+ " sends SYN or ACK.. Syn: "+msg.synflag() + " - seq: " + msg.seq()+ 
    			" - Ack: "+ msg.ackflag() + " - acknum: " + msg.ack()
    			+ " Expected Ack: " + expectedAck+"\n");
    }
    
    class ExpectedAck {
    	Message msg;
    	double time;
    	
    	ExpectedAck(Message msg){
    		this.msg = msg;
    		this.time = SimEngine.getTime();
    	}
    }
    
    boolean first = true;


//**********************************************************************************	

    // This method is called upon that an event destined for this node triggers.

    public void recv(SimEnt src, Event ev) {
        if (ev instanceof TimerEvent) {
        	if(first) 
        	{
        		first = false;
        		TCP(new NetworkAddr(_toNetwork, _toHost));
        	}
        	else
        	{
	            if(expectedAck.size() < _ackWindow) {
	            	Message newMsg = new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq);
	            	
	            	System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " sends message with seq: " + _seq + " at time " + SimEngine.getTime()+"\n");
	            	
	            	_seq = _seq + 69;
	            	expectedAck.put(_seq, new ExpectedAck(newMsg));
	            	send(_peer, newMsg, 3);
	            }
        	}
        	
        	for(Integer key : expectedAck.keySet()) {
        		ExpectedAck ack = expectedAck.get(key);
        		double currentTime = SimEngine.getTime();
        		
        		//If enough time has passed resend message
        		if(currentTime - ack.time > _resendTime) {
        			System.out.println("Resending message (seq: "+ack.msg.seq()+") to node: "+ack.msg.destination());
        			ack.time = currentTime;
        			ack.msg.ttl = 0;
        			send(_peer, ack.msg, 3);
        		}
            }
        	
        	//Send timer events until we have sent all the messages and Acks expected
            if (_ackedMsg < _stopSendingAfter) {
            	send(this, new TimerEvent(), _timeBetweenSending);
            }
        }

        if (ev instanceof Message) {
            System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " receives message with seq: " + ((Message) ev).seq() + " at time " + SimEngine.getTime());
            
            int timeDelay = 3;
            Message msg = (Message) ev;
            boolean ackflag = msg.ackflag();
            boolean synflag = msg.synflag();
            
            int recvSeq = msg.seq();
            int recvAck = msg.ack();
            NetworkAddr dst = msg.source();
            Message newMsg = null;
            
            //3 Way Handshake
            if (synflag && !ackflag) { //Receive Syn, Send Syn/Ack
                _seq = (int) (Math.random()*1000);
                newMsg = new Message(_id, dst, _seq, true, true, recvSeq+1);
                
                _seq++;
                expectedAck.put(_seq, new ExpectedAck(newMsg));
                System.out.println("3WH: Sending SYN/ACK");
            }else if (ackflag && synflag){ //Receive Syn/Ack, Send Ack
            	newMsg = new Message(_id, dst, _seq, false, true, recvSeq+1);
            	System.out.println("3WH: Sending ACK");
            }
            
            //Receive regular message (send Ack back)
            if (!(ackflag || synflag)){ 
            	newMsg = new Message(_id, dst, _seq, false, true, recvSeq+69);
            }
            
            //Remove buffered message when receive Ack
            if(ackflag && expectedAck.remove(recvAck) != null){
            	++_ackedMsg;
            }
            
            if(newMsg == null) return;
            send(_peer, newMsg, timeDelay);
            TCP_status(newMsg);
        }

        //System.out.println("Node: " + _id.nodeId() + ". Packets sent: " + sentPackets + ". Packets recv: " + recvPackets);
    }

    //**********************************************************************************
    protected int sentPackets = 0;
    protected int recvPackets = 0;

    // Statistics regarding sent packets
    protected void statisticsSend() {
        ++sentPackets;
    }

    // Statistics regarding received packets
    protected void statisticsRecv() {
        ++recvPackets;
    }
}
