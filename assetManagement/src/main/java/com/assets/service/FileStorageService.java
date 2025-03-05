package com.assets.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface FileStorageService {

	String saveFile(MultipartFile file) throws IOException;

	Resource getImage(String imageName) throws MalformedURLException;
}