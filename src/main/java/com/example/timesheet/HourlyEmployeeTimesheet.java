package com.example.timesheet;

import java.math.BigDecimal;

public class HourlyEmployeeTimesheet extends EmployeeTimesheet {
    public static final String HOURLY_EMPLOYEE = "Hourly";

    private final BigDecimal hourlyRate;

    public HourlyEmployeeTimesheet(String firstName, String lastName, int numHoursWorked,
                                   BigDecimal hourlyRate) {
        super(firstName, lastName, numHoursWorked);
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    @Override
    public String getEmployeeType() {
        return HOURLY_EMPLOYEE;
    }

    @Override
    public BigDecimal getPayAmount() {
        return getBaseTimePay().add(getOverTimePay());
    }

    private BigDecimal getBaseTimePay() {
        final int hours = Math.min(numHoursWorked, 40);
        return hourlyRate.multiply(BigDecimal.valueOf(hours));
    }
    private BigDecimal getOverTimePay() {
        final int hours = Math.max(0, numHoursWorked - 40);
        return hourlyRate.multiply(BigDecimal.valueOf(2))
                .multiply(BigDecimal.valueOf(hours));
    }
}
