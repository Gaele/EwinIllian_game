package audio;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

// The class implements the MetaEventListener interface to detect when the sequence has reached the end of its tracks.
public class PlayMidiSequence implements MetaEventListener {

	// midi meta-event constant used to signal the end of a track
	private static final int END_OF_TRACK = 47;

	private final static String SOUND_DIR = "Sounds/";

	private Sequencer sequencer;
	private Synthesizer synthesizer;
	private Sequence seq = null;
	private final String fileName;
	private final DecimalFormat df;
	private final boolean looping;
	// number of time to jump when we restart
	private final float restartAt;
	private int sequencerTickPerTime;

	/**
	 * 
	 * @param fnm
	 *            the file name to load in the SOUND_DIR directory
	 * @param loop
	 *            does the music must loop or not ?
	 * @param restart
	 *            number of time to jump when we restart
	 */
	public PlayMidiSequence(final String fnm, final boolean loop,
			final int restart) {
		df = new DecimalFormat("0.#");
		looping = loop;
		restartAt = restart;

		fileName = SOUND_DIR + fnm;
		initSequencer();
		loadMidi(fileName);
		// play();
	}

	private void initSequencer() {
		try {
			sequencer = MidiSystem.getSequencer(false);

			if (sequencer == null) {
				System.out.println("Cannot get a sequencer");
				System.exit(0);
			}

			sequencer.open();
			sequencer.addMetaEventListener(this);

			// maybe the sequencer is not the same as the synthesizer
			// so link sequencer --> synth (this is required in J2SE 5.0)

			final MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
			int microsoft = -1;
			int i = 0;
			System.out.println("HELP");
			for (final MidiDevice.Info device : devices) {
				if (device.getName().equals("Microsoft GS Wavetable Synth")) {
					microsoft = i;
					System.out.println("Use: Microsoft GS Wavetable Synth");
				}
				i++;
			}

			// load synthesizer
			if (microsoft != -1) {
				// final Synthesizer s;
				final MidiDevice s = MidiSystem
						.getMidiDevice(devices[microsoft]);
				try {
					s.open();
					final Receiver receiver = s.getReceiver();
					final Transmitter transmitter = sequencer.getTransmitter();
					transmitter.setReceiver(receiver);
				} catch (final MidiUnavailableException e) {
					System.out.println("ERROR on : "
							+ devices[microsoft].getName());
					System.out.println(e.getMessage());
				}
			} else if (!(sequencer instanceof Synthesizer)) {
				System.out.println("Link the sequencer and the synthesizer");
				synthesizer = MidiSystem.getSynthesizer();
				synthesizer.open();
				System.out.println("Synthesizer: "
						+ synthesizer.getDeviceInfo().getName());
				final Receiver synthReceiver = synthesizer.getReceiver();
				final Transmitter seqTransmitter = sequencer.getTransmitter();
				seqTransmitter.setReceiver(synthReceiver);
			} else {
				synthesizer = (Synthesizer) sequencer;
			}

			// this function display the available devices :
			// try {
			// test5();
			// } catch (final Exception e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }

		} catch (final MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void test5() throws Exception {
		final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (final MidiDevice.Info inf : infos) {
			final MidiDevice transDevice = MidiSystem.getMidiDevice(inf);
			final MidiDevice.Info[] infos2 = MidiSystem.getMidiDeviceInfo();
			for (final MidiDevice.Info inf2 : infos2) {
				final MidiDevice recDevice = MidiSystem.getMidiDevice(inf2);
				if (!transDevice.equals(recDevice)) {
					try {
						transDevice.open();
						recDevice.open();
						final Transmitter tran = transDevice.getTransmitter();
						final Receiver rec = recDevice.getReceiver();
						tran.setReceiver(rec);
						System.out.println("Connected " + inf.getName() + "("
								+ transDevice.getClass().getSimpleName()
								+ ") to " + inf2.getName() + "("
								+ recDevice.getClass().getSimpleName() + ")");
						// Thread.sleep(10000);
						tran.setReceiver(null);
					} catch (final Exception e) {
					} finally {
						transDevice.close();
						recDevice.close();
					}

				}
			}
		}
	}

	private void loadMidi(final String fnm) {
		try {
			// seq = MidiSystem.getSequence(getClass().getResource(fnm));
			seq = MidiSystem.getSequence(new File(fnm));

			// get the type of the sequencer
			// System.out.println("Sequence type : " + seq.getDivisionType());
			final float seqType = seq.getDivisionType();
			if (seqType == Sequence.PPQ) {
				System.out.println("PPQ");
			} else if (seqType == Sequence.SMPTE_24) {
				System.out.println("SMPTE_24");
			} else if (seqType == Sequence.SMPTE_25) {
				System.out.println("SMPTE_25");
			} else if (seqType == Sequence.SMPTE_30) {
				System.out.println("SMPTE_30");
			} else if (seqType == Sequence.SMPTE_30DROP) {
				System.out.println("SMPTE_30DROP");
			}
			// get number of tick per note
			System.out.println(seq.getResolution());
			sequencerTickPerTime = seq.getResolution();

			final double duration = ((double) seq.getMicrosecondLength()) / 1000000;
			// System.out.println("Duration: " + df.format(duration) + "secs");

		} catch (final InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play() {
		if ((sequencer != null) && (seq != null)) {
			try {
				sequencer.setSequence(seq);
				sequencer.start(); // start playing it
			} catch (final InvalidMidiDataException e) {
				e.printStackTrace();
			} // load MIDI into sequencer
		} else {
			System.out.println("Sequencer null : " + sequencer == null);
			System.out.println("Sequence null : " + seq == null);
		}
	}

	public void stop() {
		sequencer.stop();
	}

	public void resume() {
		sequencer.start();
	}

	// /**
	// * @param args
	// */
	// public static void main(final String[] args) {
	// final PlayMidiSequence pms = new PlayMidiSequence("Jingle.mid", true, 4);
	// try {
	// // Thread.sleep(1000);
	// // pms.stop();
	// // Thread.sleep(1000);
	// // pms.resume();
	// Thread.sleep(9999999);
	// } catch (final InterruptedException e) {
	// System.out.println("Sleep interrupted");
	// }
	//
	// System.exit(0);
	// }

	@Override
	public void meta(final MetaMessage event) {
		if (event.getType() == END_OF_TRACK) {
			if ((sequencer != null) && sequencer.isOpen() && looping) {
				// System.out.println("Looping Midi: " + fileName);
				sequencer
						.setTickPosition((int) (restartAt * sequencerTickPerTime));
				sequencer.start();
			} else {
				// System.out.println("Exciting...");
				sequencer.close();
				System.exit(0);
			}
		}
	}

}
