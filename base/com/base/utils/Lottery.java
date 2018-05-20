package com.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lottery {

	private List<String> ids = new ArrayList<String>();

	/**
	 * 清除摇号池
	 */
	public void clean() {
		ids = new ArrayList<String>();
	}

	/**
	 * 添加参加摇号的Id
	 * 
	 * @param id
	 */
	public void add(String id) {
		ids.add(id);
	}

	/**
	 * 摇号函数
	 * 
	 * @param ids
	 * @return
	 */
	public String hit() {
		int size = ids.size();
		Random random = new Random();
		int hitNum = random.nextInt(size);
		return ids.get(hitNum);
	}

	/**
	 * 即时摇号
	 * @param list
	 * @return
	 */
	public String hit(List<String> list) {
		ids = list;
		return hit();
	}
	
	/**
	 * 即时摇号 返回1..n中的一个数字
	 * @param list
	 * @return
	 */
	public  int hit(int n){
		Random random = new Random();
		int hitNum = random.nextInt(n);
		return hitNum;
	}

	public static void main(String[] args) throws Exception {
		Lottery u = new Lottery();
		u.add("01");
		u.add("02");
		//u.add("03");
		//u.add("04");
		for (int i = 0; i < 10; i++) {
			System.out.println(u.hit());
			Thread.currentThread().sleep(200);
		}

	}
}
