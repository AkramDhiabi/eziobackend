package com.gemalto.eziomobile.demo.dao.signdatamaster;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.SignDataInfo;

@Repository
public interface SigndatamasterDao extends CrudRepository<SignDataInfo, Integer>{
	
	SignDataInfo findSigndataByUserId(int userId);
	
	void deleteSigndataByUserId(int userId);
	
	void deleteSigndataByStatusAndUserId(int status, int userId);
}
