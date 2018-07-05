package org.launchcode.whichwichcontactlist.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

    private final Date dateSubmitted;

    private boolean isActive;

    public RequestOff() {
        this.dateSubmitted = Date.valueOf(LocalDate.now());
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

    public String getHumanReadableDate() {

        String humanReadableDate = this.date.toLocalDate().getDayOfWeek().toString().substring(0,3) + ", " +
                this.date.toLocalDate().getMonth().toString() + " " +
                this.date.toLocalDate().getDayOfMonth();

        return humanReadableDate;

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public Time getStartTime() {
        return startTime;
    }

    public String getHumanReadableStartTime() {

        String humanReadableStartTime = this.startTime.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));

        return humanReadableStartTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public String getHumanReadableEndTime() {

        String humanReadableEndTime = this.endTime.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));

        return humanReadableEndTime;

    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestOff that = (RequestOff) o;
        return isActive == that.isActive &&
                Objects.equals(employee.getId(), that.employee.getId()) &&
                Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, employee, date, startTime, endTime, isActive);
    }
}
