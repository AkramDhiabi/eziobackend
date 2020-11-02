package com.gemalto.eziomobile.demo.dao.p2pmaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.P2pInfo;

@Repository
public interface P2PmasterDao extends CrudRepository<P2pInfo, Integer>{
	
	P2pInfo findP2pInfoByUserId(int userId);
	
	void deleteP2pInfoByUserId(int userId);

}
