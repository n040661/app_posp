package xdt.util;

import org.apache.commons.lang.StringUtils;

/**
 * 数学类公用方法
 * User: Jeff
 * Date: 15-6-2
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
public class UtilMath {

    /**
     * 向上取整的算法
     * @param amount
     * @return
     */
   public static Double   keepUpDouble(String amount){
        Double result = 0.0;
        if(StringUtils.isNotBlank(amount)){
            Double d = Double.parseDouble(amount);
            if(d > 0){
                result = Math.ceil(d);
            }else{      //如果是负数  则转换为正数
                result = Math.ceil(Math.abs(d));
            }
        }
        return  result;
   }
    public static Double   keepUpDouble(Double amount){
        Double result = 0.0;
        if(amount != null && amount > 0){
            result = Math.ceil(amount);
        }
        return  result;
   }

}
