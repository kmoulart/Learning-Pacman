package algorithme_apprentissage;

import game.Exec;
import game.controllers.examples.LearningController;
import game.controllers.examples.NearestPillPacManVS;
import game.core.Game.DM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

import utils.Util;

/**
 * Genetic Algorithm sample class <br/>
 * <b>The goal of this GA sample is to maximize the number of capital letters in
 * a String</b> <br/>
 * compile using "javac GeneticAlgorithm.java" <br/>
 * test using "java GeneticAlgorithm" <br/>
 * 
 * @author A.Liapis
 */

public class OurGeneticAlgorithm {
	// --- constants
	static int POPULATION_SIZE = 100;
	private static double mutationChance = 0; // en pourcent
	private static double crossRate = 0;

	// public static final int PROPENSION_HAUT = 0;
	// public static final int PROPENSION_DROITE = PROPENSION_HAUT + 1;
	// public static final int PROPENSION_BAS = PROPENSION_DROITE + 1;
	// public static final int PROPENSION_GAUCHE = PROPENSION_BAS + 1;
	public static final int PROPENSION_ATTAQUE = 0;// PROPENSION_GAUCHE + 1;
	public static final int PROPENSION_FUITE = PROPENSION_ATTAQUE + 1;
	public static final int PROPENSION_SUICIDE = PROPENSION_FUITE + 1;
	public static final int PROPENSION_SURVIE = PROPENSION_SUICIDE + 1;
	public static final int MEASURE = PROPENSION_SURVIE + 1;
	// public static final int PROPENSION_RANDOM = PROPENSION_SURVIE + 1;
	// public static final int DISTANCE_ATTAQUE = PROPENSION_RANDOM + 1;
	// public static final int DISTANCE_FUITE = DISTANCE_ATTAQUE + 1;
	// public static final int ZONE_PREFEREE_HAUT_BAS = DISTANCE_FUITE + 1;
	// public static final int ZONE_PREFEREE_GAUCHE_DROITE =
	// ZONE_PREFEREE_HAUT_BAS + 1;
	// public static final int MEASURE = ZONE_PREFEREE_GAUCHE_DROITE + 1;

	public class Chromosome implements Comparable {
		// --- variables:

		/**
		 * Fitness evaluates to how "close" the current gene is to the optimal
		 * solution (i.e. contains only 1s in its chromosome) A gene with higher
		 * fitness value from another signifies that it has more 1s in its
		 * chromosome, and is thus a better solution While it is common that
		 * fitness is a floating point between 0..1 this is not necessary: the
		 * only constraint is that a better solution must have a strictly higher
		 * fitness than a worse solution
		 */
		protected float mFitness;
		protected double[] genes = new double[MEASURE + 1];

		// --- functions:
		/**
		 * Allocates memory for the mChromosome array and initializes any other
		 * data, such as fitness We chose to use a constant variable as the
		 * chromosome size, but it can also be passed as a variable in the
		 * constructor
		 */
		Chromosome() {
			// initializing fitness
			mFitness = 0.f;
		}

		/**
		 * Randomizes the numbers on the mChromosome array to values 0 or 1
		 */
		public void randomizeChromosome() {
			for (int i = 0; i < genes.length; i++) {
				genes[i] = rand.nextDouble();
			}
		}

		/**
		 * Creates a number of offspring by combining (using crossover) the
		 * current Gene's chromosome with another Gene's chromosome. Usually two
		 * parents will produce an equal amount of offpsring, although in other
		 * reproduction strategies the number of offspring produced depends on
		 * the fitness of the parents.
		 * 
		 * @param other
		 *            : the other parent we want to create offpsring from
		 * @return Array of Gene offspring (default length of array is 2). These
		 *         offspring will need to be added to the next generation.
		 */
		public void crossover(Chromosome other) {
			// Should we cross over?
			if (rand.nextDouble() > crossRate)
				return;

			Chromosome maman = this;
			Chromosome papa = other;
			if (rand.nextBoolean()) {
				maman = other;
				papa = this;
			}

			int pivot = rand.nextInt(genes.length);
			System.out.println("PIVOT\t" + pivot);
			for (int i = pivot; i < genes.length; i++) {
				double tmp = maman.genes[i];
				maman.genes[i] = papa.genes[i];
				papa.genes[i] = tmp;
			}
		}

		/**
		 * Mutates a gene using inversion, random mutation or other methods.
		 * This function is called after the mutation chance is rolled. Mutation
		 * can occur (depending on the designer's wishes) to a parent before
		 * reproduction takes place, an offspring at the time it is created, or
		 * (more often) on a gene which will not produce any offspring
		 * afterwards.
		 */
		public void mutate() {
			for (int i = 0; i < genes.length; i++) {
				if (rand.nextDouble() < mutationChance)
					this.genes[i] = rand.nextDouble();
			}
		}

