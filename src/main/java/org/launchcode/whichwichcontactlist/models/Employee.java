package org.launchcode.whichwichcontactlist.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min = 2, max = 15, message = "First name cannot be empty")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 15, message = "Last name cannot be empty")
    private String lastName;

    @NotNull
    @Size(min = 10, max = 10, message = "Please enter a valid phone number")
    private String phoneNumber;

    @NotNull
    @Size(min = 8, max = 50, message = "Please enter a valid email address")
    private String email;

    @NotNull
    @Size(min = 8, max=300, message = "Please enter a valid password")
    private String password;

    private boolean isActive;

    @OneToMany
    @JoinColumn(name = "employee_id")
    private List<RequestOff> requestsOff = new ArrayList<>();

    @ManyToOne
    private Store store;

    @ManyToOne
    private JobTitle jobTitle;

    public Employee() {
    }

    public Employee(String firstName, String lastName, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        isActive = true;
    }

    public void setActiveToInactive() {
        isActive = false;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
