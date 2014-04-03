package algorithme_apprentissage;

import game.Exec;
import game.controllers.PacmanControllerApprenant;
import game.controllers.examples.RandomGhosts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Programme d'aprentissage via l'algorithme génétique des stratégies d'un
 * pacman
 * 
 * @author Leleux Laurent & Kévin Moulart
 */
public class NotreAlgorithmeGenetique {

	// Nombre de chromosomes dans la population
	static int TAILLE_POPULATION = 100;
	// Nombre de parties de test jouées par chaque chromosome pour son
	// évaluation de performance
	public static final int NOMBRE_PARTIE_PAR_CHROMOSOME = 3;
	// Chance de voir un gène muter lors du passage de génération
	private static double CHANCE_MUTATION = 0.05;
	// Chances de voir se produire un croisement entre deux chromosomes lors du
	// passage de génération
	private static double CHANCE_CROISEMENTS = 0.7;
	// Préférence en ce qui concerne le calcul de la performance : le score ou
	// le score²
	private static boolean CARRE = true;
	// Facteur de multiplication de la distance qui jouera pour amplifier la
	// propension à la gourmandise, a la couardise, à l'agressivité ou au mal
	// de vivre
	public static final double FACTEUR_DISTANCE = 3;
	// Precise des infos sur la serie de donnée qui sera logguée dans les
	// fichier
	// score / score² / score_avec_distances / score²_avec_distances
	public static final String SERIE = "score²";

	/*
	 * Différents gènes utilisés dans cet algorithme génétiques, reprenant
	 * chacun un trait de caractère plus ou moins fort chez l'individu concerné
	 * (valeurs entre 0 et 1)
	 */
	public enum Genes {
		// propension qu'a l'individu à aller vers le haut
		PROPENSION_HAUT,
		// propension qu'a l'individu à aller vers la droite
		PROPENSION_DROITE,
		// propension qu'a l'individu à aller vers le bas
		PROPENSION_BAS,
		// propension qu'a l'individu à aller vers la gauche
		PROPENSION_GAUCHE,
		// propension qu'a l'individu à chercher à manger ce qu'il peut et qui
		// est proche de lui
		GOURMANDISE,
		// propension qu'a l'individu à fuir ses ennemis
		COUARDISE,
		// propension qu'a l'individu à attaquer les ennemis faibles
		AGRESSIVITE,
		// propension qu'a l'individu à aller dans une direction aléatoire
		STUPIDITE,
		// propension qu'a l'individu à se suicider en foncant vers l'ennemi le
		// plus proche
		MAL_DE_VIVRE,
		// propension qu'a l'individu à faire demi-tour
		INDECISION,
		// distance à partir de laquelle l'individu commence à avoir peur de ses
		// ennemis
		DIST_PEUR,
		// distance à partir de laquelle l'individu commence à avoir envie de
		// manger ce qu'il voit
		DIST_GOURMAND,
		// distance à partir de laquelle l'individu commence à avoir envie
		// d'attaquer les ennemis
		DIST_AGRESSION;
	}

	public class Chromosome implements Comparable<Chromosome> {

		// Variable servant à évaluer la performance de l'individu
		protected float performance;
		// tableau contenant les différents gènes de l'individu
		protected double[] genes = new double[Genes.values().length];

		/**
		 * Constructeur vide d'un individu
		 */
		Chromosome() {
			performance = 0;
		}

		/**
		 * Constructeur par recopie d'un individu
		 * 
		 * @param chromosome
		 */
		public Chromosome(Chromosome chromosome) {
			this();
			for (int i = 0; i < genes.length; i++) {
				genes[i] = chromosome.genes[i];
			}
		}

		/**
		 * Génère des valeurs aléatoires pour chaque gène de l'individu
		 */
		public void remplissageGenesAuHasard() {
			for (int i = 0; i < genes.length; i++) {
				genes[i] = rand.nextDouble();
			}
		}

		/**
		 * Effectue si besoin le croisement entre deux individus
		 * 
		 * @param other
		 */
		public void croisement(Chromosome other) {
			// Le croisement est-il nécessaire ?
			if (rand.nextDouble() > CHANCE_CROISEMENTS)
				return;

			// on décide qui sera la maman et le papa
			Chromosome maman = this;
			Chromosome papa = other;

			/*
			 * Et on choisit deux pivot tels que l'on inversera les gènes
			 * contenus entre ces deux pivots entre la maman et le papa
			 * 
			 * De cette manière en partant de deux gènes de la forme : 
			 * p p p p p p p p
			 * m m m m m m m m
			 * 
			 * on arrivera a deux individus :
			 * p p m m m p p p
			 * m m p p p m m m
			 */
			int pivot1 = rand.nextInt(genes.length);
			int pivot2 = rand.nextInt(genes.length);
			if (pivot1 > pivot2) {
				int swap = pivot1;
				pivot1 = pivot2;
				pivot2 = swap;
			}
			for (int i = pivot1; i < pivot2; i++) {
				double swap = maman.genes[i];
				maman.genes[i] = papa.genes[i];
				papa.genes[i] = swap;
			}
		}

