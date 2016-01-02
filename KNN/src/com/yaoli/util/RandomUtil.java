package com.yaoli.util;

import java.util.Random;
import org.apache.log4j.Logger;

/**
 * �������ʹ����
 * @author yaoli
 *
 */
public class RandomUtil {
	
	private static Logger logger = Logger.getLogger(RandomUtil.class);

	/**
	 * �������һ��[0,n)���������
	 * @param n ������һ��������
	 */
	public int[] getZeroToN(int n) {
		return getRandomBySpecifiedNum(0, n);
	}

	/**
	 * �������һ��[n,m)���������
	 * ע���������Ϊ��������m�����n��m���ܺ�n���
	 * @param n ������һ��������
	 * @param m ������һ��������
	 * @return ���ز��ظ����������
	 */
	public int[] getNToMRandomNum(int n, int m) {
		return getRandomBySpecifiedNum(n, m);
	}
	
	/**
	 * �������һ��[0,n)���������
	 * @param n ��һ��������
	 * @param k ��ȡ�ĸ���
	 * @return ���س���Ϊk�����飬��[0,n)��Χ�е��������
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
	 * �������һ��[n,m)��������飬���紫��7,10������[7-10)���������
	 * ע���������Ϊ��������m�����n��m���ܺ�n���
	 * @param n ��һ��������
	 * @param m ��һ��������
     * @param k ��ȡ�ĳ���
	 * @return ���س���Ϊk�����飬��[n,m)��Χ�е��������
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
			throw new IllegalArgumentException("���Ϸ��Ĳ���");
		}

		int a[] = new int[m - n];

		for (int i = 0; i < m - n; i++) {
			a[i] = i + n;
		}

		// ָ�����һ����
		int point = a.length - 1;

		Random random = new Random();
		for (int i = a.length - 1; i > 0; --i, --point) {
			int randomNUM = random.nextInt(i);// [0,n+1) �� [n,n]�������
			// ��������
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
