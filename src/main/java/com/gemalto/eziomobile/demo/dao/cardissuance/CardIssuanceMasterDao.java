package com.gemalto.eziomobile.demo.dao.cardissuance;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.CardIssuanceInfo;

@Repository
public interface CardIssuanceMasterDao extends CrudRepository<CardIssuanceInfo, Integer>{
	
	int countByUserId(int userId);
	
	List<CardIssuanceInfo> findCardsByUserId(int userId);
	
	CardIssuanceInfo findCardInfoByUserIdAndPanNo(int userId, String panNo);
	
	void deleteCardInfoByUserIdAndPanNo(int userId, String panNo);

	void deleteCardIssuanceInfoByUserId(int uId);
}
