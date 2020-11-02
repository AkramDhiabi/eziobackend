package com.gemalto.eziomobile.demo.service.p2pmaster;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.P2PMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface P2PmasterService {

	P2PMasterDTO findP2pInfoByUserId(int userId) throws ServiceException;
	
	void deleteP2pInfoByUserId(int userId) throws ServiceException;
}
