package com.lwjhn.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lwjhn
 * @Date: 2020-11-5
 * @Description: com.lwjhn.domino
 * @Version: 1.0
 */
public class FileOperator {
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!deleteDir(new File(dir, children[i]))) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    /**
     * 验证字符串是否为正确路径名的正则表达式
     *
     * @param sPath 文件或文件夹名称
     * @return
     */
    public static boolean matchFilePath(String sPath) {
        return sPath.matches("^([A-Za-z]:)?[/\\\\][^:?\"><*]*");
    }

    /**
     * 文件拷贝
     *
     * @param src_name 源文件名
     * @param des_name 目标文件名
     */
    public static boolean copyFile(String src_name, String des_name) {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(src_name);
            os = new FileOutputStream(des_name);

            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = is.read(buff, 0, 1024)) > 0) {
                os.write(buff, 0, rc);
                os.flush();
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean copyDir(String src_name, String des_name) {
        File src_file = new File(src_name);
        if (src_file.isFile()) {
            return copyFile(src_name, des_name);
        }

        File des_file = new File(des_name);
        if (!des_file.exists()) {
            des_file.mkdirs();
        }

        File[] fs = src_file.listFiles();
        for (File f : fs) {
            if (f.isFile()) {
                if (!copyFile(f.getPath(), des_name + "\\" + f.getName())) {
                    return false;
                }
            } else if (f.isDirectory()) {
                if (!copyDir(f.getPath(), des_name + "\\" + f.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 文件拷贝
     *
     * @param src_name 源文件名
     * @param des_name 目标文件名
     */
    public static boolean copyFileByChannel(String src_name, String des_name) {
        FileInputStream is = null;
        FileOutputStream os = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            is = new FileInputStream(src_name);
            os = new FileOutputStream(des_name);
            in = is.getChannel();
            out = os.getChannel();

            in.transferTo(0, in.size(), out);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean copyDirByChannel(String src_name, String des_name) {
        File src_file = new File(src_name);
        if (src_file.isFile()) {
            return copyFileByChannel(src_name, des_name);
        }

        File des_file = new File(des_name);
        if (!des_file.exists()) {
            des_file.mkdirs();
        }

        File[] fs = src_file.listFiles();
        for (File f : fs) {
            if (f.isFile()) {
                if (!copyFileByChannel(f.getPath(), des_name + "\\" + f.getName())) {
                    return false;
                }
            } else if (f.isDirectory()) {
                if (!copyDirByChannel(f.getPath(), des_name + "\\" + f.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean newFile(String filename, InputStream is) throws Exception {
        return newFile(new File(filename), is);
    }

    public static boolean newFile(String filename, byte[] buff) throws Exception {
        return newFile(new File(filename), buff);
    }

    public static boolean newFile(File file, byte[] buff) throws Exception {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(buff);
            os.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean newFile(File file, InputStream is) throws Exception {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = is.read(buff, 0, 1024)) > 0) {
                os.write(buff, 0, rc);
                os.flush();
            }
            return true;
        } catch (Exception e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final String FileDeliRegex = "[/\\\\]+";
    public static final Pattern FileRootPattern = Pattern.compile("^([A-Za-z]:)?([/\\\\].*)");
    public static final String FileFolderPathRegex = "(^|[/\\\\])[^/\\\\]+$";
    public static final String FileNameRegex = ".*[/\\\\]";
    public static final String FileAliasRegex = "(.*[/\\\\])|(\\.[0-9a-z]+$)";

    public static String getAvilableFileName(String base) {
        return base == null ? "" : base.replaceAll("[`~!@#$%^&*<>?:'\"{},;/|\\\\]", "");
    }

    public static String getAvilableFilePath(String base) {
        return base == null ? "" : base.replaceAll("[`~!@#$%^&*<>?:'\"{},;|]", "").replaceAll(FileDeliRegex, "/");
    }

    public static String getAvailablePath(String... base) {
        return getAvailablePath(false, base);
    }

    public static String getAvailablePath(boolean filterDiagonal, String... elements) {
        StringJoiner joiner = new StringJoiner("/");
        String buf;
        Matcher matcher = FileRootPattern.matcher(buf = elements[0].replaceAll(FileDeliRegex, "/"));
        if (matcher.find()) {
            if (matcher.group(1) != null) joiner.add(matcher.group(1));
            buf = matcher.group(2);
        }
        joiner.add((filterDiagonal ? "/" + getAvilableFileName(buf) : getAvilableFilePath(buf))
                .replaceAll("[/\\\\]+$", ""));
        for (int index = 1; index < elements.length; index++) {
            buf = (filterDiagonal ? getAvilableFileName(elements[index]) : getAvilableFilePath(elements[index]))
                    .replaceAll("^[/\\\\]+|[/\\\\]+$", "");
            if ("".equals(buf)) continue;
            joiner.add(buf);
        }
        return joiner.toString();
    }

    public static String getFileFolderByRegex(String path) {
        return path.replaceAll(FileFolderPathRegex, "").replaceAll(FileDeliRegex, "/");
    }

    public static String getFileNameByRegex(String path) {
        return getAvilableFileName(path.replaceAll(FileNameRegex, ""));
    }

    public static String getFileAliasByRegex(String path) {
        return getAvilableFileName(path.replaceAll(FileAliasRegex, ""));
    }
}

