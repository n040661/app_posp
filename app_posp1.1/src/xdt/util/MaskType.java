package xdt.util;

import org.apache.log4j.Logger;

/**
 * 揭盖实体类
 * @author liuliehui
 *
 */
public enum MaskType {

	NONE("无") {
		@Override
		public String doMask(String input) {
			return input;
		}
	},

	ACCOUNT("账号") {
		@Override
		public String doMask(String input) {
			if (input.length() > 19 || input.length() < 13) {
				logger.warn(String.format("account [%s] length is wrong", input));
				if (input.length() < 8)
					return input;
			}
			StringBuilder sb = new StringBuilder();
			while (input.length() > 8) {
				sb.append(input.substring(0, 4));
				sb.append(' ');
				input = input.substring(4);
			}
			sb.append("**** ");
			sb.append(input.substring(4));
			return sb.toString();
		}
	},

	PHONE("手机号") {
		@Override
		public String doMask(String input) {
			if (input.length() < 8)
				return input;
			StringBuilder sb = new StringBuilder();
			while (input.length() > 8) {
				sb.append(input.substring(0, 3));
				sb.append(' ');
				input = input.substring(3);
			}
			sb.append("**** ");
			sb.append(input.substring(4));
			return sb.toString();
		}
	},

	IDCARD("身份证") {
		@Override
		public String doMask(String input) {
			if (input.length() < 10)
				return input;
			StringBuilder sb = new StringBuilder();
			sb.append(input.substring(0, 4));
			sb.append(" *** ");
			sb.append(input.substring(input.length() - 4));
			return sb.toString();
		}
	},
	ACCNONAME("账户姓名") {
		@Override
		public String doMask(String input) {
			  if(input == null  || input.contains("$") || input.trim().length() == 0){
				   return "";
			  } 
			  input = input.trim();
			  StringBuilder sb = new StringBuilder();
			  for(int i = 0 ; (i < 8 && i < input.length()); i ++){
				  if (i % 2 == 1){
				    sb.append("*");
				  } else {
				    sb.append(input.charAt(i));
				  }
			  }
			  if (input.length() > 8) {
				  sb.append(input.substring(8));
			  }
			return sb.toString();
		}
	};

	private String name;

	private MaskType(String name) {
		this.name = name;
	}

	public String doMask(String input) {
		return input;
	}

	@Override
	public String toString() {
		return name;
	}

	private static Logger logger = Logger.getLogger(MaskType.class);
}