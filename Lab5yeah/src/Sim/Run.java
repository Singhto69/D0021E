package Sim;

import Sim.CustomClasses.Lossylink;
import Sim.CustomEvents.ChangeNetworkEvent;

// An example of how to build a topology and starting the simulation engine

public class Run {
    public static void main(String[] args) {
        Link a = new Link();
        Link b = new Link();

        //Link r = new Link();
        Lossylink r = new Lossylink(0.2);

        Node A = new Node(1, 1);
        Node B = new Node(2, 1);

        //Connect links to hosts
        A.setPeer(a);
        B.setPeer(b);

        Router R1 = new Router(4, 1);
        Router R2 = new Router(4, 2);

        //Routers to each other
        R1.connectInterface(0, r, R2);
        R2.connectInterface(0, r, R1);

        //Network 1
        R1.connectInterface(1, a, A);

        //Network 2
        R2.connectInterface(2, b, B);

        A.StartSending(B.getAddr().networkId(), B.getAddr().nodeId(), 10, 10, A._seq, 1, 50);

        // Start the simulation engine and of we go!
        Thread t = new Thread(SimEngine.instance());

        t.start();
        try {
            t.join();
        } catch (Exception e) {
            System.out.println("The motor seems to have a problem, time for service?");
        }
    }
}
