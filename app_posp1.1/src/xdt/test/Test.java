package xdt.test;

public class Test {
	
	/**
     * 将一个数组以某一元素分界，将这个元素之前的部分与之后的部分互换位置
     * 主要思路:  将之前的部分与之后的部分分别逆序，再将整体逆序即可
     * @param str 要进行操作的字符串
     * @param i   作为分界线的元素的下标
     * @return    转化之后生成的字符串
     */
    public static String replaceBeginAndEnd(String str, int i) {
        if (str == null || i < 0 || str.length() <= i) {
            return str;
        }
        char[] chars = str.toCharArray();
        reverseCharArray(chars, 0, i - 1);
        reverseCharArray(chars, i + 1, chars.length - 1);
        reverseCharArray(chars, 0, chars.length - 1);
        return new String(chars);
    }

    /**
     * 用来翻转一个数组的某一部分
     * @param charArray  要进行操作的数组
     * @param begin      要翻转的部分第一个元素的下标
     * @param end        要翻转的部分最后一个元素的下标
     */
    public static void reverseCharArray(char[] charArray, int begin, int end) {
        char tmp;
        while (begin < end) {
            tmp = charArray[begin];
            charArray[begin] = charArray[end];
            charArray[end] = tmp;
            begin++;
            end--;
        }
    }

    public static void main(String[] args) {
        String str = "0423";
        System.out.println(replaceBeginAndEnd(str,0));
    }

}
