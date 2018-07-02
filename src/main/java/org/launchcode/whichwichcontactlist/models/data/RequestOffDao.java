package org.launchcode.whichwichcontactlist.models.data;

import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Date;

@Repository
@Transactional
public interface RequestOffDao extends CrudRepository<RequestOff, Integer> {
    Iterable<RequestOff> findByEmployeeId(Integer id);
    Iterable<RequestOff> findByDate(Date date);
    Iterable<RequestOff> findAllByOrderByDate();
    Iterable<RequestOff> findByEmployeeIdOrderByDate(Integer id);
}
