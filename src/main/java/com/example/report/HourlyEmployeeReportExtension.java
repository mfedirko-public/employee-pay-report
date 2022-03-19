package com.example.report;

import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.HourlyEmployeeTimesheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.report.EmployeePayReport.MONEY_FORMAT;

public class HourlyEmployeeReportExtension implements EmployeePayReportExtension {
    private static final String HEADER_HOURS = "Hours";
    private static final String HEADER_RATE = "Rate";

    @Override
    public boolean supportsCell(String header, EmployeeTimesheet line) {
        return line instanceof HourlyEmployeeTimesheet
                && (HEADER_HOURS.equals(header) || HEADER_RATE.equals(header));
    }


    @Override
    public Object getCellValue(String header, EmployeeTimesheet line) {
        switch (header) {
            case HEADER_HOURS:
                return line.getNumHoursWorked();
            case HEADER_RATE:
                HourlyEmployeeTimesheet emp = (HourlyEmployeeTimesheet)line;
                return MONEY_FORMAT.format(emp.getHourlyRate());
            default:
                throw new IllegalStateException("Unsupported header: " + header);
        }
    }

    @Override
    public List<TableHeader> getHeaders() {
        List<TableHeader> headers = new ArrayList<>();
        headers.add(new TableHeader(HEADER_HOURS, 2, 8));
        headers.add(new TableHeader(HEADER_RATE, 2, 15));
        return headers;
    }
}
