package com.example.timesheet;

import java.math.BigDecimal;


public abstract class EmployeeTimesheet {
    protected final String firstName;
    protected final String lastName;
    protected final int numHoursWorked;

    protected EmployeeTimesheet(String firstName, String lastName, int numHoursWorked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numHoursWorked = numHoursWorked;
    }


    public abstract String getEmployeeType();
    public abstract BigDecimal getPayAmount();


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public int getNumHoursWorked() {
        return numHoursWorked;
    }

}
