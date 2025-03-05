package com.assets.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assets.model.Asset;
import com.assets.service.AssetService;
import com.assets.service.FileStorageService;

import jakarta.validation.Valid;

@Controller
public class AssetRegistrationController {

    @Autowired
    private AssetService assetService;
    
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/assetRegistration")
    public String showAssetRegistrationForm(Model model) {
        model.addAttribute("asset", new Asset());
        return "assetRegistration"; 
    }
    @PostMapping("/assetRegistration")
    public String registerAsset(@ModelAttribute Asset asset, BindingResult result, 
    		 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "assetRegistration";
        }

        try {
            // Handle image upload
            MultipartFile imageFile = asset.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                // Save image
                String imagePath = fileStorageService.saveFile(imageFile);
                asset.setImagePath(imagePath); // Assuming you have a field for image path
            }

            assetService.saveAsset(asset);
            redirectAttributes.addFlashAttribute("successMessage", "Asset registered successfully!");
            return "redirect:/assetRegistration"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving asset: " + e.getMessage());
            return "redirect:/assetRegistration"; 
        }
    }

    
    @GetMapping("/assetUpdate/{id}")
    public String showAssetUpdateForm(@PathVariable(value="id") Long id, Model model) {
        Asset asset = assetService.getAssetById(id)
        			.orElseThrow(() -> new IllegalArgumentException("Invalid asset ID: " + id));
        model.addAttribute("asset", asset);
        return "assetUpdate";
    }
    @PostMapping("/assetUpdate/{id}")
    public String updateAsset(@PathVariable(value="id") Long id,@Valid @ModelAttribute Asset asset, 
                              BindingResult result, 
                              @RequestParam(value = "image", required = false) MultipartFile imageFile, 
                              RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (result.hasErrors()) {
            return "assetUpdate"; // Return to the form if there are validation errors
        }

        try {
            // Retrieve the existing asset by ID
            Optional<Asset> optionalAsset = assetService.getAssetById(id);

            if (optionalAsset.isPresent()) {
                Asset existingAsset = optionalAsset.get(); 

                // Handle image upload only if a new image is provided
                if (imageFile != null && !imageFile.isEmpty()) {
                    // Save the new image using the file storage service
                    String imagePath = fileStorageService.saveFile(imageFile);
//                    System.out.println("New Image Path: " + imagePath); // Add this line to log the path
                    
                    asset.setImagePath(imagePath); // Update the image path in the asset
                } else {
                    asset.setImagePath(existingAsset.getImagePath());
//                    System.out.println("Existing Image Path: " + asset.getImagePath()); // Add this line to log the path
                }

                // Set the correct asset ID to ensure the existing asset is updated
                asset.setId(id);
                
                assetService.saveAsset(asset);
                redirectAttributes.addFlashAttribute("successMessage", "Asset updated successfully!");
                return "redirect:/assetList"; 
            } else {
                // Handle the case where the asset is not found
                throw new RuntimeException("Asset not found with ID: " + id);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating asset: " + e.getMessage());
            return "redirect:/assetUpdate/" + id;
        }
    }

    
    @GetMapping("/assetDelete/{id}")
    public String deleteAsset(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            assetService.deleteAsset(id);
            redirectAttributes.addFlashAttribute("successMessage", "Asset deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting asset: " + e.getMessage());
        }
        return "redirect:/assetList"; 
    }
}
