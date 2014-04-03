package game.controllers;

import game.core.G;
import game.core.Game;
import game.core.Game.DM;

import java.util.Random;

import algorithme_apprentissage.NotreAlgorithmeGenetique;

/**
 * Contrôleur de Pacman utilisé pour les tests de NotreAlgorithmeGenetique
 * 
 * @author Leleux Laurent & Kévin Moulart
 */
public final class PacmanControllerApprenant implements PacManController {

	// Variable contenant la valeur de la distance maximale dans le labyrinthe
	// Pour des raisons pratiques, elle est initialisée la première fois que
	// l'on appelle le getAction
	public static double distance_max;
	// Tableau des gènes de l'individu au commandes
	private double[] genes;
	// Générateur de hasard
	private static Random rand = new Random();
	// Type de mesure que l'on utilisera ici
	private DM measure = DM.PATH;
	// Définis quel fantome prendre
	private static final int MANGEABLE = 0;
	private static final int AGRESSIF = 1;

	/**
	 * Constructeur du controleur de pacman
	 * 
	 * @param chromosome
	 */
	public PacmanControllerApprenant(double[] chromosome) {
		this.genes = chromosome;
	}

	/**
	 * Fonction qui décide de la direction que le pacman doit prendre en
	 * fonction du hasard et de ses propensions à divers comportements, définies
	 * par les gènes reçus
	 * 
	 * @param game
	 * @param timeDue
	 */
	public int getAction(Game game, long timeDue) {
		// Propension du fantome à aller dans chaque direction
		// Ces propensions ont une valeur initiale mais seront impactée par
		// toutes les autres propensions si jeur jet de hasard réussi
		double[] propensions = {
				genes[NotreAlgorithmeGenetique.Genes.PROPENSION_HAUT.ordinal()],
				genes[NotreAlgorithmeGenetique.Genes.PROPENSION_DROITE
						.ordinal()],
				genes[NotreAlgorithmeGenetique.Genes.PROPENSION_BAS.ordinal()],
				genes[NotreAlgorithmeGenetique.Genes.PROPENSION_GAUCHE
						.ordinal()] };

		// Position du pacman
		int positionPacman = game.getCurPacManLoc();
		// Pastille la plus proche
		int pastilleProche = getPastilleProche(game, positionPacman);
		// Fantômes les plus proches (MANGEABLE ou AGRESSIF)
		int[] fantomesProches = getFantomesLesPlusProches(game, positionPacman);
		// Truc (fantôme ou pastille) mangeable le plus proche
		int trucMangeableProche = getTrucMangeableProche(game, positionPacman,
				pastilleProche, fantomesProches[MANGEABLE]);

		/* Decision de l'action à entreprendre */
		// Si notre pacman est gourmand
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.GOURMANDISE
				.ordinal()]) {
			// On teste toujours qu'il reste un truc à manger
			if (trucMangeableProche > 0) {
				// Si il est proche de pacman
				if (game.getPathDistance(positionPacman, trucMangeableProche) < genes[NotreAlgorithmeGenetique.Genes.DIST_GOURMAND
						.ordinal()] * distance_max)
					// On ajoute à la direction vers laquelle on doit aller pour
					// le manger un nombre inversément proportionnel à la
					// distance à ce truc
					propensions[game.getNextPacManDir(trucMangeableProche,
							true, measure)] += 1 / (NotreAlgorithmeGenetique.FACTEUR_DISTANCE * game
							.getPathDistance(trucMangeableProche,
									positionPacman));
			}
		}
		// Si notre pacman est couard
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.COUARDISE
				.ordinal()]) {
			// On test toujours si il a un ennemi
			if (fantomesProches[AGRESSIF] > 0) {
				// Si l'ennemi le plus proche est proche de pacman
				if (game.getPathDistance(positionPacman,
						fantomesProches[AGRESSIF]) < genes[NotreAlgorithmeGenetique.Genes.DIST_PEUR
						.ordinal()] * distance_max)
					// On ajoute à la direction vers laquelle on doit aller pour
					// le fuir un nombre inversément proportionnel à la
					// distance à ce fantome
					propensions[game.getNextPacManDir(
							fantomesProches[AGRESSIF], false, measure)] += 1 / (NotreAlgorithmeGenetique.FACTEUR_DISTANCE * game
							.getPathDistance(fantomesProches[AGRESSIF],
									positionPacman));
			}
		}
		// Si notre pacman est agressif
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.AGRESSIVITE
				.ordinal()]) {
			// On teste toujours si il a un ennemi a manger
			if (fantomesProches[MANGEABLE] > 0) {
				// Si cet ennemi est proche du pacman
				if (game.getPathDistance(positionPacman,
						fantomesProches[MANGEABLE]) < genes[NotreAlgorithmeGenetique.Genes.DIST_AGRESSION
						.ordinal()] * distance_max)
					// On ajoute à la direction vers laquelle on doit aller pour
					// le manger un nombre inversément proportionnel à la
					// distance à ce fantome
					propensions[game.getNextPacManDir(
							fantomesProches[MANGEABLE], true, measure)] += 1 / (NotreAlgorithmeGenetique.FACTEUR_DISTANCE * game
							.getPathDistance(fantomesProches[MANGEABLE],
									positionPacman));
			}
		}
		// Si notre pacman est stupide
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.STUPIDITE
				.ordinal()]) {
			// On ajoute 1 à une direcion aléatoire
			propensions[directionAleatoire(game)] += 1;
		}
		// Si notre pacman a le mal de vivre
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.MAL_DE_VIVRE
				.ordinal()]) {
			// On test toujours si il a un ennemi
			if (fantomesProches[AGRESSIF] > 0) {
				// On ajoute à la direction vers laquelle on doit aller pour
				// le manger un nombre inversément proportionnel à la
				// distance à ce fantome
				propensions[game.getNextPacManDir(fantomesProches[AGRESSIF],
						true, measure)] += 1 / (NotreAlgorithmeGenetique.FACTEUR_DISTANCE * game
						.getPathDistance(fantomesProches[AGRESSIF],
								positionPacman));
			}
		}
		// Si notre pacman est indécis
		if (rand.nextDouble() < genes[NotreAlgorithmeGenetique.Genes.INDECISION
				.ordinal()]) {
			// On ajoute 1 à la direction inverse de sa direction actuelle
			int dir = game.getReverse(game.getCurPacManDir());
			if (dir < 4)
				propensions[dir] += 1;
		}

		// On calcule enfin la direction qui est la plus fortement demandée
		double max = -1;
		int direction = G.UP;
		for (int i = 0; i < propensions.length; i++) {
			if (propensions[i] > max) {
				direction = i;
				max = propensions[i];
			}
		}
		// Et on la choisit
		return direction;
	}

	/**
	 * Cherche et revoie la pastille la plus proche du pacman
	 * 
	 * @param game
	 * @param positionPacman
	 * @return la pastille la plus proche
	 */
	private int getPastilleProche(Game game, int positionPacman) {
		/*
		 * Recherche des plus proches éléments (pill, powerPill, edibleGhost,
		 * agressiveGhost)
		 */
		int[] pastillesNormales = game.getPillIndicesActive();
		pastillesNormales = enleveNeg(pastillesNormales);
		// Si la distance maximale dans le labyrinthe n'et pas initialisée, on
		// l'initialise ici
		if (distance_max < 0)
			distance_max = game.getPathDistance(pastillesNormales[0],
					pastillesNormales[pastillesNormales.length - 1]);

		int[] pastillesDePuissance = game.getPowerPillIndicesActive();
		pastillesDePuissance = enleveNeg(pastillesDePuissance);
		int[] positionsDesPastilles = new int[pastillesNormales.length
				+ pastillesDePuissance.length];
		// On ajoute les pastilles à la liste des cibles
		for (int i = 0; i < pastillesNormales.length; i++)
			positionsDesPastilles[i] = pastillesNormales[i];
		for (int i = 0; i < pastillesDePuissance.length; i++)
			positionsDesPastilles[pastillesNormales.length + i] = pastillesDePuissance[i];

		// Pillule la plus proche
		return game.getTarget(positionPacman, positionsDesPastilles, true,
				DM.PATH);
	}

	/**
	 * Recherche le fantôme mangeable et le fantôme agressif le plus proches
	 * 
	 * @param game
	 * @param positionPacman
	 * @return fantômes les plus proches (agressif ou mangeable)
	 */
	private int[] getFantomesLesPlusProches(Game game, int positionPacman) {
		int[] positionsFantomes = new int[4];
		int nombreFantomesMangeables = 0;
		for (int i = 0; i < 4; i++) {
			positionsFantomes[i] = game.getCurGhostLoc(i);
			if (game.isEdible(i))
				nombreFantomesMangeables++;
		}

		// On enlève des positions des fantomes, les positions négatives
		// (fantome dans le repère)
		positionsFantomes = enleveNeg(positionsFantomes);
		int nombreFantomesagressifs = 4 - nombreFantomesMangeables;
		int[] fantomesMangeables = new int[nombreFantomesMangeables];
		int[] fantomesagressifs = new int[nombreFantomesagressifs];
		// On répartit les fantomes entre les fantomes mangeables et agressifs
		for (int i = 0; i < 4; i++) {
			if (game.isEdible(i))
				fantomesMangeables[--nombreFantomesMangeables] = positionsFantomes[i];
			else
				fantomesagressifs[--nombreFantomesagressifs] = positionsFantomes[i];
		}
		// Fantome mangeable le plus proche
		int fantomeMangeableProche = game.getTarget(positionPacman,
				fantomesMangeables, true, measure);
		// Fantome agressif le plus proche
		int fantomeagressifProche = game.getTarget(positionPacman,
				fantomesagressifs, true, measure);
		int[] fantomesProches = { fantomeMangeableProche, fantomeagressifProche };
		return fantomesProches;
	}

	/**
	 * Recherche le truc (fantome ou pastille) mangeable le plus proche
	 * 
	 * @param game
	 * @param positionPacman
	 * @param pastilleProche
	 * @param fantomesProches
	 * @return le truc mangeable le plus proche
	 */
	private int getTrucMangeableProche(Game game, int positionPacman,
			int pastilleProche, int fantomeMangeableProche) {
		// Tableau des trucs mangeables les plus proches (fantomes/pastille)
		int[] trucsMangeablesProches = new int[2];
		trucsMangeablesProches[0] = pastilleProche;
		if (fantomeMangeableProche > 0)
			trucsMangeablesProches[1] = fantomeMangeableProche;
		else
			trucsMangeablesProches[1] = (int) (distance_max + 1);
		// Truc (fantome ou pillule) le plus proche
		return game.getTarget(positionPacman, trucsMangeablesProches, true,
				measure);
	}

	/**
	 * Fonction qui enlève tous les négatifs d'un tableau donné
	 * 
	 * @param tab
	 * @return le même tableau, sans ses négatifs
	 */
	private int[] enleveNeg(int[] tab) {
		int[] newTab = new int[tab.length];
		int j = 0;
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] >= 0)
				newTab[j++] = tab[i];
		}
		int[] output = new int[j];
		for (int i = 0; i < j; i++) {
			output[i] = newTab[i];
		}
		return output;
	}

	/**
	 * Fonction qui renvoie une direction aléatoire
	 * 
	 * @param game
	 * @return une direction aléatoire
	 */
	private int directionAleatoire(Game game) {
		int[] possibleDirs = game.getPossiblePacManDirs(true);
		return possibleDirs[rand.nextInt(possibleDirs.length)];
	}
}