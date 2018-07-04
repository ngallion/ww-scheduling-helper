package org.launchcode.whichwichcontactlist.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class JobTitle {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany
    @JoinColumn(name = "job_title_id")
    private List<Employee> employee = new ArrayList<>();

    public JobTitle() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
