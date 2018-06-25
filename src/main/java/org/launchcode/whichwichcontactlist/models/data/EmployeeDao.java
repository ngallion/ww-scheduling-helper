package org.launchcode.whichwichcontactlist.models.data;

import org.launchcode.whichwichcontactlist.models.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface EmployeeDao extends CrudRepository<Employee, Integer> {
    Employee findByEmail(String email);
}
