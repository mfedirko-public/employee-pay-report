package com.example.report;

import com.example.timesheet.EmployeeTimesheet;

import java.util.Collections;
import java.util.List;

public interface EmployeePayReportExtension {
    default boolean supportsCell(String header, EmployeeTimesheet line) {
        return false;
    }
    default Object getCellValue(String header, EmployeeTimesheet line) {
        throw new IllegalStateException("Cell value not implemented for header: " + header);
    }
    default List<TableHeader> getHeaders() {
        return Collections.emptyList();
    }
    default String getAdditionalFooter() {
        return "";
    }
}
