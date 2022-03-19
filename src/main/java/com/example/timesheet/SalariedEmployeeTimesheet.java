package com.example.timesheet;

import java.math.BigDecimal;

public class SalariedEmployeeTimesheet extends EmployeeTimesheet {
    public static final String SALARIED_EMPLOYEE = "Salaried";

    private final BigDecimal weeklySalaryAmount;
    private BigDecimal bonusPercentage;

    public SalariedEmployeeTimesheet(String firstName, String lastName, BigDecimal weeklySalaryAmount) {
        super(firstName, lastName, 40);
        this.weeklySalaryAmount = weeklySalaryAmount;
    }

    public SalariedEmployeeTimesheet addBonusPercentage(BigDecimal bonusPercentage) {
        this.bonusPercentage = bonusPercentage;
        return this;
    }

    public boolean hasBonus() {
        return this.bonusPercentage != null;
    }

    public String getEmployeeType() {
        return "Salaried";
    }

    public BigDecimal getPayAmount() {
        if (hasBonus()) {
            return bonusPercentage.add(BigDecimal.ONE).multiply(weeklySalaryAmount);
        }
        return weeklySalaryAmount;
    }

    public BigDecimal getWeeklySalaryAmount() {
        return weeklySalaryAmount;
    }

}
