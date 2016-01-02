package com.yaoli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.yaoli.util.RandomUtil;

public class KNN {
	/**
	 * 首先是读取excel的数据
	 * 
	 * @throws IOException
	 * 
	 */
	public double[][] readFileToGetMat(String path) throws IOException {
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		HSSFWorkbook wbs = new HSSFWorkbook(is);
		HSSFSheet childSheet = wbs.getSheetAt(0);
		// System.out.println("有行数" + childSheet.getLastRowNum() + 1);
		double[][] mat = new double[childSheet.getLastRowNum() + 1][childSheet
				.getRow(0).getLastCellNum()];
		for (int j = 0; j <= childSheet.getLastRowNum(); j++) {
			HSSFRow row = childSheet.getRow(j);
			if (null != row) {
				for (int k = 0; k <= row.getLastCellNum(); k++) {
					HSSFCell cell = row.getCell(k);
					if (null != cell) {
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							mat[j][k] = (double) cell.getNumericCellValue();
							break;
						}
					}
				}
			}
		}
		return mat;
	}

	/**
	 * 归一化数据
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void guiyihua(double[][] mat, double[][] guiyihuajuzhen)
			throws IOException {
		int colNUM = mat[1].length; // 第一行有多少列
		for (int i = 0; i < colNUM; i++) { // 从第一列开始
			if (i != 0 && i != 1) { // 对于该问题从第三列开始
				for (int j = 0; j < mat.length; j++) { // 从第一行开始
					double temp = guiyihuajuzhen[0][i - 2]
							- guiyihuajuzhen[1][i - 2]; // 最大值-最小值
					double temp1 = mat[j][i] - guiyihuajuzhen[1][i - 2];
					mat[j][i] = temp1 / temp;
				}
			}
		}
	}

	/**
	 * 得到归一化矩阵，用于存取最大值和最小值
	 * 
	 * @param mat
	 * @return
	 * @throws IOException
	 */
	public double[][] getGuiyihuajuzheng(double[][] mat) throws IOException {
		double juzhen[][] = new double[2][mat[0].length - 2];
		int colNUM = mat[1].length; // 第一行有多少列
		// 行数
		// int rowNUM = mat.length;
		for (int i = 0; i < colNUM; i++) { // 从第一列开始
			if (i != 0 && i != 1) { // 对于该问题从第三列开始
				double max = getMaxInCol(mat, i + 1);
				double min = getMinInCol(mat, i + 1);
				juzhen[0][i - 2] = max;
				juzhen[1][i - 2] = min;
			}
		}
		return juzhen;
	}

	/**
	 * 归一化测试数据
	 * 
	 * @param guiyihuajuzhen
	 * @param testData
	 * @throws IOException
	 */
	public void guiyihuTestData(double[][] guiyihuajuzhen, double[] testData)
			throws IOException {
		for (int i = 0; i < testData.length; i++) {
			if (i != 0 && i != 1) { // 对于该问题从第三列开始
				double temp = guiyihuajuzhen[0][i - 2]
						- guiyihuajuzhen[1][i - 2]; // 最大值-最小值
				double temp1 = testData[i] - guiyihuajuzhen[1][i - 2];
				testData[i] = temp1 / temp;// (mat[j][i] - min)/(max - min);
			}
		}
	}

	/**
	 * 注意这里是colNO是列号码
	 * 
	 * @param mat
	 * @param colNO
	 * @return
	 * @throws IOException
	 */
	public double getMaxInCol(double[][] mat, int colNO) throws IOException {
		double max = mat[0][colNO - 1]; // 将第colNO-1列作为最大值
		for (int i = 0; i < mat.length; i++) {
			if (mat[i][colNO - 1] > max) {
				max = mat[i][colNO - 1];
			}
		}
		return max;
	}

	public double getMinInCol(double[][] mat, int colNO) throws IOException {
		double min = mat[0][colNO - 1]; // 将第col-1列作为小值
		for (int i = 0; i < mat.length; i++) {
			if (mat[i][colNO - 1] < min) {
				min = mat[i][colNO - 1];
			}
		}
		return min;
	}

	/**
	 * 计算欧式空间距离 testData测试数据，mat是矩阵，k是选择的数量 即knn 中的k，返回预测的类别
	 * 
	 * @param args
	 * @throws Exception
	 */
	public int classify(double[] testData, double[][] mat, int k)
			throws Exception {
		// 一共有14个特征，
		double[][] tempMat = new double[mat.length][3];

		// 计算与每个样本的距离，将计算出的距离存放在tempMat每行的第三个位置上。
		for (int i = 0; i < mat.length; i++) {// 行号
			double temp = 0.0f; // 临时用于存储 平方和
			double result = 0.0;
			for (int j = 0; j < mat[i].length; j++) {
				if (j != 0 && j != 1) { // 对于该问题从第三列开始
					temp = temp + Math.pow((mat[i][j] - testData[j]), 2);
				}
			}
			result = Math.sqrt(temp);// 开平方 ，得出空间距离
			tempMat[i][0] = mat[i][0];
			tempMat[i][1] = mat[i][1];
			tempMat[i][2] = result;
		}

		// 排序得出 距离最短的前k个数据，从小到大排列
		// 将第一行的第三个作为临时最小值，采用冒泡排序
		for (int i = 0; i < tempMat.length - 1; i++) {
			for (int j = 0; j < tempMat.length - i - 1; j++) {
				if (tempMat[j][2] > tempMat[j + 1][2]) {
					// 交换
					double temp1 = tempMat[j][0];
					double temp2 = tempMat[j][1];
					double temp3 = tempMat[j][2];

					tempMat[j][0] = tempMat[j + 1][0];
					tempMat[j][1] = tempMat[j + 1][1];
					tempMat[j][2] = tempMat[j + 1][2];

					tempMat[j + 1][0] = temp1;
					tempMat[j + 1][1] = temp2;
					tempMat[j + 1][2] = temp3;
				}
			}
		}

		// 显示矩阵
		for (int i = 0; i < tempMat.length; i++) {
			for (int j = 0; j < tempMat[i].length; j++) {
				// System.out.print(tempMat[i][j] + "\t");
			}
			// System.out.println();
		}

		// 选出前k个值。
		int[] labels = new int[k];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = (int) tempMat[i][0];
		}

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < labels.length; i++) {
			if (!map.containsKey(labels[i])) {
				map.put(labels[i], 1);
			} else {
				int temp = map.get(labels[i]);
				temp = temp + 1;
				map.put(labels[i], temp);
			}
		}

		int maxKey = Integer.MIN_VALUE;
		int maxValue = Integer.MIN_VALUE;
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {

			if (entry.getValue() > maxValue) {
				maxKey = entry.getKey();
				maxValue = entry.getValue();
			}

		}

		// System.out.println(maxKey + " " + maxValue);
		return maxKey;
	}

	/**
	 * 将矩阵分成两个矩阵，mat的样本数据，testmat是预测数据
	 * 
	 * @param mat
	 * @param testMat
	 */
	public double[][] splitMat(double mat[][]) { // 先确定预测矩阵的行数与列数
		int forcastNUM = (int) Math.round((mat.length * 0.7));
		double[][] testData = new double[forcastNUM][mat[0].length];

		// 统计每个label出现的次数
		Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
		for (int i = 0; i < mat.length; i++) {
			if (map.containsKey((int) mat[i][0])) {
				int temp = map.get((int) mat[i][0]);
				temp = temp + 1;
				map.put((int) mat[i][0], temp);
			} else {
				map.put((int) mat[i][0], 1);
			}
		}
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			// System.out.println(entry.getKey() + " " + entry.getValue());
		}

		int matPoint = 0;
		int testDataPoint = 0;

		RandomUtil randomUtil = new RandomUtil();

		// 一维矩阵用于置零
		double[] tempZero = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			// System.out.println(entry.getKey() + " " + entry.getValue());
			int labelName = entry.getKey();// 标签名字
			int labelNum = entry.getValue(); // 标签的数量

			int selectedNum = (int) Math.round(labelNum * 0.3);
			int[] a = randomUtil.getTruncatedZeroToNRandom(labelNum,
					selectedNum);

			for (int i = 0; i < a.length; i++) {
				testData[testDataPoint + i] = mat[matPoint + a[i]];
				// 用于置零
				mat[matPoint + a[i]] = tempZero;
			}

			// 设置成下一个point点
			matPoint = matPoint + labelNum;
			testDataPoint = testDataPoint + selectedNum;
		}

		// 用于去除 0.0行
		for (int i = 0; i < mat.length - 1; i++) {
			for (int j = 0; j < mat.length - i - 1; j++) {
				if (mat[j][0] == 0.0) {
					// 交换
					double[] temp1 = mat[j];
					mat[j] = mat[j + 1];
					mat[j + 1] = temp1;
				}
			}
		}

		// 返回测试矩阵
		return testData;
	}

	/**
	 * 将矩阵写到xls文件中
	 * 
	 * @param mat
	 * @param testData
	 * @throws IOException
	 */
	public void writeMatrixToXlx(double[][] mat, String filepath)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(filepath);// d:\\test01.xls
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet();
		wb.setSheetName(0, "first sheet");
		for (int i = 0; i < mat.length; i++) {
			if (mat[i][0] != 0.0) {
				HSSFRow row = s.createRow(i);
				for (int j = 0; j < mat[i].length; j++) {
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(mat[i][j]);
				}
			}
		}
		wb.write(fos);
		fos.close();
	}
	
	/**
	 * 将混合矩阵写到xls文件中
	 * 
	 * @param mat
	 * @param testData
	 * @throws IOException
	 */
	public void writeConfusionMatrixToXlx(int[][] mat, String filepath)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(filepath);// d:\\test01.xls
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet();
		wb.setSheetName(0, "first sheet");
		for (int i = 0; i < mat.length; i++) {
			HSSFRow row = s.createRow(i);
			for (int j = 0; j < mat[i].length; j++) {
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(mat[i][j]);
			}
		}
		wb.write(fos);
		fos.close();
	}
	
	public static void main(String[] args) throws Exception {
		KNN knn = new KNN();

//		// 调用函数获取矩阵
//		String path = "C:\\Users\\will\\Desktop\\叶片图库\\leaf\\leaf.xls";
//		// C:\\Users\\will\\Desktop\\叶片图库\\leaf\\leaf.xls
//
//		for (int i = 0; i < 10; i++) {
//			double[][] mat = knn.readFileToGetMat(path);
//			double[][] testData = knn.splitMat(mat);
//			knn.writeMatrixToXlx(mat, "d:\\matrix\\sample0" + i + ".xls");
//			knn.writeMatrixToXlx(testData, "d:\\matrix\\test0" + i + ".xls");
//		}

		// D:\\matrix\\test00.xls
		// d:\\matrix\\sample00.xls
		// C:\\Users\\will\\Desktop\\test\\01\\leafSample.xls
		// C:\\Users\\will\\Desktop\\test\\01\\leafForecast.xls

		double sumCorrect = 0.0;
		
		int [][] confusionMatirx =new int [30][30];

		for (int k = 0; k < 10; k++) {
			String path1 = "d:\\matrix\\sample0" + k + ".xls";
			double[][] mat = knn.readFileToGetMat(path1);

			// 获取预测数据
			String path2 = "D:\\matrix\\test0" + k + ".xls";
			double[][] testData = knn.readFileToGetMat(path2);

			// 得到用来保存每一列的最大值和最小值的矩阵
			double[][] guiyihuajuzhen = knn.getGuiyihuajuzheng(mat);

			// 归一化测试矩阵
			knn.guiyihua(mat, guiyihuajuzhen);

			// 用来保存正确的结果
			int result = 0;

			// 遍历测试矩阵
			for (int i = 0; i < testData.length; i++) {

				// 将测试矩阵归一化
				knn.guiyihuTestData(guiyihuajuzhen, testData[i]);

				// 得出测试的结果 
				// testData[i][0]表是的正确的标签，即行号
				int forcast = knn.classify(testData[i], mat, 5);
				
				//用于保存结果
				int forcast2 = knn.classify(testData[i], mat, 5);
				
				
				/*以下产生混肴矩阵*/
				//标签号在15以下
				int temp = (int)testData[i][0];
				int row = 0; //confusionMatrix的行号
				int col = 0; //confusionMatrix的列号
				if(temp <= 15){
					row = temp - 1;
				}else {
					row = temp - 7;
				}
				if(forcast <= 15){
					col = forcast - 1;
				}else {
					col = forcast - 7;
				}
				//int temp2 = confusionMatirx[row][col];
				//有的话就加一
				confusionMatirx[row][col] = confusionMatirx[row][col] + 1;
				
				
				/*以下是混肴矩阵产生结束*/
				// 如果预测正确，result+1，这里testData[i][0] 表示的第i行的地0个，即标签列
				if (forcast2 == testData[i][0]) {
					result = result + 1;
				}
			}
			
			//将混合矩阵写到matrix
			knn.writeConfusionMatrixToXlx(confusionMatirx, "d:\\matrix\\confusionMatrix0"+k+".xls");
			
			//打印混合矩阵
			for (int i = 0; i < confusionMatirx.length; i++) {
				for (int j = 0; j < confusionMatirx[i].length; j++) {
					//System.out.print(confusionMatirx[i][j]+"\t");
				}
				//System.out.println();
			}
			
			//根据混合矩阵打印出正确识别的数量
			for (int i = 0; i < confusionMatirx.length; i++) {
				if(i < 15){
					System.out.println("类别"+(i+1)+"正确识别的数量是："+confusionMatirx[i][i]);
				}else {
					System.out.println("类别"+(i+7)+"正确识别的数量是："+confusionMatirx[i][i]);
				}
			}
			
			confusionMatirx =new int [30][30];

			// 得出正确率
			double correct = ((double) result) / ((double) testData.length);

			System.out.println("第" + (k + 1) + "组的正确率是：" + correct);
			
			sumCorrect = sumCorrect + correct;
		}

		System.out.println("10组的平均正确率是：" + sumCorrect / 10);

		//注意：一共有30个类别，使用30*30的矩阵
	}

}
