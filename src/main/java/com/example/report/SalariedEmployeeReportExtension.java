package com.example.report;

import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.SalariedEmployeeTimesheet;

import static com.example.report.EmployeePayReport.MONEY_FORMAT;

public class SalariedEmployeeReportExtension implements EmployeePayReportExtension {
    private static final String HEADER_WEEKLY_PAY = "Weekly Pay";

    @Override
    public boolean supportsCell(String header, EmployeeTimesheet line) {
        return line instanceof SalariedEmployeeTimesheet
                && HEADER_WEEKLY_PAY.equals(header);
    }

    @Override
    public Object getCellValue(String header, EmployeeTimesheet line) {
        if (HEADER_WEEKLY_PAY.equals(header)) {
            SalariedEmployeeTimesheet emp = (SalariedEmployeeTimesheet)line;
            if (emp.hasBonus()) {
                return MONEY_FORMAT.format(emp.getWeeklySalaryAmount()) + "*";
            }
            return MONEY_FORMAT.format(emp.getPayAmount());
        }
        throw new IllegalStateException("Unsupported header: " + header);
    }

    @Override
    public String getAdditionalFooter() {
        return "* - a 10% bonus is awarded.";
    }
}
