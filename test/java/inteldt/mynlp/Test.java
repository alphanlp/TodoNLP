package inteldt.mynlp;

public class Test {

	public static void main(String[] args) {
		/*
		 * 与运算符用符号“&”表示，其使用规律如下：两个操作数中位都为1，结果才为1，否则结果为0，例如下面的程序段。
		 * public class data13
		 * 	{
		 * 	public static void main(String[] args)
		 * 	{
		 * 	int a=129;
		 * 	int b=128;
		 * 	System.out.println("a 和b 与的结果是："+(a&b));
		 * 	}
		 * 	}
		 * 	运行结果
		 * 	a 和b 与的结果是：128
		 * 	下面分析这个程序：
		 * 	“a”的值是129，转换成二进制就是10000001，而“b”的值是128，转换成二进制就是10000000。根据与运算符的运算规律，只有两个位都是1，结果才是1，可以知道结果就是10000000，即128。
		 */
		int index_i = 5 & 1;
		System.out.println(index_i);
	}
}
