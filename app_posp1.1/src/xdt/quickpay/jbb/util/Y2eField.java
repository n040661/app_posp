package xdt.quickpay.jbb.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.FIELD })
// 用于字段
@Retention(RetentionPolicy.RUNTIME)
// 在运行时加载到Annotation到JVM�?
public @interface Y2eField {
	/**
	 * @函数说明:获取该属性的类型
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 */
	String type() default "text";// 字段类型

	/**
	 * @函数说明:获取该属性配置的路径
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 */
	String path(); // 定义个字符串,元素路径
}