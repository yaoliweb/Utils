package com.yaoli.util;

import java.util.Random;
import org.apache.log4j.Logger;

/**
 * 随机数的使用类
 * @author yaoli
 *
 */
public class RandomUtil {
	
	private static Logger logger = Logger.getLogger(RandomUtil.class);

	/**
	 * 随机产生一个[0,n)的随机数组
	 * @param n 必须是一个正整数
	 */
	public int[] getZeroToN(int n) {
		return getRandomBySpecifiedNum(0, n);
	}

	/**
	 * 随机产生一个[n,m)的随机数组
	 * 注意参数必须为正整数，m必须比n大，m不能和n相等
	 * @param n 必须是一个正整数
	 * @param m 必须是一个正整数
	 * @return 返回不重复的随机数组
	 */
	public int[] getNToMRandomNum(int n, int m) {
		return getRandomBySpecifiedNum(n, m);
	}
	
	/**
	 * 随机产生一个[0,n)的随机数组
	 * @param n 是一个正整数
	 * @param k 截取的个数
	 * @return 返回长度为k的数组，在[0,n)范围中的随机数组
	 */
	public int[] getTruncatedZeroToNRandom(int n,int k){
		int a [] = getZeroToN(n);
		int b [] = new int[k];
		for (int i = 0; i < k; i++) {
			b[i] = a[i];
		}
		if(logger.isDebugEnabled()){
			for (int i = 0; i < b.length; i++) {
				logger.debug(b[i]+" ");
			}
		}
		return b;
	}
	
	/**
	 * 随机产生一个[n,m)的随机数组，比如传入7,10，返回[7-10)的随机数组
	 * 注意参数必须为正整数，m必须比n大，m不能和n相等
	 * @param n 是一个正整数
	 * @param m 是一个正整数
     * @param k 截取的长度
	 * @return 返回长度为k的数组，在[n,m)范围中的随机数组
	 */
	public int[] getTruncatedNToMRandomNum(int n,int m,int k){
		int a [] = getNToMRandomNum(n,m);
		int b [] = new int[k];
		for (int i = 0; i < k; i++) {
			b[i] = a[i];
		}
		
		if(logger.isDebugEnabled()){
			for (int i = 0; i < b.length; i++) {
				logger.debug(b[i]+" ");
			}
		}
		return b;
	}

	private int[] getRandomBySpecifiedNum(int n, int m) {
		if (n < 0 || m < 0 || (m - n) < 0 || m == n) {
			throw new IllegalArgumentException("不合法的参数");
		}

		int a[] = new int[m - n];

		for (int i = 0; i < m - n; i++) {
			a[i] = i + n;
		}

		// 指向最后一个数
		int point = a.length - 1;

		Random random = new Random();
		for (int i = a.length - 1; i > 0; --i, --point) {
			int randomNUM = random.nextInt(i);// [0,n+1) 即 [n,n]的随机数
			// 交换数字
			int temp = a[randomNUM];
			a[randomNUM] = a[point];
			a[point] = temp;
		}

		return a;
	}
	
	public static void main(String[] args) {
		RandomUtil randomUtil= new RandomUtil();
		randomUtil.getTruncatedNToMRandomNum(0, 200000000, 4);
	}
}
