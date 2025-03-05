package com.assets.repository;

import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assets.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

	Asset findByModelNo(String modelNo);
	
	@Query("SELECT a FROM Asset a WHERE a.name LIKE %:keyword% OR a.modelNo LIKE %:keyword%  OR a.department LIKE %:keyword% OR a.employee LIKE %:keyword% OR a.status LIKE %:keyword%")
	Page<Asset> searchAsset(@Param("keyword") String keyword, Pageable pageable);

	List<Asset> findAllByCreatedDateBetween(Date startDate, Date endDate);
}
