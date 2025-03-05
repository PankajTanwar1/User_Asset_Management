package com.assets.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import com.assets.model.Asset;

public interface AssetService {

    List<Asset> getAllAssets();
    
    List<Asset> getAssetsByDateRange(Date startDate, Date endDate);
    
    Optional<Asset> getAssetById(Long id);
    
    Asset saveAsset(Asset asset);
    
    void deleteAsset(Long id);
    
    Page<Asset> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

	Page<Asset> searchAsset(String keyword, int pageNo, int pageSize, String sortField, String sortDir); 

	long countTotalAssets();
	
	Map<String, Long> getAssetsGroupedByWeek();
	
	Map<String, Long> getAssetsGroupedByMonth();
	
	Map<String, Long> getAssetsGroupedByYear();
	
	Map<String, Long> getAssetCountByStatus();
	
	Map<String, Long> getTotalPriceByDepartment();
}