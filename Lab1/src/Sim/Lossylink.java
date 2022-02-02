package Sim;

// This class implements a link without any loss, jitter or delay

import java.util.Random;

public class Lossylink extends Link{
    private SimEnt _connectorA=null;
    private SimEnt _connectorB=null;
    private int _now=0;
    private int delay;
    private int jitter;
    private int dropprobability;
    private Random rand;

    public Lossylink()
    {
        super();
        this.rand = new Random();
    }

    // Connects the link to some simulation entity like
    // a node, switch, router etc.

    public void setConnector(SimEnt connectTo)
    {
        if (_connectorA == null)
            _connectorA=connectTo;
        else
            _connectorB=connectTo;
    }

    // Called when a message enters the link

    public void recv(SimEnt src, Event ev)
    {
        if (ev instanceof Message)
        {
            System.out.println("Link recv msg, passes it through");

            if (src == _connectorA)
            {
                send(_connectorB, ev, _now);
            }
            else
            {
                send(_connectorA, ev, _now);
            }
        }
    }
}