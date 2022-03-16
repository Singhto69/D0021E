package Sim;

import java.util.ArrayList;
import java.util.HashMap;

import Sim.CustomEvents.ChangeInterfaceEvent;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
    protected NetworkAddr _id;
    protected SimEnt _peer;
    protected int _sentmsg = 0;
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
    
    HashMap<Integer, Message> expectedAck = new HashMap<Integer, Message>();
    //int expectedAck = 0;

    public void StartSending(int network, int node, int number, int timeInterval, int startSeq) {
        _stopSendingAfter = number;
        _timeBetweenSending = timeInterval;
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(), 0);
    }

    public void TCP(NetworkAddr dst) {
    	//System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " starts 3-way-handshake");
    	_seq = (int) (Math.random()*1000);
        Message msg = new Message(this.getAddr(), dst, _seq, true, false, 0);
        _seq++;
        expectedAck.put(_seq, msg);
        
        System.out.println("3 way handshake Init");
        TCP_status(msg);
        send(_peer, msg, 0);
    }
    
    public void TCP_status(Message msg) {
    	System.out.println("Node " + _id.networkId() + "." + _id.nodeId()+ " sends Syn: "+msg.synflag() + " - seq: " + msg.seq()+ 
    			" - Ack: "+ msg.ackflag() + " - acknum: " + msg.ack()
    			+ " Expected Ack: " + expectedAck);
    }


//**********************************************************************************	

    // This method is called upon that an event destined for this node triggers.

    public void recv(SimEnt src, Event ev) {
        if (ev instanceof TimerEvent) {
            if (_stopSendingAfter > _sentmsg) {
                _sentmsg++;
                send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), 0);
                send(this, new TimerEvent(), _timeBetweenSending);
                System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " sent message with seq: " + _seq + " at time " + SimEngine.getTime());
                _seq++;

                statisticsSend();
            }
        }

        if (ev instanceof Message) {
            System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " receives message with seq: " + ((Message) ev).seq() + " at time " + SimEngine.getTime());
            statisticsRecv();
            
            int timeDelay = (int) (Math.random()*10);
            Message msg = (Message) ev;
            boolean ackflag = msg.ackflag();
            boolean synflag = msg.synflag();
            
            int recvSeq = msg.seq();
            int recvAck = msg.ack();
            NetworkAddr dst = msg.source();
            
            //bullshit
            Message newMsg = new Message(_id, _id, 0, false, false, 0);
            
            //3 way handshake
            if (synflag && !ackflag) { //Receive Syn (3-w-h)
                _seq = (int) (Math.random()*1000);
                newMsg = new Message(_id, dst, _seq, true, true, recvSeq+1);
                
                _seq++;
                expectedAck.put(_seq, newMsg);
                send(_peer, newMsg, timeDelay);
            }else if (ackflag && synflag){ //Receive Syn/Ack (3-w-h)
            	newMsg = new Message(_id, dst, _seq, false, true, recvSeq+1);
               
                _seq++;
                expectedAck.put(_seq, newMsg);
                send(_peer, newMsg, timeDelay);
            }else if (!(ackflag || synflag)){ //Receive regular message (send Ack back)
            	newMsg = new Message(_id, dst, _seq, false, true, recvSeq+69);
                send(_peer, newMsg, timeDelay);
            }
            
            // Handle ack
            if(ackflag){
            	//Remove buffered message when receive ack
            	expectedAck.remove(recvAck);
            }
            
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
