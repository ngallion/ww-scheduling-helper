package org.launchcode.whichwichcontactlist.models;


import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
public class RequestOff {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Employee employee;

    private Date date;

    private Time startTime;

    private Time endTime;

    public RequestOff() {
    }

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
