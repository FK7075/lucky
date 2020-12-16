package com.lucky.framework.uitls.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩类型枚举
 *
 * @author Log
 */
enum CompressType {
    //	GZIP是用于UNIX系统的文件压缩，在Linux中经常会使用到*.gz的文件，就是GZIP格式
    ZIP, JAR, GZIP
}

public class ZipUtils extends LuckyFile {

    private static final Logger log= LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 将单个文件或者文件夹压缩为zip
     *
     * @param srcFile 待压缩的文件或文件夹
     * @param zipFile 压缩后文件
     * @throws IOException
     */
    public static void compress(File srcFile, File zipFile) throws IOException {
        long start = System.currentTimeMillis();
        if (!srcFile.exists())
            throw new RuntimeException("源文件[" + srcFile + "]并不存在！无法进行压缩！");
        if (!zipFile.exists())
            zipFile.createNewFile();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFile)); // 创建ZipOutputStream类对象
        compress(out, srcFile, ""); // 调用方法
        out.close(); // 将流关闭
        long end = System.currentTimeMillis();
        log.info("压缩完成，耗时：" + (end - start) + " ms");
    }

    /**
     * 将单个文件或者文件夹压缩为zip
     *
     * @param srcFile 待压缩的文件或文件夹
     * @param zipFile 压缩后文件的路径
     * @throws IOException
     */
    public static void compress(File srcFile, String zipFile) throws IOException {
        compress(srcFile, new File(zipFile));
    }

    private static void compress(ZipOutputStream out, File f, String base)
            throws IOException {
        // 测试此抽象路径名表示的文件是否是一个目录
        if (f.isDirectory()) {
            // 获取路径数组
            File[] fl = f.listFiles();
            if (!"".equals(base)) {
                // 写入此目录的entry
                out.putNextEntry(new ZipEntry(base + "/"));
            }
            base = base.length() == 0 ? "" : base + "/"; // 判断参数是否为空
            for (int i = 0; i < fl.length; i++) { // 循环遍历数组中文件
                compress(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建新的进入点
            // 创建FileInputStream对象
            FileInputStream in = new FileInputStream(f);
            int b; // 定义int型变量
            byte[] buf = new byte[BUFFER_SIZE];
            log.info("正在压缩：" + base);
            while ((b = in.read(buf)) != -1) { // 如果没有到达流的尾部
                out.write(buf, 0, b);
                out.flush();
            }
            in.close();
        }
    }


    /**
     * 解压缩
     *
     * @param compressFile   待解压的压缩文件
     * @param unCompressFile 解压后的目标文件夹路径
     */
    public static void unCompress(File compressFile, String unCompressFile) {
        unCompress(compressFile, new File(unCompressFile));
    }

    /**
     * 解压缩
     *
     * @param compressFile   待解压的压缩文件
     * @param unCompressFile 解压后的目标文件夹
     */
    public static void unCompress(File compressFile, File unCompressFile) {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!compressFile.exists()) {
            throw new RuntimeException(compressFile.getPath() + "所指的压缩文件不存在！");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(compressFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                log.info("正在解压：" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = unCompressFile.getAbsolutePath() + File.separator + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(unCompressFile.getAbsolutePath() + File.separator + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(targetFile));
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            log.info("解压完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}