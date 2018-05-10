package ormtools;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mybatis.generator.api.*;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.*;
import org.mybatis.generator.internal.*;
public class Generator {
    @Test
	public void testBuild() throws Exception {
		// 信息缓存     
		List<String> warnings = new ArrayList<String>();                 
		// 覆盖已有的重名文件                
		boolean overwrite = true;                 
		// 准备 配置文件                 
		File configFile = new File("G:\\Personal\\副业\\codecloud\\trunk\\java\\app_posp\\app_posp1.1\\src\\ormtools\\Generator.xml");
		// 1.创建 配置解析器               
		ConfigurationParser parser = new ConfigurationParser(warnings);                 
		// 2.获取 配置信息               
		Configuration config = parser.parseConfiguration(configFile);                 
		// 3.创建 默认命令解释调回器                
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);                
		// 4.创建 mybatis的生成器                
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);                 
		// 5.执行，关闭生成器         
		myBatisGenerator.generate(null);     
   }
}
