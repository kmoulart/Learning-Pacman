package utils;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class testgauss {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rand = new Random();
		SortedMap<Double, Integer> map = new TreeMap<Double, Integer>();
		for (int i = 0; i < 100000; i++) {
			double rnd = rand.nextGaussian();
			double random = ((int) rnd * 1000) / 1000.0;
			if (!map.containsKey(random))
				map.put(random, 0);
			map.put(random, map.get(random) + 1);
		}
		for (Double d : map.keySet()) {
			System.out.print(d + "\t");
			for (int i = 0; i < map.get(d); i += 25)
				System.out.print("|");
			System.out.println();
		}
	}

}
