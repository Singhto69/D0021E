package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Node extends SimEnt {
    protected NetworkAddr _id;
    protected SimEnt _peer;
    protected int _sentmsg = 0;
    protected int _seq = 0;

    private BufferedWriter logger;
    public FileWriter logfile;
    private String dirName;
    private String fileName;

    public Node(int network, int node) {
        super();
        _id = new NetworkAddr(network, node);
        this.dirName = System.getProperty("user.dir");
    }

    private void logentry(String text) throws IOException {
        try {
            File dir = new File(this.dirName);
            File actualFile = new File(dir, "logcbr.txt");
            this.logfile = new FileWriter(actualFile, true);
            this.logger = new BufferedWriter(this.logfile);
            this.logger.append(text);
            this.logger.newLine();
            this.logger.close();
            System.out.println("Successful Write to:" + this.dirName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Sets the peer to communicate with. This node is single homed

    public void setPeer(SimEnt peer) {
        _peer = peer;

        if (_peer instanceof Link) {
            ((Link) _peer).setConnector(this);
        }
    }


    public NetworkAddr getAddr() {
        return _id;
    }

//**********************************************************************************	
    // Just implemented to generate some traffic for demo.
    // In one of the labs you will create some traffic generators

    protected int _stopSendingAfter = 0; //messages
    protected int _timeBetweenSending = 10; //time between messages
    protected int _toNetwork = 0;
    protected int _toHost = 0;

    public void StartSending(int network, int node, int number, int timeInterval, int startSeq) {
        _stopSendingAfter = number;
        _timeBetweenSending = timeInterval;
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(), 0);
    }

//**********************************************************************************	

    // This method is called upon that an event destined for this node triggers.

    public void recv(SimEnt src, Event ev) throws IOException {
        if (ev instanceof TimerEvent) {
            if (_stopSendingAfter > _sentmsg) {
                _sentmsg++;
                send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), 0);
                send(this, new TimerEvent(), _timeBetweenSending);
                System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " sent message with seq: " + _seq + " at time " + SimEngine.getTime());
                logentry("s:" + _id.networkId() + ":" + +_id.nodeId() + ":" + _seq + ":" + (int) SimEngine.getTime());
                _seq++;

                statisticsSend();
            }
        }

        if (ev instanceof Message) {
            System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " receives message with seq: " + ((Message) ev).seq() + " at time " + SimEngine.getTime());
            logentry("r:" + _id.networkId() + ":" + +_id.nodeId() + ":" + ((Message) ev).seq() + ":" + (int) SimEngine.getTime());
            statisticsRecv();
        }

        System.out.println("Node: " + _id.nodeId() + ". Packets sent: " + sentPackets + ". Packets recv: " + recvPackets);
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