		/**
		 * Effectue la mutation sur chaque gène si nécessaire
		 */
		public void mutation() {
			for (int i = 0; i < genes.length; i++) {
				// Faut-il muter ce gène ?
				if (rand.nextDouble() < CHANCE_MUTATION) {
					// on ajoute une normale centrée en 0 et de largeur totale 1
					// au gène, le resultat est ensuite ramené à un nombre entre
					// 0 et 1
					double rnd = rand.nextGaussian();
					while (rnd < -0.5 || 0.5 < rnd)
						rnd = rand.nextGaussian();
					this.genes[i] += rnd;
					if (this.genes[i] < 0)
						this.genes[i] = 0;
					if (this.genes[i] > 1)
						this.genes[i] = 0;
				}
			}
		}

		/**
		 * Setter de la performance
		 * 
		 * @param performance
		 */
		public void setPerformance(float performance) {
			this.performance = performance;
		}

		/**
		 * @return la performance de l'individu
		 */
		public float getPerformance() {
			return performance;
		}

		@Override
		public int compareTo(Chromosome other) {
			if (this.performance > other.performance)
				return -1;
			if (this.performance == other.performance)
				return 0;
			return 1;
		}
	};

	// Population de chromosomes
	private ArrayList<Chromosome> population;
	// Contrôleur des fantomes utilisé lors des tests
	private RandomGhosts ghostAlgo = new RandomGhosts();
	// Générateur de pseudo hasard
	private static Random rand = new Random();
	// Compteur des générations
	private static int compteurGenerations;

	/**
	 * Constructeur de notre algorithme génétique, prenant en paramètre la
	 * taille de la population et générant aléatoirement les différent
	 * chromosomes de celle-ci
	 * 
	 * @param taille
	 */
	public NotreAlgorithmeGenetique(int taille) {
		population = new ArrayList<Chromosome>();
		for (int i = 0; i < taille; i++) {
			Chromosome entry = new Chromosome();
			entry.remplissageGenesAuHasard();
			population.add(entry);
		}
	}

	/**
	 * Evalue la génération courante, en lançant des tests sur chaque individu
	 * et en récupérant sa performance
	 */
	public void evaluationDeLaGenerationCourante() {
		Exec exec = new Exec();
		for (Chromosome chromosome : population) {
			PacmanControllerApprenant pacmanAlgo = new PacmanControllerApprenant(
					chromosome.genes);
			float score;
			score = exec.runExperiment(pacmanAlgo, ghostAlgo,
					NOMBRE_PARTIE_PAR_CHROMOSOME);
			chromosome.setPerformance(score * (CARRE ? score : 1));
		}
	}

	/**
	 * Produit la nouvelle génération de chromosomes
	 */
	public void produireLaGenerationSuivante() {
		ArrayList<Chromosome> pool = new ArrayList<Chromosome>(population);
		population.clear();
		Collections.sort(pool);

		// Elitisme : on reprend d'office les deux meilleurs dans la nouvelle
		// génération
		population.add(new Chromosome(pool.get(0)));
		population.add(new Chromosome(pool.get(1)));
		// On boucle tant que la population n'est pas remplie de nouveaux gènes
		for (int x = pool.size() - 1; x >= 2; x -= 2) {

			// On sélectionne deux membres de la génération précédente selon le
			// principe de la roulette wheel
			Chromosome n1 = selectMember(pool);
			Chromosome n2 = selectMember(pool);

			// On croise les chromosome et on les mute si nécessaire
			n1.croisement(n2);
			n1.mutation();
			n2.mutation();

			// Enfin, on les ajoute à la nouvelle population
			population.add(new Chromosome(n1));
			population.add(new Chromosome(n2));
		}
	}

	/**
	 * Applique le principe de la roulette wheel pour sélectionner un membre de
	 * la population
	 * 
	 * @param population
	 * @return le chromosome sélectionné
	 */
	private Chromosome selectMember(ArrayList<Chromosome> population) {
		// On calcule la somme des performances des chromosomes de la population
		double total = 0.0;
		for (int x = population.size() - 1; x >= 0; x--) {
			total += population.get(x).performance;
		}

		// On choisit au hasard une valeur entre 0 et la total calculé
		double slice = total * rand.nextDouble();

		// On boucle pour trouver le gène auquel cela correspond
		double totalCourant = 0;
		for (int x = population.size() - 1; x >= 0; x--) {
			Chromosome courant = population.get(x);
			totalCourant += courant.performance;
			if (totalCourant >= slice) {
				return courant;
			}
		}
		// Si ce point est atteint c'est que le dernier chromosome de la
		// population doit être sélectionné
		return population.get(population.size() - 1);
	}

