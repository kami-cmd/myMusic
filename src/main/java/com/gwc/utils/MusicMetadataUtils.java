package com.gwc.utils;

import com.gwc.entity.Music;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static com.gwc.utils.StringContent.COVER_PATH;

public class MusicMetadataUtils {


    /*
    音乐解析
     */

    public static Music parse(MultipartFile file) throws IOException {
        //返回的对象
        Music metadata = new Music();

        // 创建临时文件（后缀保留原文件扩展名）
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        File tempFile = File.createTempFile("audio_", suffix);
        try {
            //通过Files.copy来拿到文件而不破坏文件
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //加载文件
            AudioFile audioFile = AudioFileIO.read(tempFile);
            //拿到各项数据
            Tag tag = audioFile.getTag();
            // 获取音频头信息
            var audioHeader = audioFile.getAudioHeader();

            if (tag != null) {
                //歌曲名字
                metadata.setName(tag.getFirst(FieldKey.TITLE));
                //歌曲演唱者
                metadata.setSinger(tag.getFirst(FieldKey.ARTIST));
                //专辑
                metadata.setAlbum(tag.getFirst(FieldKey.ALBUM));
                //拿到封面
                List<Artwork> artworkList = tag.getArtworkList();
                if (!artworkList.isEmpty()) {
                    Artwork artwork = artworkList.get(0);
                    String mimeType = artwork.getMimeType();
                    //图片的扩展名
                    String ext = mimeType.substring(mimeType.lastIndexOf("/") + 1);

                    String coverPath = FileUtils.upFile(COVER_PATH, artwork.getBinaryData(), "." + ext);

                    metadata.setCoverAddress(coverPath);
                }
            }
            //整数秒
            int duration = audioHeader.getTrackLength();
            //转化时间
            String resultTime = ParseTime(duration);
            metadata.setTime(resultTime);
            // 获取文件大小（直接从 MultipartFile 获取）
            long size = file.getSize();
            //转化文件大小
            String resultSize = ParseSize(size);
            metadata.setSize(resultSize);
        } catch (Exception e) {
            // 解析失败时记录日志，使用文件名作为备选
            e.printStackTrace();
        } finally {
            // 无论成功或失败，最后删除临时文件
            Files.deleteIfExists(tempFile.toPath());
        }
        // 如果解析出的字段为空，使用文件名（不含扩展名）作为歌曲名
        if (metadata.getName() == null || metadata.getName().isEmpty()) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                metadata.setName(fileName.substring(0, dotIndex));
            } else {
                metadata.setName(fileName);
            }
        }
        return metadata;
    }

    private static String ParseSize(long size) {
        if (size <= 0) return "0MB";
        double mb = size / (1024.0 * 1024.0); // 转换为 MB
        return String.format("%.1fMB", mb);
    }

    private static String ParseTime(int duration) {
        int min = duration / 60;
        int second = duration % 60;
        return min + ":" + second;
    }

}
