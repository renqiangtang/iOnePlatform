package com.tradeland.engine;

import java.util.List;
import java.util.Random;

public class RandomUtils {

	/**
	 * 摇号函数
	 * @param ids
	 * @return
	 */
	public static String hit(List<String> ids) {
		int size = ids.size();
		Random random = new Random();
		int hitNum = random.nextInt(size);
		return ids.get(hitNum);
	}

	public static void main(String[] args) {
		Random random = new Random();
		for (int i = 0; i < 100; i++)
			System.out.println(random.nextInt(3));
	}

}
