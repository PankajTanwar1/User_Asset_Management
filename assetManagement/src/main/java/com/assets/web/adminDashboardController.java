package com.assets.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assets.model.Asset;
import com.assets.service.AssetService;
import com.assets.service.UserService;

@Controller
@RequestMapping("/adminDashboard")
public class adminDashboardController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AssetService assetService;
	
    @GetMapping
    public String displayAdminDashboard(Model model) {
    	
    	
    	List<Asset> assets = assetService.getAllAssets();    	
    	long totalUsers = userService.countTotalUsers();
    	long totalAssets = assetService.countTotalAssets();
    	
    	model.addAttribute("assets", assets);
    	model.addAttribute("totalAssets", totalAssets);
    	model.addAttribute("totalUsers", totalUsers);
    	
    	Map<String, Long> assetsByWeek = assetService.getAssetsGroupedByWeek();
        Map<String, Long> assetsByMonth = assetService.getAssetsGroupedByMonth();
        Map<String, Long> assetsByYear = assetService.getAssetsGroupedByYear();
        model.addAttribute("assetsByWeek", assetsByWeek);
        model.addAttribute("assetsByMonth", assetsByMonth);
        model.addAttribute("assetsByYear", assetsByYear);
    	
        Map<String, Long> assetCountByStatus = assetService.getAssetCountByStatus();
        model.addAttribute("assetStatusData", assetCountByStatus);
        
        Map<String, Long> totalPriceByDepartment = assetService.getTotalPriceByDepartment();
        model.addAttribute("priceDistributionData", totalPriceByDepartment);
    	
    	
    	return "adminDashboard";
    }

}