package messages;

public class Message {

	private int sender;
	private int receiver;
	private int message;
	private long atTime; // send the message at this time, delayed the send
	private Object extraInfos;
	
	public Message(int sender, int receiver, int message, Object infos) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.atTime = 0;
		this.extraInfos = infos;
	}

	// GETTERS
	
	public int getSender() {
		return sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public int getMessage() {
		return message;
	}

	public double getAtTime() {
		return atTime;
	}

	public Object getExtraInfos() {
		return extraInfos;
	}
	
	// SETTERS
	
	public void setAtTime(long t) {
		atTime = t + System.nanoTime();
	}
	
}
