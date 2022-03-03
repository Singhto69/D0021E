package Sim.CustomClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private BufferedWriter logger;
    public FileWriter logfile;
    private String dirName;
    private String fileName;

    public NodeGaussian(int network, int node, double mu, double sigma) {
        super(network, node);

        this.rand = new Random();
        this.mu = mu;
        this.sigma = sigma;
        this.dirName = System.getProperty("user.dir");
    }

    private void logentry(String text) throws IOException {
        try {
            File dir = new File(this.dirName);
            File actualFile = new File(dir, "loggaussian.txt");
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

    public void recv(SimEnt src, Event ev) throws IOException{
        if (ev instanceof TimerEvent) {
            if (_stopSendingAfter > _sentmsg) {
                _sentmsg++;
                send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), 0);

                //Normal Distribution with (5) mean and (2) in st.d.
				// https://stackoverflow.com/questions/31754209/can-random-nextgaussian-sample-values-from-a-distribution-with-different-mean

				send(this, new TimerEvent(), _timeBetweenSending + rand.nextGaussian() * sigma + mu);

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
}
