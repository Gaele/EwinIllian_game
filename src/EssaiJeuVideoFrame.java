

import javax.swing.JFrame;



public class EssaiJeuVideoFrame extends JFrame {

	GamePanel gp;
	
	public EssaiJeuVideoFrame() {
		setTitle("Essai 01 Jeu vidéo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setLocationRelativeTo(null);
		setResizable(false);
		
		gp = new GamePanel();
		setContentPane(gp);
		pack();
		gp.addNotify();
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		EssaiJeuVideoFrame gf = new EssaiJeuVideoFrame();
	}
}
