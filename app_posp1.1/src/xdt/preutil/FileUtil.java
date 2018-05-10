package xdt.preutil;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;




public class FileUtil {
	
	/**获得文件夹File集合
	 * @param file 目标目录
	 * @return 文件夹File集合
	 */
	public static java.io.File[] listFolders(java.io.File file){
		return file.listFiles(new FileFilter(){
			public boolean accept(java.io.File pathname){
				if(pathname.isDirectory())return true;else return false;
			}
		});
	}
	
	/** 获得文件名,不包括后缀
	 * @param f
	 * @return
	 */
	public static String getName(File f){
		String name=f.getName();
		if(f.isFile()){
			return name.substring(0,name.lastIndexOf("."));
		}
		return null;
	}
	
	/**获得文件名,不包括后缀
	 * @param name
	 * @return
	 */
	public static String getName(String name){
		return name.substring(0,name.lastIndexOf("."));
	}
	
	/** 获得文件名的后缀,不包括点
	 * @param f
	 * @return
	 */
	public static String getNameSuffix(File f){
		String name=f.getName();
		if(f.isFile()){
			return name.substring(name.lastIndexOf(".")+1);
		}
		return null;
	}
	
	/** 获得文件名的后缀,不包括点
	 * @param f
	 * @return
	 */
	public static String getNameSuffix(String name){
		return name.substring(name.lastIndexOf(".")+1);
	}
	
	/**过滤指定后缀文件对象的集合
	 * @param file 目标文件夹
	 * @param suffix 后缀 (gif,doc,txt)
	 * @return 指定后缀文件对象的集合
	 */
	public static java.io.File[] listFileSuffix(java.io.File file,String suffix){
		final String[] suffixs=suffix.split(",");
		return file.listFiles(new FileFilter(){
			public boolean accept(java.io.File pathname){
				if(pathname.isFile()){
					String name=pathname.getName();
					for (int i = 0; i < suffixs.length; i++) {
						if(name.endsWith(suffixs[i]))return true;
					}
				}
				return false;
			}
		});
	}
	
	
	/** 读取文本返回字符串
	 * @param target
	 * @return
	 */
	public static String readTextFile(java.io.File target){
		FileInputStream fis =null;
		BufferedReader br =null;
		StringBuffer result=new StringBuffer();
		if(target.isFile()){
			try {
				String buf="";
				fis = new FileInputStream(target);
				br = new BufferedReader(new InputStreamReader(fis,"utf-8"));
				while ((buf=br.readLine())!=null) {
					result.append(buf);
					result.append("\n");
				}
				return result.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(br!=null)br.close();
					if(fis!=null)fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}

	
	/** 文本字符串写入到文件
	 * @param content
	 * @param target
	 */
	public static void writeTextFile(String content,java.io.File target){
		System.out.println(target.isFile());
		if(content!=null&&!"".equals(content)&&target.isFile()){
			FileOutputStream fos=null;
			Writer out=null;
			try {
				  fos = new FileOutputStream(target);   
				  out = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
				  out.write(content);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(out!=null)out.close();
					if(fos!=null)fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("文本写入文件失败");
		}
	}

	/**复制文件夹至另一个文件夹下
	 * @param src
	 * @param dest 必须是目录
	 * @throws IOException 
	 */
	public static void copyDirToDir(java.io.File src, java.io.File dest) throws IOException {
		String target=dest.getPath();
		if(src.isDirectory()&&dest.isDirectory()){
			target+=File.separator+src.getName();
		}
		copyFileToDir(src.getPath(),target);
    }
	
	
	// 复制文件   
	public static void copyFile(File sourceFile,File targetFile)throws IOException{  
	        // 新建文件输入流并对它进行缓冲   
	        FileInputStream input = new FileInputStream(sourceFile);
	        BufferedInputStream inBuff=new BufferedInputStream(input);  
	  
	        // 新建文件输出流并对它进行缓冲   
	        targetFile.setExecutable(true);  //可执行
	        targetFile.setReadable(true);  //可读
	        targetFile.setWritable(true); //可写
	        FileOutputStream output = null;
	        try {
	        	 output = new FileOutputStream(targetFile);
	        	 
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			BufferedOutputStream outBuff=new BufferedOutputStream(output); 
	         
	        
	        // 缓冲数组   
	        byte[] b = new byte[1024 * 5];  
	        int len;  
	        while ((len =inBuff.read(b)) != -1) {  
	            outBuff.write(b, 0, len);  
	        }
	        // 刷新此缓冲的输出流   
	        outBuff.flush();  
	          
	        //关闭流   
	        inBuff.close();  
	        outBuff.close();  
	        output.close();  
	        input.close();  
	}  

	/** sourceDir 文件夹下文件及文件夹复制至targetDir文件夹下
	 * @param sourceDir 必须是目录
	 * @param targetDir 必须是目录
	 * @throws IOException
	 */
	public static void copyFileToDir(String sourceDir, String targetDir)throws IOException{
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++){
		    if (file[i].isFile()){
		        //源文件
		    	File sourceFile=file[i];
		    	//目标文件   
		    	File targetFile=new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
		    	copyFile(sourceFile,targetFile);  
		    }
		    if (file[i].isDirectory()){
		    	//准备复制的源文件夹
		    	String dir1=sourceDir + "/" + file[i].getName();
		    	//准备复制的目标文件夹
		    	String dir2=targetDir + "/"+ file[i].getName();
		    	copyFileToDir(dir1, dir2);
		    }
		}
	}
	
	//获取项目路径
	public static String getProjectSystemPath(){
		String path=FileUtil.class.getResource("/").getPath();
		path=path.substring(1,path.lastIndexOf("WEB-INF"));
		return path;
	}
	
	/**
	 * 获取WEB根目录路径
	 */
	public static String getWebAppRootPath()throws Exception{
		String path = FileUtil.class.getResource("").getPath();
		if (StringUtils.isBlank(path) || path.indexOf("WEB-INF") == -1){
			throw new Exception("无法获取WEB根目录的路径");
		}
		return path.substring(0,path.indexOf("WEB-INF"));
	}
	
	/**
	 * 校验是否有目录，没有建立返回File
	 * 导出清算文件 
	 */
	public static File mkFile()throws Exception{
		String fileStr = "file";
		File file = null;
		
		file = new File(getWebAppRootPath()+File.separator+fileStr);
		if (!file.exists()){
			file.mkdir();
		}
		return file;
	}
	
	/**
	 * 校验是否有目录，没有建立返回File
	 * 导出对帐文件 
	 */
	public static File mkOutChkFile()throws Exception{
		String outChkFile = "outChkFile";
		File file = null;
		
		file = new File(getWebAppRootPath()+File.separator+outChkFile);
		if (!file.exists()){
			file.mkdir();
		}
		return file;
	}
	/**
	 * 校验是否有目录，没有建立返回File
	 * 文件导入清算
	 */
	public static File mkImportSettleFile(String dateString)throws Exception{
		File file = null;
		file = new File(getWebAppRootPath()+File.separator+"importSettleFile"+File.separator+dateString+File.separator);
		if (!file.exists()){
			file.mkdir();
		}
		return file;
	}
	
	
	/**
	 * 创建文件
	 */
	public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在，不需要再次创建！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
               System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }
	
	
	
	
	
	
	
	public static void main(String[] args) {
		 String privatekeyFile = "F:\\repertory\\xinc\\trunk\\java\\app_posp\\app_posp1.1\\config\\key\\pri_pkcs8.pem";  
		File file = new File(privatekeyFile);
		String s = readTextFile(file);
		System.out.println(s);
	}
	
	
	
	
	
	
	
}
