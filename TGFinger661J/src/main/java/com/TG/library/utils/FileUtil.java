package com.TG.library.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/3/5
 * 文件操作的工具类
 */
public class FileUtil {

    private static FileUtil fileUtil = null;

    public static FileUtil insance() {
        if (fileUtil == null) {
            synchronized (FileUtil.class) {
                if (fileUtil == null) {
                    fileUtil = new FileUtil();
                }
            }
        }
        return fileUtil;
    }

    public static boolean writeFile(byte[] imgData, String path) {
        boolean compalete = false;
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                boolean delete = file.delete();
                if (delete)
                    Log.d("===TAG===", "---   已存在的File删除成功...");
            }
            fos = new FileOutputStream(file);
            fos.write(imgData);
            fos.flush();
            compalete = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return compalete;
    }

    /**
     * 读取文件并返回byte[]数组
     *
     * @param path 文件所在的路径
     * @return
     */
    public static byte[] readFile(String path, byte[] tmpByte) {
        FileInputStream fis = null;
        File file = new File(path);
        if (!file.exists())
            return null;
        try {
            fis = new FileInputStream(file);
            fis.read(tmpByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tmpByte;
    }

    /**
     * 读取文件里边的数据
     *
     * @param file
     * @param fileDataByte
     */
    public static void readFile(File file, byte[] fileDataByte) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(fileDataByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从目标路径的文件夹下获取文件的名字
     *
     * @param path  指定的路径
     * @param index 数据列的索引
     * @return
     */
    public static String getFileName(String path, int index) {
        String fileName = "";
        File file = new File(path);
        if (!file.exists()) {
            return fileName;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (i == index) {
                        fileName = files[i].getName();
                        break;
                    }
                }
            } else {
                return fileName;
            }
        }
        return fileName;
    }

    /**
     * 更新指定文件夹下的文件
     */
    public static boolean updateFile(String path, String fileName, byte[] FileData) {
        boolean status = false;
        Log.d("===TAG","  更新文件，路径："+path);
        File file = new File(path);
        if (!file.exists()) {
            status = false;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    //目标位置的文件
                    File file1 = files[i];
                    String file1Name = file1.getName();
                    if (file1Name.equals(fileName)) {
                        boolean delete = file1.delete();
                        if (delete) {
                            File newFile = new File((path + File.separator + file1Name));
                            try {
                                FileOutputStream fos = new FileOutputStream(newFile);
                                fos.write(FileData);
                                fos.flush();
                                status = true;
                                Log.d("===TAG","   更新模板："+status);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            status = false;
                        }
                        break;
                    }
                }
            } else {
                status = false;
                Log.d("===TAG","   更新模板："+status);
            }
        }
        return status;
    }

    /**
     * 从文件中读取数据
     *
     * @param file
     */
    public static String readFileToStr(File file) {
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder("");
        try {
            fis = new FileInputStream(file);
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = fis.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            if (StringEmpty.isNotEmpty(sb.toString())) {
                String json = sb.toString();
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取传入路径下的文件夹下的所有文件的名称列表
     *
     * @param path 目标文件夹的路径
     * @return
     */
    public static ArrayList<String> getInitFinerFileList(String path) {
        ArrayList<String> fileList = null;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            fileList = new ArrayList<>();
            if (files != null) {
                if (files.length > 0) {
                    for (File file1 : files) {
                        String name = file1.getName();
                        fileList.add(name);
                    }
                } else {
                    return null;
                }
            }
        }
        return fileList;
    }

    /**
     * 删除指定文件目录下得指定文件
     *
     * @param path     指定文件夹得路径
     * @param fileName 指定文件得名字
     * @return
     */
    public static boolean removeFile(String path, String fileName) {
        boolean delete = false;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                String name = files[i].getName();
                if (name.equals(fileName)) {
                    delete = files[i].delete();
                    break;
                }
            }
        }
        return delete;
    }

    /**
     * 删除所有的文件
     *
     * @param path
     * @return
     */
    public static boolean removeAllFile(String path) {
        boolean delete = false;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                delete = file1.delete();
            }
        }
        return delete;
    }

    /**
     * 获取外置的SD卡的路径
     */
    public static String getExtendedMemoryPath(Context mContext) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            //            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                //                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                //                if (removable) {
                return path;
                //                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
