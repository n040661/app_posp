package xdt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipTest {

    public static void main(String[] args) {
        readZip();
       // writeZip();
    }

    /**
     * 读取zip文件内容
     */
    private static void readZip() {
        File fil = new File("D:\\3a945c919f66f1ef.zip");
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(fil));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ZipEntry zipEn = null;
        /**
         * 需要读取zip文件项的内容时，需要ZipFile类的对象的getInputStream方法取得该项的内容，
         * 然后传递给InputStreamReader的构造方法创建InputStreamReader对象，
         * 最后使用此InputStreamReader对象创建BufferedReader实例
         * 至此已把zip文件项的内容读出到缓存中，可以遍历其内容
         */
        ZipFile zfil = null;
        try {
            zfil = new ZipFile("D:\\3a945c919f66f1ef.zip");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            while ((zipEn = zipIn.getNextEntry()) != null) {
                if (!zipEn.isDirectory()) { // 判断此zip项是否为目录
                    System.out.println(zipEn.getName() + ":\t");
                    /**
                     * 把是文件的zip项读出缓存，
                     * zfil.getInputStream(zipEn)：返回输入流读取指定zip文件条目的内容 zfil：new
                     * ZipFile();供阅读的zip文件 zipEn：zip文件中的某一项
                     */
                    BufferedReader buff = new BufferedReader(
                            new InputStreamReader(zfil.getInputStream(zipEn)));
                    String str;
                    while ((str = buff.readLine()) != null) {
                        System.out.println("\t" + str);
                    }
                    buff.close();
                }
                zipIn.closeEntry();// 关闭当前打开的项
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                zfil.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件 
     * 创建压缩文件
     * 若该文件本来就存在文件则清空，然后写入，直到关闭ZipOutputStream
     * 即从ZipOutputStream实例化到关闭期间写入的文件不会清空
     * 若不关闭ZipOutputStream流，则会压缩错误：不可预料的压缩文件末端
     * 
     * 压缩文件夹可以循环其内文件再读取其数据，再压缩文件
     * 
     */
    private static void writeZip() {
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream("D:\\av\\zip\\zip1.zip"));//若文件不存在则创建
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        int i=0;
        while (i<3) {
            ZipEntry entry = new ZipEntry(i+".txt");
            try {
                zipOut.putNextEntry(entry);// 此方法会清空zip文件原来存在的内容，然后创建新的文件1.txt，并将流定位到条目数据的开始处                                       
                zipOut.write(98);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            i++;
        }
        try {
            zipOut.close();//必须关闭，否则压缩文件错误：不可预料的压缩文件末端
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}