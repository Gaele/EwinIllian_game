package sprite;

import graphicLibrary.ImagesLoader;

import java.awt.Graphics;

public class SpriteZero extends Sprite {

	// time between 2 frames
	private static int programPeriod;
	// time between 2 images
	private static int periodBetween2Images;

	private int etat;
	private int oldEtat;
	private boolean modifEtat = false;
	private static final int REPOS = 1;
	private static final int COURT_GAUCHE = 2;
	private static final int COURT_DROITE = 3;
	private static final int SAUT = 4;

	private static final boolean SENS_GAUCHE = false;
	private static final boolean SENS_DROITE = true;
	private boolean sens = SENS_DROITE;
	private static final int VITESSECOURSE = 5;

	public SpriteZero(final int x, final int y, final int w, final int h,
			final ImagesLoader imsLd, final String name, final String image,
			final int programPeriod, final int periodBetween2Images) {
		super(x, y, w, h, imsLd, name, image);
		etat = COURT_GAUCHE;
		oldEtat = REPOS;
		SpriteZero.programPeriod = programPeriod;
		SpriteZero.periodBetween2Images = periodBetween2Images;
	}

	@Override
	public void updateSpriteSpeed() {
		if (isActive && modifEtat) {
			switch (etat) {
			case REPOS:
				dx = 0;
				stopLooping();
				break;
			case COURT_GAUCHE:
				sens = SENS_GAUCHE;
				loopImage(programPeriod, periodBetween2Images, 2);
				dx = -SpriteZero.VITESSECOURSE;
				break;
			case COURT_DROITE:
				sens = SENS_DROITE;
				loopImage(programPeriod, periodBetween2Images, 2);
				dx = SpriteZero.VITESSECOURSE;
				break;
			case SAUT:
				setImage("saute01");
				loopImage(programPeriod, periodBetween2Images, 2);
				break;
			}
			modifEtat = false;
		}
		super.updateSpriteSpeed();
	}

	@Override
	public void drawSprite(final Graphics g) {
		if (isActive) {
			super.prepareDrawSprite(g);
			if (image != null) { // the sprite has no image
				if (sens == SENS_DROITE) {
					// g.drawImage(image, locx, locy, null);
				} else {
					// g.drawImage(image, locx + getImage().getWidth(), locy,
					// locx, locy + getImage().getHeight(), 0, 0,
					// getImage().getWidth(), getImage().getHeight(), null);
				}
			}
		}
	}

	// Actions
	public void courtGauche() {
		if ((etat != COURT_GAUCHE) && (etat != SAUT)) {
			oldEtat = etat;
			etat = COURT_GAUCHE;
			modifEtat = true;
		}
	}

	public void courtDroite() {
		if ((etat != COURT_DROITE) && (etat != SAUT)) {
			oldEtat = etat;
			etat = COURT_DROITE;
			modifEtat = true;
		}
	}

	public void repos() {
		if ((etat != REPOS) && (etat != SAUT)) {
			oldEtat = etat;
			etat = REPOS;
			modifEtat = true;
		}
	}

	public void saut() {
		if (etat != SAUT) {
			oldEtat = etat;
			etat = SAUT;
			modifEtat = true;
		}
	}

	@Override
	public void imageSequenceEnded(final String imageName) {
		System.out.println("Sequence end (SpriteZero)");
		if (etat == SAUT) {
			etat = REPOS;
			setImage("Zero_court_gauche");
		}

	}
}
