package utils;

public class Util {
	/**
	 * Méthode qui renvoie un entier au hasard entre la valeur minimale et
	 * la valeur maximale
	 * 
	 * @param valeurMinimale
	 *            Valeur minimale pour le random
	 * @param valeurMaximale
	 *            Valeur maximale pour le random
	 * 
	 * @return l'entier choisis
	 */
	public static int rand(int valeurMinimale, int valeurMaximale) {
		return (int) (Math.random() * (valeurMaximale - valeurMinimale + 1))
				+ valeurMinimale;
	}
	
	public static double moyenne(double... val) {
		double somme = 0;
		for (double valeur : val)
			somme += valeur;
		return somme / val.length;
	}
}
