package Sim.CustomClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
    private double thisoffset;
    //private int[] seqtooffset;

    private HashMap<Double, Integer> seqtooffset = new HashMap<>();

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

    public void hashmaplogfile() throws IOException {
        try {


            String text;
            File dir = new File(this.dirName);
            File actualFile = new File(dir, "loggaussian.txt");
            this.logfile = new FileWriter(actualFile, true);
            this.logger = new BufferedWriter(this.logfile);

            for (Double key : seqtooffset.keySet()) {
                text = "s:" + _id.networkId() + ":" + _id.nodeId() + ":" + key + ":" + seqtooffset.get(key);
                this.logger.append(text);
                this.logger.newLine();
            }

            this.logger.close();
            System.out.println("Successful Write to:" + this.dirName);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void recv(SimEnt src, Event ev) throws IOException {
        if (ev instanceof TimerEvent) {
            if (_stopSendingAfter > _sentmsg) {
                _sentmsg++;
                send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), 0);

                //Normal Distribution with (5) mean and (2) in st.d.
                // https://stackoverflow.com/questions/31754209/can-random-nextgaussian-sample-values-from-a-distribution-with-different-mean

                thisoffset = _timeBetweenSending + rand.nextGaussian() * sigma + mu;

                thisoffset = Math.round(thisoffset * 10.0) / 10.0;

                if (seqtooffset.containsKey(thisoffset)) {
                    int x = seqtooffset.get(thisoffset) + 1;
                    seqtooffset.put(thisoffset, x);

                } else {
                    seqtooffset.put(thisoffset, 1);
                }


                send(this, new TimerEvent(), thisoffset);

                System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " sent message with seq: " + _seq + " at time " + SimEngine.getTime());

                //logentry("s:" + _id.networkId() + ":" + +_id.nodeId() + ":" + (int) thisoffset + ":" + seqtooffset.get(thisoffset));

                _seq++;
                statisticsSend();
            }
            else{
                hashmaplogfile();
            }
        }
        if (ev instanceof Message) {
            if (seqtooffset.containsKey(thisoffset)) {
                int x = seqtooffset.get(thisoffset) + 1;
                seqtooffset.put(thisoffset, x);

            } else {
                seqtooffset.put(thisoffset, 1);
            }
            System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " receives message with seq: " + ((Message) ev).seq() + " at time " + SimEngine.getTime());
            //logentry("r:" + _id.networkId() + ":" + +_id.nodeId() + ":" + (int) thisoffset + ":" + seqtooffset.get(thisoffset));
            statisticsRecv();
        }

        System.out.println("Node: " + _id.nodeId() + ". Packets sent: " + sentPackets + ". Packets recv: " + recvPackets);
    }
}
