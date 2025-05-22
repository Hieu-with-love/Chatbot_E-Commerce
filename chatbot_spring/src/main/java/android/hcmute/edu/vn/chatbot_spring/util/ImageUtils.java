package android.hcmute.edu.vn.chatbot_spring.util;

import android.hcmute.edu.vn.chatbot_spring.configuration.CloudinaryConfig;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImageUtils {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile fileImage){
        try{
            // 5MB
            long MAX_SIZE = 5 * 1024 * 1024;
            if (fileImage.getSize() > MAX_SIZE){
                throw new RuntimeException("File size exceeds limit");
            }
            if (!isImage(fileImage)){
                throw new RuntimeException("File is not an image");
            }
            Map uploadResult = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed" + e.getMessage());
        }
    }

    private boolean isImage(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }

    public String deleteImage(String publicId){
        try{
            Map deletedImage = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if (deletedImage.get("result").equals("ok")){
                return "Delete image successfully";
            } else {
                return "Delete image failed";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
