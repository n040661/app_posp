package xdt.util;

public class testClockwiseOutput { // 顺时针打印一个矩阵
	/*
	 * @org.junit.Test public void test() { int[][] num = new int[100][100]; int n =
	 * 4; int count = 1; for (int i = 0; i < n; i++) { for (int j = 0; j < n; j++) {
	 * num[i][j] = count++; } }
	 * 
	 * output(num, 0, n - 1); }
	 * 
	 * public void output(int[][] num, int start, int end) { if (start >= end || end
	 * <= 0) return; for (int i = start; i <= end; i++) {
	 * System.out.println(num[start][i]); } for (int i = start + 1; i <= end; i++) {
	 * System.out.println(num[i][end]); } for (int i = end - 1; i >= start; i--) {
	 * System.out.println(num[end][i]); } for (int i = end - 1; i > start; i--) {
	 * System.out.println(num[i][start]); } output(num, start + 1, end - 1); }
	 */
//-----------------------------------------------------------
	// 给出一个排序好的数组和一个数，求数组中连续元素的和等于所给数的子数组
	@org.junit.Test
	public void test() {
		int[] num = { 1, 2, 2, 3, 4, 5, 6, 7, 8, 9 };
		int sum = 7;
		findSum(num, sum);
	}

	public void findSum(int[] num, int sum) {
		int left = 0;
		int right = 0;
		for (int i = 0; i < num.length; i++) {
			int curSum = 0;
			left = i;
			right = i;
			while (curSum < sum) {
				curSum += num[right++];
			}
			if (curSum == sum) {
				for (int j = left; j < right; j++) {
					System.out.print(num[j] + " ");
				}
				System.out.println();
			}
		}
	}
}