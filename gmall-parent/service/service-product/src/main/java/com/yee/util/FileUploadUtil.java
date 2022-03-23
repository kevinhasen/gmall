package com.yee.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ClassName: FileUploadUtil
 * Description:
 * date: 2022/2/16 23:55
 * 文件上传
 * @author Yee
 * @since JDK 1.8
 */
public class FileUploadUtil {
    //静态初始化
    static {
        try {
            //读取配置文件
            ClassPathResource resource = new ClassPathResource("tracker.properties");
            //初始化参数
            ClientGlobal.init(resource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    public static String upload(MultipartFile file){
        try {
            //获得文件名
            String filename = file.getOriginalFilename();
            //获得storage信息
            StorageClient storageClient =
                    new StorageClient(new TrackerClient().getConnection(), null);
            //文件上传,上传的字节数组,文件后缀名,第三个参数description,为空
            String[] strings = storageClient.upload_file(file.getBytes(),
                    StringUtils.getFilenameExtension(filename), null);
            //返回文件路径,strings[0]是组名,strings[1]是具体路径
            return strings[0]+"/"+strings[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param groupName  组名
     * @param path 路径
     * @return
     */
    public static byte[] download(String groupName, String path){
        try {
            //获得storage信息
            StorageClient storageClient =
                    new StorageClient(new TrackerClient().getConnection(), null);
            byte[] bytes = storageClient.download_file(groupName, path);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     * @param groupName  组名
     * @param path 路径
     * @return
     */
    public static void delFile(String groupName, String path){
        try {
            //获得storage信息
            StorageClient storageClient =
                    new StorageClient(new TrackerClient().getConnection(), null);
            storageClient.delete_file(groupName, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //测试文件下载
    public static void main(String[] args) {
        String group = "group1";
        String path = "M00/00/02/wKjIgGINkuWAK2gtAACTFcPy5ps405.jpg";
        //测试文件下载
        byte[] download = FileUploadUtil.download(group, path);
        try {
            //需要提前设置文件名
            File file = new File("D:\\Temp\\1.jpg");
            //不存在则创建
            if (!file.exists()){
                file.createNewFile();
            }
            //
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(download);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
