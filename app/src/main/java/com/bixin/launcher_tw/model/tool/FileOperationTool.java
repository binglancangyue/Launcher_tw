package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.bixin.launcher_tw.model.LauncherApp;
import com.trello.rxlifecycle2.android.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class FileOperationTool {
    private static int failureCode = 0;
    private static final String TAG = "FileOperationTool";

    public static boolean reNameFile(String path, String name) {
        File file = new File(path);
        String newPath;
        if (file.isFile()) {
            String suffixName = path.substring(path.lastIndexOf("."));
            path = path.substring(0, path.lastIndexOf("/"));
            newPath = path + "/" + name + suffixName;
        } else {
            path = path.substring(0, path.lastIndexOf("/"));
            newPath = path + "/" + name;
        }
        if (new File(newPath).exists()) {
            failureCode = 1;
            return false;
        }
        failureCode = 0;
        return file.renameTo(new File(newPath));
    }

    public static int getFailureCode() {
        return failureCode;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
/*            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }*/

        /* 如果不需要打log，可以使用下面的语句
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        */

            FileInputStream fileInputStream = new FileInputStream(oldPathName);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制文件夹及其中的文件
     *
     * @param oldPath String 原文件夹路径 如：data/user/0/com.test/files
     * @param newPath String 复制后的路径 如：data/user/0/com.test/cache
     * @return <code>true</code> if and only if the directory and files were copied;
     * <code>false</code> otherwise
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e("--Method--", "copyFolder: cannot create directory.");
                    return false;
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp
                            .getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }

                /*if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e("--Method--", "copyFolder:  oldFile not exist.");
                    return false;
                } else if (!temp.isFile()) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.");
                    return false;
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.");
                    return false;
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }*/
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG, "copyFolder: fail");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAll(String copyFilePath, String newFilePath) {
        if (new File(copyFilePath).isFile()) {
            return copyFile(copyFilePath, newFilePath);
        } else {
            return copyFolder(copyFilePath, newFilePath);
        }
    }

    public static void deleteAllFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteAllFile(f);
            }
            file.delete();
        }
    }

    public static String getFileLastModifiedTime(File file) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(file.lastModified());
    }

    /**
     * 调用系统打开文件菜单
     *
     * @param fileDirectory 地址
     */
    public static void openFile(String fileDirectory) {
        File file = new File(fileDirectory);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(LauncherApp.getInstance(),
                    BuildConfig.APPLICATION_ID, file);
            intent.setDataAndType(contentUri, getMIMEType(file));
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));//设置类型
        }
        try {
            LauncherApp.getInstance().startActivity(intent);
        } catch (Exception e) {
//            ToastTool.showToast(R.string.cannot_open_file);
//            Log.e(TAG, "Exception: " + e.getCause() + " \n :" + e.getMessage());
        }
    }

    /**
     * 获取文件类型
     *
     * @param file 需要区分类型的文件
     * @return 文件类型
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int length = fName.length();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        int dotIndex = fName.lastIndexOf(".") + 1;
        if (dotIndex <= 0) {
            return type;
        }

        // 获取文件的后缀名
        String fileType = fName.substring(dotIndex, length).toLowerCase();
        if ("".equals(fileType)) {
            Log.d(TAG, "getMIMEType: null");
            return type;
        }
        if (mimeTypeMap.hasExtension(fileType)) {
            return mimeTypeMap.getMimeTypeFromExtension(fileType);
        }
        return type;
    }

}
