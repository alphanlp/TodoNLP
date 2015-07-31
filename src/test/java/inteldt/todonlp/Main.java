package inteldt.todonlp;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		// System.out.println(ed("sailn","failing"));
		System.out.println(ed("recoginze", "recognize"));
		// System.out.println(ed("hack", "hankcs"));
	}

	public static int ed(String wrongWord, String rightWord) {
		final int m = wrongWord.length();
		final int n = rightWord.length();

		int[][] d = new int[m + 1][n + 1];
		for (int j = 0; j <= n; ++j) {
			d[0][j] = j;
		}
		for (int i = 0; i <= m; ++i) {
			d[i][0] = i;
		}

		for (int[] l : d) {
			System.out.println(Arrays.toString(l));
		}

		for (int i = 1; i <= m; ++i) {
			char ci = wrongWord.charAt(i - 1);
			for (int j = 1; j <= n; ++j) {
				char cj = rightWord.charAt(j - 1);
				if (ci == cj) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					// 等号右边的分别代表 将ci改成cj 错串加cj 错串删ci
					d[i][j] = Math.min(d[i - 1][j - 1] + 1,
							Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
				}
			}
		}

		System.out.println();
		for (int[] l : d) {
			System.out.println(Arrays.toString(l));
		}

		return d[m][n];
	}
}
