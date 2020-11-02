package com.gemalto.eziomobile.demo.service.p2pmaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dao.p2pmaster.P2PmasterDao;
import com.gemalto.eziomobile.demo.dto.P2PMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.P2pInfo;

@Service
public class P2PmasterServiceImpl implements P2PmasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(P2PmasterServiceImpl.class.getClass());
	
	@Autowired
	private P2PmasterDao p2pDao;

	@Override
	public P2PMasterDTO findP2pInfoByUserId(int userId) throws ServiceException {
		P2PMasterDTO p2pInfoDTO = null;
		try {
			P2pInfo p2pInfo = p2pDao.findP2pInfoByUserId(userId);
			
			if(p2pInfo != null){
				p2pInfoDTO = new P2PMasterDTO();
				
				p2pInfoDTO.setUserId(p2pInfo.getUserId());
				p2pInfoDTO.setBenificiaryUserId(p2pInfo.getBenificiary_UserId());
				p2pInfoDTO.setAmount(p2pInfo.getAmount());
				p2pInfoDTO.setFromAccountNo(p2pInfo.getFromAccountNo());
				p2pInfoDTO.setChallenge(p2pInfo.getChallenge());
				p2pInfoDTO.setMsg(p2pInfo.getMsg());
			}
			
		} catch (Exception e) {
			logger.error("Exception : Unable to find p2p data for userId : "+userId, e);
			throw new ServiceException(e);
		}
		return p2pInfoDTO;
	}

	
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteP2pInfoByUserId(int userId) throws ServiceException {
		
		try {
			p2pDao.deleteP2pInfoByUserId(userId);
		} catch (Exception e) {
			logger.error("Exception : Unable to delete p2p data for userId : "+userId, e);
			throw new ServiceException(e);
		}
		
	}

}
