
import java.text.DecimalFormat;

public class GameStats {

	/**
	 * Enregistre des statistiques toutes les secondes environs
	 */
	private static long MAX_STATS_INTERVAL = 1000L;
	
	/**
	 * Nombre de FPS stocké pour avoir une moyenne
	 */
	private static int NUM_FPS = 10;
	private static int NUM_UPS = 10;
	
	private long statsInterval = 0L;// En ms
	private long prevStatsTime = System.nanoTime();
	private long totalElapsedTime = 0L;
	
	private long frameCount = 0;// Nombre de tour de boucle effectué
	private double fpsStore[];
	private long statsCount = 0L;// incrémenté à chaque fois qu'on ajoute une valeur à fpsStore
	private double averageFPS = 0.0;
	
	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;
	
	private DecimalFormat df = new DecimalFormat("0.##");
	private DecimalFormat timedf = new DecimalFormat("0.####");
	
	private int period;// periode entre les dessins en ms
	
	public GameStats(int period) {
		this.period = period;
		this.fpsStore = new double[NUM_FPS];
		this.upsStore = new double[NUM_FPS];
		for(int i = 0; i < NUM_FPS; i++) {
			this.fpsStore[i] = 0;
			this.upsStore[i] = 0;
		}
	}
	
	public void reportStats() {
		frameCount++;
		statsInterval+=period;
		if(statsInterval >= MAX_STATS_INTERVAL) {
			long timeNow = System.nanoTime();
			
			long realElapsedTime = timeNow -prevStatsTime;
			totalElapsedTime+=realElapsedTime;
			
			long sInterval = (long)statsInterval*1000000L;// ms -> ns
			double timingError = ((double)(realElapsedTime - sInterval)) / sInterval * 100.0;
			totalFramesSkipped += framesSkipped;
			
			double actualFPS = (((double)frameCount / totalElapsedTime) * 1000000000L);
			double actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			// store the latest FPS
			fpsStore[(int)statsCount%NUM_FPS] = actualFPS;
			upsStore[(int)statsCount%NUM_FPS] = actualUPS;
			statsCount++;
			
			double totalFPS = 0.0;// nombre de FPS stockées
			double totalUPS = 0.0;
			for(int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}
			if(statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS/statsCount;
				averageUPS = totalUPS/statsCount;
			} else {
				averageFPS = totalFPS/NUM_FPS;
				averageUPS = totalUPS/NUM_FPS;
			}
			System.out.println(
					timedf.format((double) statsInterval/1000) + " " +
					timedf.format((double) realElapsedTime/1000000000L) + "s " +
					timedf.format(timingError) + "% " +
					frameCount + "c " +
					framesSkipped + "/" + totalFramesSkipped + " skip; " +
					df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " +
					df.format(actualUPS) + " " + df.format(averageUPS) + " aups"
					);
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L;
		}
		
	}
	
	public void addFrameSkipped() {
		framesSkipped++;
	}
	
	
	
}
