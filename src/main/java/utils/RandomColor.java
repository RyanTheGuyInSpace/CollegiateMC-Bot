package utils;

import java.awt.*;
import java.util.Random;

public class RandomColor {

	private static Random rand = new Random();

	public static Color getRandomColor() {

		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		Color color = new Color(r, g, b);

		return color;
	}
}
