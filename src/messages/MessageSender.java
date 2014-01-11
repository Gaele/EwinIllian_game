package messages;

import java.util.Comparator;
import java.util.TreeSet;

import entities.Entity;
import entities.EntityManager;


public class MessageSender {

	TreeSet<Message> priorityQ;
	private volatile static MessageSender uniqueInstance;

	public MessageSender() {

		final Comparator<Message> c = new Comparator<Message>() {
			@Override
			public int compare(final Message arg0, final Message arg1) {
				if (arg0.getAtTime() > arg1.getAtTime()) {
					return 1;
				} else if (arg0.getAtTime() < arg1.getAtTime()) {
					return -1;
				}
				return 0;
			}
		};

		priorityQ = new TreeSet<Message>(c);

	}

	public void sendMessage(final int sender, final int receiver,
			final int message, final long delay, final Object extra) {
		final Message m = new Message(sender, receiver, message, extra);
		if (delay <= 0.0) {
			final Entity r = EntityManager.getInstance().getEntityFromId(
					receiver);
			send(r, m);
		} else {
			m.setAtTime(delay);
			priorityQ.add(m);
		}
	}

	private void sendDelayedMessage() {
		final long currentTime = System.nanoTime();

		while (priorityQ.first().getAtTime() > currentTime) {
			final Message m = priorityQ.first(); // get message
			final Entity r = EntityManager.getInstance().getEntityFromId(
					m.getReceiver()); // get the receiver
			send(r, m); // send the message

			priorityQ.remove(priorityQ.first()); // remove first message
		}
	}

	private void send(final Entity receiver, final Message m) {
		receiver.handleMessage(m);
	}

	public static MessageSender getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new MessageSender();
				}
			}
		}
		return uniqueInstance;
	}

}
