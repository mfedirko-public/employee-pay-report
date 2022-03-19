package com.example.timesheet;

import java.math.BigDecimal;

public class CommissionedEmployeeTimesheet extends EmployeeTimesheet {
    public static final String COMMISSIONED_EMPLOYEE = "Commissioned";
    private static final BigDecimal COMMISSION_RATE = BigDecimal.valueOf(20, 2); // 0.25

    private final BigDecimal totalWeeklySales;

    public CommissionedEmployeeTimesheet(String firstName, String lastName, BigDecimal totalWeeklySales) {
        super(firstName, lastName, 40);
        this.totalWeeklySales = totalWeeklySales;
    }


    public BigDecimal getTotalWeeklySales() {
        return totalWeeklySales;
    }

    @Override
    public String getEmployeeType() {
        return COMMISSIONED_EMPLOYEE;
    }

    @Override
    public BigDecimal getPayAmount() {
        return totalWeeklySales.multiply(COMMISSION_RATE);
    }
}
