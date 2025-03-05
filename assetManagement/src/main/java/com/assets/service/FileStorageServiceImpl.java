package com.assets.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
	
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Max file size (e.g., 5 MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

    // Allowed file types (e.g., image formats)
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        
    	// Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds the maximum limit of 5 MB.");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            throw new IOException("File type not allowed. Allowed types are JPEG, PNG, and GIF.");
        }

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique file name to avoid overwriting files
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Create file path
        Path filePath = uploadPath.resolve(uniqueFileName);

        // Save the file using BufferedOutputStream for efficiency
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
            bos.write(file.getBytes());
        }

        return uniqueFileName; // Return the new unique file name
    }

    // Helper method to validate file types
    private boolean isAllowedFileType(String contentType) {
        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to get file extension
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
    
    // Method to retrieve an image as a Resource
    public Resource getImage(String imageName) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir, imageName);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new MalformedURLException("Could not read the image: " + imageName);
        }

        return resource;
    }
}