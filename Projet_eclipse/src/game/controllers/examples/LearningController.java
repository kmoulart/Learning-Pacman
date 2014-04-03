package game.controllers.examples;

import game.controllers.GhostController;
import game.core.G;
import game.core.Game;
import game.core.Game.DM;

import java.util.Random;

import algorithme_apprentissage.OurGeneticAlgorithm;


public class LearningController implements GhostController {

	private double[] chromosome;

	private static Random rand = new Random();

	public LearningController(double[] chromosome) {
		this.chromosome = chromosome;
	}

	public int[] getActions(Game game, long timeDue) {
		int pacloc = game.getCurPacManLoc();
		int[] directions = new int[Game.NUM_GHOSTS];
		boolean attract;
		DM measure = DM.values()[(int) (chromosome[OurGeneticAlgorithm.MEASURE] * 3)];
		for (int i = 0; i < directions.length; i++) // for each ghost
		{
			// la propension du fantome à aller dans chaque direction
			// double[] propensions = { chromosome[PROPENSION_HAUT],
			// chromosome[PROPENSION_DROITE], chromosome[PROPENSION_BAS],
			// chromosome[PROPENSION_GAUCHE] };
			if (game.isEdible(i)) {
				// comportement quand mangeable

				double tot = chromosome[OurGeneticAlgorithm.PROPENSION_SUICIDE] + chromosome[OurGeneticAlgorithm.PROPENSION_SURVIE];
				double slice = tot * rand.nextDouble();
				if (slice < chromosome[OurGeneticAlgorithm.PROPENSION_SUICIDE])
					directions[i] = game.getNextGhostDir(i, pacloc, true, measure);
				else
					directions[i] = game.getNextGhostDir(i, pacloc, false, measure);
			} else {
				// comportement quand non mangeable

				double tot = chromosome[OurGeneticAlgorithm.PROPENSION_FUITE] + chromosome[OurGeneticAlgorithm.PROPENSION_ATTAQUE];
				double slice = tot * rand.nextDouble();
				if (slice < chromosome[OurGeneticAlgorithm.PROPENSION_FUITE])
					directions[i] = game.getNextGhostDir(i, pacloc, false, measure);
				else
					directions[i] = game.getNextGhostDir(i, pacloc, true, measure);
			}
		}
		return directions;
	}

	private int getRandomDir(int whichGhost, Game game) {
		if (game.ghostRequiresAction(whichGhost)) {
			int[] possibleDirs = game.getPossibleGhostDirs(whichGhost);
			return possibleDirs[G.rnd.nextInt(possibleDirs.length)];
		}
		return 0;
	}
}