		/**
		 * Sets the fitness, after it is evaluated in the GeneticAlgorithm
		 * class.
		 * 
		 * @param value
		 *            : the fitness value to be set
		 */
		public void setFitness(float value) {
			mFitness = value;
		}

		/**
		 * @return the gene's fitness value
		 */
		public float getFitness() {
			return mFitness;
		}

		public int compareTo(Object o) {
			Chromosome g = (Chromosome) o;
			if (this.mFitness > g.mFitness)
				return -1;
			if (this.mFitness == g.mFitness)
				return 0;
			return 1;
		}
	};

	// --- variables:

	/**
	 * The population contains an ArrayList of genes (the choice of arrayList
	 * over a simple array is due to extra functionalities of the arrayList,
	 * such as sorting)
	 */
	ArrayList<Chromosome> mPopulation;

	// --- functions:

	/**
	 * Creates the starting population of Gene classes, whose chromosome
	 * contents are random
	 * 
	 * @param size
	 *            : The size of the popultion is passed as an argument from the
	 *            main class
	 */
	public OurGeneticAlgorithm(int size) {
		// initialize the arraylist and each gene's initial weights HERE
		mPopulation = new ArrayList<Chromosome>();
		for (int i = 0; i < size; i++) {
			Chromosome entry = new Chromosome();
			entry.randomizeChromosome();
			mPopulation.add(entry);
		}
	}

	/**
	 * For all members of the population, runs a heuristic that evaluates their
	 * fitness based on their phenotype. The evaluation of this problem's
	 * phenotype is fairly simple, and can be done in a straightforward manner.
	 * In other cases, such as agent behavior, the phenotype may need to be used
	 * in a full simulation before getting evaluated (e.g based on its
	 * performance)
	 */
	NearestPillPacManVS p = new NearestPillPacManVS();

	public void evaluateGeneration() {
		Exec exec = new Exec();
		for (int i = 0; i < mPopulation.size(); i++) {
			// evaluation of the fitness function for each gene in the
			// population goes HERE
			// System.out.println("Gene " + i);
			Chromosome g = mPopulation.get(i);
			LearningController l = new LearningController(g.genes);
			// long time_ago = new GregorianCalendar().getTimeInMillis();
			float score = exec.runExperiment(p, l, 3);
			g.setFitness(1000000000 / (score * score));
			// System.out.println(new GregorianCalendar().getTimeInMillis() -
			// time_ago);
		}
	}

	/**
	 * With each gene's fitness as a guide, chooses which genes should mate and
	 * produce offspring. The offspring are added to the population, replacing
	 * the previous generation's Genes either partially or completely. The
	 * population size, however, should always remain the same. If you want to
	 * use mutation, this function is where any mutation chances are rolled and
	 * mutation takes place.
	 */
	@SuppressWarnings("unchecked")
	public void produceNextGeneration() {
		// use one of the offspring techniques suggested in class (also applying
		// any mutations) HERE
		ArrayList<Chromosome> pool = new ArrayList<Chromosome>(mPopulation);
		Collections.sort(pool);
		for (int i = 0; i < pool.size(); i++) {
			System.out.println("Fit : " + pool.get(i).mFitness);
//			System.out.println("SUICIDE : " + pool.get(i).genes[PROPENSION_SUICIDE] + " SURVIE : " + pool.get(i).genes[PROPENSION_SURVIE]);
//			System.out.println("FUITE : " + pool.get(i).genes[PROPENSION_FUITE] + " ATTAQUE : " + pool.get(i).genes[PROPENSION_ATTAQUE]);
		}
		mPopulation.clear();
		// ellitisme : on reprend d'office les deux meilleurs
		mPopulation.add(pool.get(0));
		mPopulation.add(pool.get(1));
		// Loop until the pool has been processed
		for (int x = pool.size() - 1; x >= 2; x -= 2) {
			// Select two members
			
			Chromosome n1 = selectMember(pool);
			Chromosome n2 = selectMember(pool);
//			System.out.println("------------AVANT-----------");
//			System.out.println("Fit : " + n1.mFitness);
//			System.out.println("SUICIDE : " + n1.genes[PROPENSION_SUICIDE] + " SURVIE : " + n1.genes[PROPENSION_SURVIE]);
//			System.out.println("FUITE : " + n1.genes[PROPENSION_FUITE] + " ATTAQUE : " + n1.genes[PROPENSION_ATTAQUE]);
//			System.out.println("Fit : " + n2.mFitness);
//			System.out.println("SUICIDE : " + n2.genes[PROPENSION_SUICIDE] + " SURVIE : " + n2.genes[PROPENSION_SURVIE]);
//			System.out.println("FUITE : " + n2.genes[PROPENSION_FUITE] + " ATTAQUE : " + n2.genes[PROPENSION_ATTAQUE]);

			// Cross over and mutate
			n1.crossover(n2);
			n1.mutate();
			n2.mutate();

//			System.out.println("------------APRES-----------");
//			System.out.println("Fit : " + n1.mFitness);
//			System.out.println("SUICIDE : " + n1.genes[PROPENSION_SUICIDE] + " SURVIE : " + n1.genes[PROPENSION_SURVIE]);
//			System.out.println("FUITE : " + n1.genes[PROPENSION_FUITE] + " ATTAQUE : " + n1.genes[PROPENSION_ATTAQUE]);
//			System.out.println("Fit : " + n2.mFitness);
//			System.out.println("SUICIDE : " + n2.genes[PROPENSION_SUICIDE] + " SURVIE : " + n2.genes[PROPENSION_SURVIE]);
//			System.out.println("FUITE : " + n2.genes[PROPENSION_FUITE] + " ATTAQUE : " + n2.genes[PROPENSION_ATTAQUE]);

			// Add to the actual pool
			mPopulation.add(n1);
			mPopulation.add(n2);
		}

		//
		// int nombreNouveauxGenes = 0;
		// int index = 0;
		// while (nombreNouveauxGenes < size()) {
		// for (int i = 1; i < 10 && (index + i) < pop.size()
		// && nombreNouveauxGenes < size(); i++) {
		// Gene[] childs = pop.get(index).reproduce(pop.get(index + i));
		// for (int j = 0; j < childs.length; j++) {
		// mPopulation.set(nombreNouveauxGenes, childs[j]);
		// nombreNouveauxGenes++;
		// }
		// }
		// index++;
		// }

	}

