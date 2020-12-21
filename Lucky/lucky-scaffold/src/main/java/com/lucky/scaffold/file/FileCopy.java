package com.lucky.scaffold.file;

import java.io.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/23 17:38
 */
public abstract class FileCopy {

    public static final int BUFFER_SIZE = 4096;

    /**
     * 复制目录或文件，包含最外层文件夹
     * 注：将文件夹以及文件夹中的所有文件拷贝到另一个文件夹
     *     formDir:
     *          src
     *             |_main
     *                  |_java
     *                  |_resource
     *             |_test
     *             |_pom.xml
     *      toDir:
     *          fileCopy
     *
     *      复制后：
     *       toDir -->
     *       fileCopy
     *              |
     *              src
     *                  |_main
     *                      |_java
     *                      |_resource
     *                  |_test
     *                  |_pom.xml
     *
     * @param fromDir 源文件/文件夹
     * @param toDir 目标文件夹
     */
    public static void copyFolder(File fromDir, File toDir) throws IOException {
        if(fromDir.isDirectory()){
            File toFolder=new File(toDir+File.separator+fromDir.getName());
            copyFiles(fromDir,toFolder);
            return;
        }
        copyFiles(fromDir,toDir);
    }

    /**
     * 复制目录或文件，不包含最外层文件夹
     * 注：将文件夹中的所有文件拷贝到另一个文件夹
     *
     *     formDir:
     *          src
     *             |_main
     *                  |_java
     *                  |_resource
     *             |_test
     *             |_pom.xml
     *      toDir:
     *          fileCopy
     *
     *      复制后：
     *       toDir -->
     *       fileCopy
     *             |_main
     *                  |_java
     *                  |_resource
     *             |_test
     *             |_pom.xml
     *
     * @param fromDir 源文件/文件夹
     * @param toDir 目标文件夹
     * @throws IOException
     */
    public static void copyFiles(File fromDir, File toDir) throws IOException {
        if(!fromDir.exists())
            throw new RuntimeException("不存在的源文件: "+fromDir+" ,复制失败！");

        //判断源目录是不是一个目录
        if (!fromDir.isDirectory()) {
            FileOutputStream o=new FileOutputStream(toDir.getAbsoluteFile()+File.separator+fromDir.getName());
            FileInputStream i=new FileInputStream(fromDir);
            System.out.printf("正在写入文件: \n%s ==> %s...\n",fromDir.getAbsolutePath(),toDir.getAbsoluteFile()+File.separator+fromDir.getName());
            copy(i,o);
            return;
        }
        //如果目的目录不存在
        if (!toDir.exists()) {
            //创建目的目录
            toDir.mkdirs();
            System.out.printf("创建文件夹：[%s]\n",toDir.getAbsolutePath());
        }
        //获取源目录下的File对象列表
        File[] files = fromDir.listFiles();
        for (File file : files) {
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            String strFrom = fromDir + File.separator + file.getName();
            File from = new File(strFrom);
            String strTo = toDir + File.separator + file.getName();
            File to = new File(strTo);
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                copyFiles(from, to);
            }
            if (file.isFile()) {
                System.out.printf("正在写入文件: \n%s ==> %s\n",from.getAbsolutePath(),to.getAbsoluteFile());
                copy(new FileInputStream(from), new FileOutputStream(to));
            }
        }
    }

    private static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copyBase(InputStream in, OutputStream out) throws IOException {
        notNull(in, "No InputStream specified");
        notNull(out, "No OutputStream specified");

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }


    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param in  the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        notNull(in, "No InputStream specified");
        notNull(out, "No OutputStream specified");

        try {
            return copyBase(in, out);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }


    /**
     * 删除文件夹以及文件夹中所有的文件
     * @param folder
     */
    public static void deleteFile(File folder){
        File[] files = folder.listFiles();
        for (File f : files) {
            if(f.isFile()){
                f.delete();
            }else{
                deleteFile(f);
            }
        }
        folder.delete();
    }

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param in  the Reader to copy from
     * @param out the Writer to copy to
     * @return the number of characters copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(Reader in, Writer out) throws IOException {
        notNull(in, "No Reader specified");
        notNull(out, "No Writer specified");

        try {
            int byteCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    public static String copyToString(Reader in) throws IOException {
        if (in == null) {
            return "";
        }

        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }


}
