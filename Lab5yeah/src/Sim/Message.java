package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Message implements Event {
    private NetworkAddr _source;
    private NetworkAddr _destination;
    private int _seq = 0;
    private boolean _synflag;
    private boolean _ackflag;
    private int _acknum;

    public int ttl = 0;

    public Message(NetworkAddr from, NetworkAddr to, int seq, boolean syn, boolean ack, int acknum) {
        _source = from;
        _destination = to;
        _seq = seq;
        _synflag = syn;
        _ackflag = ack;
        _acknum = acknum;
    }

    public Message(NetworkAddr from, NetworkAddr to, int seq) {
        _source = from;
        _destination = to;
        _seq = seq;

    }


    public boolean synflag() {
        return _synflag;
    }

    public boolean ackflag() {
        return _ackflag;
    }

    public int ack() {
        return _acknum;
    }

    public void setack() {
        _ackflag = !_ackflag;
    }

    public NetworkAddr source() {
        return _source;
    }

    public void seDestination(NetworkAddr addr) {
        _destination = addr;
    }

    public NetworkAddr destination() {
        return _destination;
    }

    public int seq() {
        return _seq;
    }

    public void entering(SimEnt locale) {
    }

}
	
