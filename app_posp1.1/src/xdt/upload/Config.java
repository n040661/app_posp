package xdt.upload;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Config {
	
	private static Logger log = Logger.getLogger(Config.class);	

    private static Properties proPerties =null;


	static { // 自动初始化properties 
		if(proPerties==null){
			proPerties =new Properties();
			 InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("fileconfig.properties");   
		      try {   
		    	  proPerties.load(inputStream); 
		    	  //log.info("loading system.properties files Success.");
	
		      } catch (IOException e) {   
		       //log.info("loading system.properties files Failure."+e.getMessage());
		       System.exit(0);
		      }
		}
	}
    
	/**
	 * getProPerties方法重载
	 * @param key
	 * @return
	 */
	private static String getProPerties(String key) {
	
		return proPerties.getProperty(key);
	}

	public static String getProPerties(String key,String defaultValue) {
		String value = defaultValue;
	
	    if(getProPerties(key)!=null && !"".equals(getProPerties(key)))value=getProPerties(key);
	    	  
	    log.debug("get key: "+key+" properties's " +getProPerties(key));
	
	    return value;
	
	}

	public static Boolean getProPerties(String key,Boolean defaultValue) {
		Boolean value = defaultValue;
	
	    if(getProPerties(key)!=null && !"".equals(getProPerties(key)))
	    value= Boolean.valueOf(getProPerties(key));
	
	    return value;
	
	}

	public static Integer getProPerties(String key,int defaultValue) {
		Integer value = defaultValue;
	
	      if(getProPerties(key)!=null && !"".equals(getProPerties(key)))
			  value= Integer.valueOf(getProPerties(key));
	      return value;
		
	}
}
