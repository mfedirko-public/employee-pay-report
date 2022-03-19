package com.example.report;

import com.example.timesheet.CommissionedEmployeeTimesheet;
import com.example.timesheet.EmployeeTimesheet;

import java.util.Collections;
import java.util.List;

import static com.example.report.EmployeePayReport.MONEY_FORMAT;

public class CommissionedEmployeeReportExtension implements EmployeePayReportExtension {
    private static final String HEADER_SALES = "Sales";

    @Override
    public boolean supportsCell(String header, EmployeeTimesheet line) {
        return line instanceof CommissionedEmployeeTimesheet
                && HEADER_SALES.equals(header);
    }

    @Override
    public Object getCellValue(String header, EmployeeTimesheet line) {
        CommissionedEmployeeTimesheet emp = (CommissionedEmployeeTimesheet)line;
        if (HEADER_SALES.equals(header)) {
            return MONEY_FORMAT.format(emp.getTotalWeeklySales());
        }
        throw new IllegalStateException("Unsupported header: " + header);
    }

    @Override
    public List<TableHeader> getHeaders(String employeeType) {
        if (!CommissionedEmployeeTimesheet.COMMISSIONED_EMPLOYEE.equals(employeeType)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(
                new TableHeader(HEADER_SALES, 3, 15)
        );
    }
}
