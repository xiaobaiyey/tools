package com.xiaobai.android;

import com.android.dex.Dex;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.merge.CollisionPolicy;
import com.android.dx.merge.DexMerger;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 用于超多dex文件合并
 */
public class Main {
    private static HashMap<String, Dex> dexHashMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // write your code here
        String zipPath = null;
        if (args.length == 0) {
            System.out.println("请输入压缩包路径：");
            Scanner scanner = new Scanner(System.in);
            zipPath = scanner.next();

        } else {
            zipPath = args[0];
        }


        if (!checkApkFile(zipPath)) {
            System.out.println("请检查输入路径");
        }
        FileInputStream fileInputStream = new FileInputStream(zipPath);
        ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        getDexFromApk(zipInputStream);
        startMerger();
    }

    private static void startMerger() throws IOException {
        System.out.println("开始尝试合并dex文件");
        Dex dex = dexHashMap.get("classes.dex");
        dexHashMap.remove("classes.dex");
        int index = 0;
        boolean hasMore = false;
        for (String name : dexHashMap.keySet()) {
            Dex temp = dexHashMap.get(name);
            if (checkDex(dex, temp)) {
                System.out.println("开始保存第" + index + "个dex文件");
                FileOutputStream fileOutputStream = new FileOutputStream("classes" + index + ".dex");
                fileOutputStream.write(dex.getBytes());
                fileOutputStream.close();
                dex = temp;
                index++;
                continue;
            }
            hasMore = true;
            DexMerger dexMerger = new DexMerger(new Dex[]{dex, temp}, CollisionPolicy.FAIL, new DxContext());
            dex = dexMerger.merge();

        }

        if (hasMore) {
            System.out.println("开始保存第" + index + "个dex文件");
            FileOutputStream fileOutputStream = new FileOutputStream("classes" + index + ".dex");
            fileOutputStream.write(dex.getBytes());
            fileOutputStream.close();
        }
        System.out.println("合并完毕");

    }

    /**
     * 检查字段是否超标,可能不全，后面遇到问题在补充
     *
     * @param dex
     * @param temp
     * @return
     */
    private static boolean checkDex(Dex dex, Dex temp) {
        int dexmethodSize = dex.methodIds().size() + temp.methodIds().size();
        int fieldSize = dex.fieldIds().size() + temp.fieldIds().size();
        int proIdSzie = dex.protoIds().size() + temp.protoIds().size();
        if (dexmethodSize > 0xffff || fieldSize > 0xffff || proIdSzie > 0xffff) {
            return true;
        }
        return false;
    }

    /**
     * 读取所有的dex文件
     *
     * @param zipInputStream
     */
    private static void getDexFromApk(ZipInputStream zipInputStream) throws IOException {
        while (true) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) {
                break;
            }
            String name = zipEntry.getName();
            if (name.startsWith("classes") && name.endsWith(".dex")) {
                // System.out.println(name);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int readed = 0;
                while ((readed = zipInputStream.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, readed);
                }

                Dex dex = new Dex(byteArrayOutputStream.toByteArray());
                dexHashMap.put(name, dex);
            }
            zipInputStream.closeEntry();

        }
        System.out.println("读取dex完毕");
        System.out.println("总共：" + dexHashMap.size() + "个dex文件");
    }


    /**
     * 检查文件是否存在，或者是否有效
     *
     * @param path
     * @return
     */
    private static boolean checkApkFile(String path) {

        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            return false;
        } else {

            return true;
        }
        //return false;
    }


}
