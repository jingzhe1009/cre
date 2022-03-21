package com.bonc.frame.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/24 22:05
 */
public enum WriterUtil {

    ;

    private static Log log = LogFactory.getLog(WriterUtil.class);

    public static void inputStreamToFile(InputStream ins, File file) {
        FileOutputStream os = null;
        try {
//            createFile(file);
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException("调用inputStreamToFile产生异常：" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("inputStreamToFile关闭io产生异常：" + e.getMessage());
            }
        }
    }


    public static String getfilePath(String url) {
        File path2 = null;
        try {
//            String aaurl = ResourceUtils.getURL("classpath:static").getPath();
            URL aaurl = ResourceUtils.getURL("file:/D:\\C\\54075\\Desktop\\test");
            url = aaurl.getPath();
            path2 = new File(url.replace(" ", " ").replace('/', '\\'));
//            path2 = new File(ResourceUtils.getURL("classpath:static").getPath().replace(" ", " ").replace('/', '\\'));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean save2File(String filepath, String results) {

        log.info("保存文件路径 : " + filepath);

        boolean flag = true;
        //获取文件路径
        File file = new File(filepath);
        String absolutePath = file.getAbsolutePath();
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            if (!mkdirs) {
                System.out.println("文件夹创建失败");
                flag = false;
                return flag;
            }
        }
        // 写
        try (PrintWriter os = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));) {
//        try (OutputStream os = new FileOutputStream(file)) {
            os.write(results);
            os.flush();
        } catch (FileNotFoundException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public static String getOutputFilePath(String filePath, String fileName) {

        StringBuilder result;
        do {
            result = new StringBuilder();
            result.append(filePath).append("/").append(fileName)
                    .append("-").append(getNowtime()).append(".csv");
        } while (new File(result.toString()).exists());
        return result.toString();
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowtime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(date) + (UUID.randomUUID().toString().replaceAll("[-]", ""));
        return time;
    }

    // -------------------------------------------- zip -------------------------------------------
    public static void zipFiles(List<File> srcfilelist, File zipfile) throws Exception {
        if (srcfilelist == null || srcfilelist.isEmpty()) {
            throw new Exception("压缩文件失败,源文件为null");
        }
        byte[] buf = new byte[2048];
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (File srcfile : srcfilelist) {
                if (!srcfile.exists()) {
                    log.warn("文件不存在");
                    throw new Exception("压缩文件失败,源文件不存在,缺失文件:[" + srcfile + "]");
                }
                BufferedReader reader = new BufferedReader(new FileReader(srcfile));
                out.putNextEntry(new ZipEntry(srcfile.getName()));

                String tempString = null;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    out.write(tempString.getBytes());
                }
                out.closeEntry();
                reader.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * zip解压
     *
     * @param srcFile     zip源文件
     * @param destDirPath 解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[2048];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) + " ms");
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

    public static boolean deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                //递归删除目录中的子目录下
                if (children != null) {
                    for (String aChildren : children) {
                        boolean success = deleteFile(new File(file, aChildren));
                        if (!success) {
                            return false;
                        }
                    }
                }
            }
            // 目录此时为空，可以删除
            return file.delete();
        }
        return true;
    }

    public static boolean createFile(File file) throws Exception {
        if (file == null) {
            throw new Exception("创建文件失败,File为null,file:[" + file + "]");
        }
        if (!file.exists()) {
            String absolutePath = file.getAbsolutePath();
//            System.out.println(absolutePath);
            boolean b = mkdirsParentFile(file);
        }
        if (!file.exists()) {
            boolean b = mkdirsParentFile(file);
            if (!b) {
                throw new Exception("文件夹创建失败");
            }
        }
        return true;
    }

    private static boolean mkdirsParentFile(File file) {
        if (file == null) {
            return false;
        }
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            boolean mkdirs = parentFile.mkdirs();
            return mkdirs;
        }
        return false;
    }

    public static String unzip(InputStream inputStream) {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            StringBuilderWriter sw = new StringBuilderWriter();
            while (zipInputStream.getNextEntry() != null) {
                IOUtils.copy(zipInputStream, sw);
                zipInputStream.closeEntry();
            }
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException("读取压缩文件失败", e);
        }
    }

}
