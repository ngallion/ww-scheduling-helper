package org.launchcode.whichwichcontactlist.models.data;

import org.launchcode.whichwichcontactlist.models.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface StoreDao extends CrudRepository<Store, Integer> {
    Store findOneByCode(String code);
}
