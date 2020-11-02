package com.gemalto.eziomobile.demo.service.groupmaster;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface GroupmasterService {
	
	String findGroupNameByGroupId(int groupId) throws ServiceException;

}
