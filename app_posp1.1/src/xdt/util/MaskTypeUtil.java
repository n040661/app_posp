package xdt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

/**
 * 揭盖工具类
 * @author liuliehui
 *
 */
public class MaskTypeUtil {
	private static Map<String, String> accMap = new HashMap<String, String>(); // 账户号索引Map
	
	/**
	 * 获取揭盖的索值和要显示static 的信息
	 * @param map  保存到map中
	 * @param maskType 索引类型
	 * @param name 属性名
	 * @param value 属性值
	 */
	public static void getIndexShowValue(Map<String, String> map, MaskType maskType,String name,String value) {
		if(StringUtils.isEmpty(value)){
			map.put(name, value);
			return ;
		}
		switch (maskType) {
		case NONE:
			map.put(name, value);
			map.put(name + "_Show", value);
			break;
		case ACCOUNT:
			//账号遮盖
			map.put(name, getAccountIndex(value));
			map.put(name + "_Show", maskType.doMask(value));
			map.put(name + "_ShowLastFour", value.substring(value.length()-4));
			break;
		case PHONE:
			//手机号遮盖
			map.put(name, getAccountIndex(value));
			map.put(name + "_Show", maskType.doMask(value));
			break;
		case IDCARD:
			//身份证遮盖
			map.put(name, getAccountIndex(value));
			map.put(name + "_Show", maskType.doMask(value));
			break;
		case ACCNONAME:
			//账户姓名
			map.put(name, getAccountIndex(value));
			map.put(name + "_Show", maskType.doMask(value));
			break;
		default:
			map.put(name, maskType.doMask(value));
			break;
		}
	}
	
	/**
	 * 根据账户号获取唯一的索引号
	 * @param acc
	 * @return
	 * @throws AppException
	 */
	public static synchronized String getAccountIndex(String acc) {
		if (accMap.containsKey(acc)) {
			return accMap.get(acc); // 索引存在则直接取出
		} else { // 不存在生成唯一随机索引
			Random random = new Random();
			int rand = random.nextInt();
			String index = Integer.toHexString(rand);
			while (accMap.containsValue(index)) {
				rand = random.nextInt();
				index = Integer.toHexString(rand);
			}
			accMap.put(acc, index);
			return index;
		}
	}

	/**
	 * 根据账号索引获得账号
	 * @param index
	 * @return
	 */
	public static String getIndexAccount(String index) {
		for (Entry<String, String> entry : accMap.entrySet()) {
			if (entry.getValue().equals(index))
				return entry.getKey();
		}
		return null;
	}
}
