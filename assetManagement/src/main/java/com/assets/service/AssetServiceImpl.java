package com.assets.service;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.assets.model.Asset;
import com.assets.repository.AssetRepository;

@Service
public class AssetServiceImpl implements AssetService {

	@Autowired
    private AssetRepository assetRepository;
    
    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    
    @Override
    public List<Asset> getAssetsByDateRange(Date startDate, Date endDate) {
        return assetRepository.findAllByCreatedDateBetween(startDate, endDate);
    }

    @Override
    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }

    @Override
    public Asset saveAsset(Asset asset) {
        try {
            return assetRepository.save(asset);
        } catch (Exception e) {
            throw new RuntimeException("Error saving asset", e);
        }
    }
    
    @Override
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
    
    @Override
	public Page<Asset> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
																			    Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
	    return assetRepository.findAll(pageable);
	}
	
	
	 @Override
	    public Page<Asset> searchAsset(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
	        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
	        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize, sort);
	        return assetRepository.searchAsset(keyword, pageable);
	    }
	 
	 @Override
	    public long countTotalAssets() {
	        return assetRepository.count(); 
	    }
	 
	// Group by Week
	 @Override
	 public Map<String, Long> getAssetsGroupedByWeek() {
	        WeekFields weekFields = WeekFields.of(Locale.getDefault());
	        return assetRepository.findAll().stream()
	            .filter(asset -> asset.getPurchaseDate() != null)
	            .collect(Collectors.groupingBy(
	                asset -> {
	                    int week = asset.getPurchaseDate().get(weekFields.weekOfWeekBasedYear());
	                    int year = asset.getPurchaseDate().getYear();
	                    return "Week " + week + " " + year;
	                },
	                Collectors.counting()
	            ));
	    }

	 // Group by Month
	 @Override
	 public Map<String, Long> getAssetsGroupedByMonth() {
	        return assetRepository.findAll().stream()
	            .filter(asset -> asset.getPurchaseDate() != null)
	            .collect(Collectors.groupingBy(
	            		 asset -> {
	                         // Force the key to be a String explicitly
	                         String formattedDate = asset.getPurchaseDate().format(DateTimeFormatter.ofPattern("MMM yyyy"));
	                         return formattedDate;
	                     },
	                     Collectors.counting() // Count assets for each month
	                 ));
	    }

	 // Group by Year
	 @Override
	 public Map<String, Long> getAssetsGroupedByYear() {
	        return assetRepository.findAll().stream()
	            .filter(asset -> asset.getPurchaseDate() != null)
	            .collect(Collectors.groupingBy(
	                asset -> String.valueOf(asset.getPurchaseDate().getYear()),
	                Collectors.counting()
	            ));
	    }
	 
	 @Override
	 public Map<String, Long> getAssetCountByStatus() {
	     return assetRepository.findAll().stream()
	         .filter(asset -> asset.getStatus() != null) // Ensure status is not null
	         .collect(Collectors.groupingBy(
	             Asset::getStatus, // Group by the status field
	             Collectors.counting() // Count the number of assets for each status
	         ));
	 }
	 
	 @Override
	 public Map<String, Long> getTotalPriceByDepartment() {
	     return assetRepository.findAll().stream()
	         .filter(asset -> asset.getPrice() != null && asset.getDepartment() != null) // Filter out null values
	         .collect(Collectors.groupingBy(
	             Asset::getDepartment, // Group by the department field
	             Collectors.summingLong(Asset::getPrice) // Sum the prices for each department
	         ));
	 }

}
