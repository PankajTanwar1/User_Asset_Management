package com.assets.web;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.assets.model.Asset;
import com.assets.service.AssetService;
import com.assets.service.FileStorageService;

@Controller
public class AssetDisplayController {

    @Autowired
    private AssetService assetService;
    
    @Autowired
	private FileStorageService fileStorageService;

    @GetMapping("/assetList")
    public String showAssetList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
//    public String showAssetList(Model model) {
//    	List<Asset> assets = assetService.getAllAssets();
//    	 model.addAttribute("assets", assets);
//        return "assetList"; 
    	return findPaginatedForAsset(1,"name","asc", model, keyword);
    }

    @GetMapping("/{id}")
    public String getAssetById(@PathVariable Long id, Model model) {
        Optional<Asset> asset = assetService.getAssetById(id);
        if (asset.isPresent()) {
            model.addAttribute("asset", asset.get());
            return "assetDetail";
        } else {
            return "404";
        }
    }
    
    @GetMapping("/assets/{imageName:.+}")
    public ResponseEntity<Resource> getAssetImage(@PathVariable String imageName) {
        try {
            Resource resource = fileStorageService.getImage(imageName);
            String contentType = getContentType(imageName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String imageName) {
        if (imageName.endsWith(".jpg") || imageName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (imageName.endsWith(".png")) {
            return "image/png";
        } else if (imageName.endsWith(".gif")) {
            return "image/gif";
        }
        return null; // Unknown type
    }

    
    
    @GetMapping("/assets/page/{pageNo}")
    public String findPaginatedForAsset(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam(value = "sortField", defaultValue = "name") String sortField,
                                @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                Model model,
                                @RequestParam(value = "keyword", required = false) String keyword) {
    int pageSize = 10;
    Page<Asset> page;

    // Validate sortDir
    if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
        sortDir = "asc";
    }

    if (keyword != null && !keyword.isEmpty()) {
        page = assetService.searchAsset(keyword, pageNo, pageSize, sortField, sortDir);
    } else {
        page = assetService.findPaginated(pageNo, pageSize, sortField, sortDir);
    }

    List<Asset> assets = page.getContent(); 
        
    model.addAttribute("currentPage", pageNo); 
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
        
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortDir", sortDir);
    model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
    model.addAttribute("assets", assets); 
    model.addAttribute("keyword", keyword);

    return "assetList";
    }
    
    @GetMapping("/assetHistory")
    public String showAssetHistory(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
        Model model) {

        List<Asset> assets = assetService.getAssetsByDateRange(startDate, endDate);
        model.addAttribute("assets", assets);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "assetHistory"; // Thymeleaf template
    }
    
}