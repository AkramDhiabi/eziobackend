package com.gemalto.eziomobile.demo.service.groupmaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dao.groupmaster.GroupmasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.GroupMasterInfo;

import java.util.Optional;

@Service
public class GroupmasterServiceImpl implements GroupmasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(GroupmasterServiceImpl.class.getClass());

	@Autowired
	GroupmasterDao groupMasterDao;
	
	@Override
	public String findGroupNameByGroupId(int groupId) throws ServiceException{
		String groupName = null;
		try{
			Optional<GroupMasterInfo> groupMasterInfo =  groupMasterDao.findById(groupId);
			if(groupMasterInfo.isPresent()) {
				groupName = groupMasterInfo.get().getGroupName();
				logger.info("groupName: " + groupName);
			}
		}catch(Exception e){
			logger.error("Exception occurred in GroupServiceImpl - findGroupNameByGroupId");
			throw new ServiceException();
		}
		
		return groupName;
	}

}
