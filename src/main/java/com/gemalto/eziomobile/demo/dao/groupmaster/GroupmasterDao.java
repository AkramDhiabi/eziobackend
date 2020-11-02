package com.gemalto.eziomobile.demo.dao.groupmaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.GroupMasterInfo;

@Repository
public interface GroupmasterDao extends CrudRepository<GroupMasterInfo, Integer>{

}