	private static Random rand = new Random();

	private Chromosome selectMember(ArrayList<Chromosome> pop) {
		// Get the total fitness
		double tot = 0.0;
		for (int x = pop.size() - 1; x >= 0; x--) {
			double score = pop.get(x).mFitness;
			tot += score;
		}
		double slice = tot * rand.nextDouble();

		// Loop to find the node
		double ttot = 0.0;
		for (int x = pop.size() - 1; x >= 0; x--) {
			Chromosome node = pop.get(x);
			ttot += node.mFitness;
			if (ttot >= slice) {
				pop.get(x);
				return node;
			}
		}

		return pop.get(pop.size() - 1);
	}

	// accessors
	/**
	 * @return the size of the population
	 */
	public int size() {
		return mPopulation.size();
	}

	/**
	 * Returns the Gene at position <b>index</b> of the mPopulation arrayList
	 * 
	 * @param index
	 *            : the position in the population of the Gene we want to
	 *            retrieve
	 * @return the Gene at position <b>index</b> of the mPopulation arrayList
	 */
	public Chromosome getGene(int index) {
		return mPopulation.get(index);
	}

	// Genetic Algorithm maxA testing method
	public static void main(String[] args) {
		// Initializing the population (we chose 500 genes for the population,
		// but you can play with the population size to try different
		// approaches)
		OurGeneticAlgorithm population = new OurGeneticAlgorithm(
				POPULATION_SIZE);
		int generationCount = 0;
		// For the sake of this sample, evolution goes on forever.
		// If you wish the evolution to halt (for instance, after a number of
		// generations is reached or the maximum fitness has been achieved),
		// this is the place to make any such checks
		while (generationCount < 1000) {
			// --- evaluate current generation:
			long start = System.currentTimeMillis();
			population.evaluateGeneration();
			System.out.println(System.currentTimeMillis() - start);
			// --- print results here:
			// we choose to print the average fitness,
			// as well as the maximum and minimum fitness
			// as part of our progress monitoring
			float avgFitness = 0.f;
			float minFitness = Float.POSITIVE_INFINITY;
			float maxFitness = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < population.size(); i++) {
				float currFitness = population.getGene(i).getFitness();
				avgFitness += currFitness;
				if (currFitness < minFitness) {
					minFitness = currFitness;
				}
				if (currFitness > maxFitness) {
					maxFitness = currFitness;
				}
			}
			if (population.size() > 0) {
				avgFitness = avgFitness / population.size();
			}
			String output = "Generation: " + generationCount;
			output += "\t AvgFitness: " + avgFitness;
			output += "\t MinFitness: " + minFitness;
			output += "\t MaxFitness: " + maxFitness;
			System.out.println(output);
			// produce next generation:
			population.produceNextGeneration();
			generationCount++;
		}
	}
};