	/**
	 * @return le nombre d'individus de la population
	 */
	public int size() {
		return population.size();
	}

	/**
	 * Renvoie le chromosome à l'indice sélectionné de la population
	 * 
	 * @param index
	 * @return
	 */
	public Chromosome getGene(int indice) {
		return population.get(indice);
	}

	/**
	 * Méthode de test de l'algorithme génétique
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		NotreAlgorithmeGenetique population = new NotreAlgorithmeGenetique(
				TAILLE_POPULATION);
		compteurGenerations = 0;
		// On sauve le temps initial afin de savoir générer des logs uniques
		// lors de chaque exécution
		long time = System.currentTimeMillis();
		String file = "pacman_log_" + time + SERIE + ".txt";
		String evolutionGenes = "evolution_genes" + time + SERIE + ".txt";

		// On boucle pour atteindre les 100 générations
		while (compteurGenerations < 100) {
			// On sauve le temps de début de chaque génération pour savoir le
			// temps nécessaire pour les tests de chaque génération
			long start = System.currentTimeMillis();

			// On évalue la population courante
			population.evaluationDeLaGenerationCourante();

			// On imprime le temps mis pour l'évaluation
			System.out.println((System.currentTimeMillis() - start)
					+ " milli-secondes pour évaluer la génération.");

			// On calcule le scire moyen, minimal et maximal de la
			// génération courante
			float scoreMoyen = 0;
			float scoreMin = Float.POSITIVE_INFINITY;
			float scoreMax = Float.NEGATIVE_INFINITY;
			// On calcule égallement les moyennes des valeurs des gènes pour la
			// génération en cours
			double[] moyenneGenes = new double[Genes.values().length];
			for (int i = 0; i < population.size(); i++) {
				float score = population.getGene(i).getPerformance();
				if (CARRE)
					score = (float) Math.sqrt(score);
				for (int j = 0; j < moyenneGenes.length; j++) {
					moyenneGenes[j] += population.getGene(i).genes[j];
				}
				scoreMoyen += score;
				if (score < scoreMin) {
					scoreMin = score;
				}
				if (score > scoreMax) {
					scoreMax = score;
				}
			}

			if (population.size() > 0) {
				scoreMoyen = scoreMoyen / population.size();
				for (int j = 0; j < moyenneGenes.length; j++) {
					moyenneGenes[j] /= population.size();
				}
			}
			// On génère l'affichage
			String output = "Generation: " + compteurGenerations;
			output += "\t AvgFitness: " + scoreMoyen;
			output += "\t MinFitness: " + scoreMin;
			output += "\t MaxFitness: " + scoreMax;
			String outputEvolutionGenes = "";
			for (int j = 0; j < moyenneGenes.length; j++) {
				outputEvolutionGenes += moyenneGenes[j] + "\t";
			}
			System.out.println(output);
			// On log les valeurs brutes dans les fichiers (en append)
			ecrire(evolutionGenes, outputEvolutionGenes + "\n");
			ecrire(file, scoreMoyen + "\n");

			// On produit la génération suivante
			population.produireLaGenerationSuivante();
			compteurGenerations++;
		}
	}

	/**
	 * Ecris une string dans un fichier
	 * 
	 * @param nomFic
	 * @param texte
	 */
	public static void ecrire(String nomFic, String texte) {
		// On va chercher le chemin et le nom du fichier et on me tout ca dans
		// un String
		String adressedufichier = System.getProperty("user.dir") + "/" + nomFic;
		try {
			/*
			 * BufferedWriter a besoin d un FileWriter, les 2 vont ensemble, on
			 * donne comme argument le nom du fichier true signifie qu on ajoute
			 * dans le fichier (append)
			 */
			FileWriter fw = new FileWriter(adressedufichier, true);

			// On déclare le BufferedWriter output auquel on donne comme
			// argument le FileWriter fw créé ci-dessus
			BufferedWriter output = new BufferedWriter(fw);

			// On écrit dans le fichier ou plutot dans le BufferedWriter qui
			// sert comme un tampon(stream)
			output.write(texte);

			// ensuite flush envoie dans le fichier le contenu du BufferedWriter
			output.flush();

			// et on le ferme
			output.close();
		} catch (IOException ioe) {
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}

	}
};
