package Sim;

import java.util.Random;

public class Lossylink extends SimEnt {
    private SimEnt _connectorA=null;
    private SimEnt _connectorB=null;
    private int _now=0;
    private int delay;
    private int jitter;
    private int dropprobability;


    public Lossylink()
    {
        super();
    }

    public void setConnector(SimEnt connectTo)
    {
        if (_connectorA == null)
            _connectorA=connectTo;
        else
            _connectorB=connectTo;
    }

    @Override
    public void recv(SimEnt source, Event event) {
        if (event instanceof Message)
        {
            System.out.println("Link recv msg, passes it through");
            if (source == _connectorA)
            {
                send(_connectorB, event, _now);
            }
            else
            {
                send(_connectorA, event, _now);
            }
        }

    }
}
