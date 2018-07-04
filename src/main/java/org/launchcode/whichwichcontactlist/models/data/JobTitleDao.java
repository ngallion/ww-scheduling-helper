package org.launchcode.whichwichcontactlist.models.data;


import org.launchcode.whichwichcontactlist.models.JobTitle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface JobTitleDao extends CrudRepository<JobTitle, Integer> {
}
