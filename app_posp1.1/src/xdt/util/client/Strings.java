/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xdt.util.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
*
* @author: XieminQuan
* @time  : 2007-11-20 下午04:10:22
*
* DNAPAY
*/

public class Strings {

    public boolean isInteger(String str) {
        int begin = 0;
        if (str == null || str.trim().equals("")) {
            return false;
        }
        str = str.trim();
        if (str.startsWith("+") || str.startsWith("-")) {
            if (str.length() == 1) {
                // "+" "-"
                return false;
            }
            begin = 1;
        }
        for (int i = begin; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static boolean isNullOrEmpty(Object str) {
        return str == null || str.toString().equals("");
    }

    public static String getStackTrace(Throwable e) {
        StringBuffer stack = new StringBuffer();
        stack.append(e);
        stack.append("\r\n");

        Throwable rootCause = e.getCause();

        while (rootCause != null) {
            stack.append("Root Cause:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            stack.append(rootCause.getMessage());
            stack.append("\r\n");
            stack.append("StackTrace:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            rootCause = rootCause.getCause();
        }


        for (int i = 0; i < e.getStackTrace().length; i++) {
            stack.append(e.getStackTrace()[i].toString());
            stack.append("\r\n");
        }
        return stack.toString();
    }

    public static String toString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString().trim();
    }

    public static String format(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    public static String format(String str, int beginSize, int endSize, String leftFill, String rightFill, boolean cutLeft) throws Exception {

        while (beginSize > 0) {
            str = leftFill + str;
            beginSize--;
        }
        if (str.getBytes("gbk").length > endSize) {

            byte[] temp = str.getBytes();
            byte[] newbyte = new byte[endSize];
            if (cutLeft) {
                for (int i = newbyte.length - 1, j = temp.length - 1; i >= 0; i--, j--) {
                    newbyte[i] = temp[j];
                }
            } else {
                for (int i = 0; i < newbyte.length; i++) {
                    newbyte[i] = temp[i];
                }
            }
            str = new String(newbyte);
        }

        while (str.getBytes("gbk").length < endSize) {
            str += rightFill;
        }
        return str;
    }

    public static String padLeft(String input, char c, int length) {
        String output = input;
        while (output.length() < length) {
            output = c + output;
        }
        return output;
    }

    public static String toString(Throwable e) {
        if (e == null) {
            return "";
        }

        String exStr = e.getMessage();
        for (StackTraceElement ste : e.getStackTrace()) {
            exStr = exStr + "\n" + ste.toString();
        }

        return exStr;

    }

    public static String padRight(String input, char c, int length) {
        String output = input;
        while (output.length() < length) {
            output = output + c;
        }
        return output;
    }

    /** 右补空格
     * 
     * @param input
     * @param length
     * @return 
     */
    public static String padRight(String input, int length) {
        return padRight(input, ' ', length);
    }

    /** 左补0
     * 
     * @param input
     * @param length
     * @return 
     */
    public static String padLeft(String input, int length) {
        return padLeft(input, '0', length);
    }

    public static String bytePadLeft(String input, char c, int length) {
        String output = input;
        while (output.getBytes().length < length) {
            output = c + output;
        }
        return output;
    }

    public static String bytePadRight(String input, char c, int length) {
        String output = input;
        while (output.getBytes().length < length) {
            output = output + c;
        }
        return output;
    }
    
    public static String trim(String str) {
    	if(Strings.isNullOrEmpty(str))
    		return "";
    	else return str.trim();
    }
    
    public static String trim(Object obj) {
    	if(Strings.isNullOrEmpty(obj))
    		return "";
    	else return obj.toString().trim();
    }
    
    public static String trimNull(Object o) {
    	if(Strings.isNullOrEmpty(o))
    		return "";
    	else return o.toString();
    }
    
    public static String getMatching(String target, String pattern) {
		StringBuffer result = new StringBuffer();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(target);
		if (m.find()) {
			result.append(m.group());
		}
		return result.toString();
	}
    public static String random(int len) {
        String str = "";
        java.util.Random rander = new java.util.Random(System.currentTimeMillis());
        for (int i = 0; i < len; i++) {
            str += HEXCHAR[rander.nextInt(16)];
        }
        return str;
    }
    private static char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
}
