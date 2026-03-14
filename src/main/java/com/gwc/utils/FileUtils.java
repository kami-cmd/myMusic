package com.gwc.utils;


import cn.hutool.core.lang.UUID;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.io.IOUtils;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.gwc.utils.StringContent.ZIP_NAME;
import static com.gwc.utils.StringContent.ZIP_NAME_LENGTH;

public class FileUtils {
    private static final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static final String bucketName = "gwc-music";
    // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
    private static final String region = "cn-beijing";
    // 从环境变量中获取访问凭证。运行本代码示例之前，请先配置环境变量
    private static final EnvironmentVariableCredentialsProvider credentialsProvider;

    static {
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private static final OSS ossClient = OSSClientBuilder.create()
            .endpoint(endpoint)
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build();


    public static String upFile(String preKey, byte[] fileBytes, String originalFileName) {
        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 显式声明使用 V4 签名算法
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        /*--------------------------以上是配置内容-------------------------------------------------*/
        //上传文件
        //存储的空间
        //preKey指例如music,userAct等
        //生成文件的存储名字
        String fileName = getFileName(originalFileName);
        String objectPath = preKey + "/" + fileName;
        //把文件自动转成字节传入
        ossClient.putObject(bucketName, objectPath, new ByteArrayInputStream(fileBytes));
        //返回路径,http(s)://<Bucket名称>.<Endpoint>/<Object完整路径>
        return "http://" + bucketName + "." + endpoint.substring(endpoint.indexOf("oss")) + "/" + objectPath;
    }

    private static String getFileName(String originalFileName) {
        //拿到后缀
        String fileEndName = originalFileName.substring(originalFileName.indexOf("."));
        //生成uuid
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //拼上,返回
        return uuid + fileEndName;
    }

    public static void downloadFile(String preKey, HttpServletResponse response, String ossFileNames) throws IOException {
        //拿到文件的路径
        String objectPath = preKey + "/" + ossFileNames;
        //拿到oss对象
        OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, objectPath));
        // 设置响应头
        // 告诉浏览器以附件形式下载文件
        response.setContentType("application/octet-stream");
        // 对文件名进行URL编码，避免中文乱码
        String encodedFileName = URLEncoder.encode(objectPath, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        // 可选：设置文件大小，提升下载进度显示
        long contentLength = ossObject.getObjectMetadata().getContentLength();
        response.setContentLengthLong(contentLength);
        //通过流的方式输出与传入
        InputStream inputStream = ossObject.getObjectContent();
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream, outputStream);

        //刷新
        outputStream.flush();
        //关流
        inputStream.close();
    }

    public static void downloadFilesByZip(String preKey, HttpServletResponse response, List<String> ossFileNames) throws IOException {
        // 设置响应头，强制下载ZIP文件,以及解决中文编码问题
        response.setContentType("application/octet-stream");
        String finialZipName = "播放量TOP10_" + System.currentTimeMillis() + ".zip";
        String encodedFileName = URLEncoder.encode(finialZipName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        //使用ZipOutputStream将多个OSS文件实时压缩输出
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        //循环写字
        int index = 0;
        int count = 1;
        for (String fileName : ossFileNames) {
            // 从OSS获取文件流
            //完整路径
            String songPath = preKey + "/" + fileName;
            //拿到oss对象
            OSSObject ossObject = ossClient.getObject(bucketName, songPath);
            // 构造ZIP内部文件名（可自定义格式）
            String zipEntryName = String.format(count + "." + ZIP_NAME.charAt(index) + fileName.substring(fileName.indexOf(".")));

            // 创建ZIP条目并写入流
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zipOut.putNextEntry(zipEntry);

            InputStream in = ossObject.getObjectContent();
            IOUtils.copy(in, zipOut);
            //关流
            in.close();
            zipOut.closeEntry();
            //文字错位
            index++;
            index %= ZIP_NAME_LENGTH;
            count++;
        }
        zipOut.close();
    }

    public static void deleteFile(String url, String ossPath) {
       String filePath = url.substring(url.lastIndexOf(ossPath));
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, filePath);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        }
    }
}
