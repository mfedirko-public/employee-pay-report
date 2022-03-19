package com.example;

import com.example.report.CommissionedEmployeeReportExtension;
import com.example.report.EmployeePayReport;
import com.example.report.HourlyEmployeeReportExtension;
import com.example.report.SalariedEmployeeReportExtension;
import com.example.timesheet.CommissionedEmployeeTimesheet;
import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.HourlyEmployeeTimesheet;
import com.example.timesheet.SalariedEmployeeTimesheet;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PayReportApp {

    private static final BigDecimal BONUS_PERCENTAGE = BigDecimal.valueOf(10, 2);

    public static void main(String[] args) {
        final OutputStream os = System.out;
        EmployeePayReport report = new EmployeePayReport(getTimesheetData())
            .withExtension(new SalariedEmployeeReportExtension())
            .withExtension(new CommissionedEmployeeReportExtension())
            .withExtension(new HourlyEmployeeReportExtension());
        report.printHeader(os);
        report.printRows(os);
        report.printFooter(os);
    }

    private static List<EmployeeTimesheet> getTimesheetData() {
        return Arrays.asList(
            new SalariedEmployeeTimesheet("James", "Hogan", BigDecimal.valueOf(3300)).addBonusPercentage(BONUS_PERCENTAGE),
            new HourlyEmployeeTimesheet("Jennifer", "Waltz", 45, BigDecimal.valueOf(1095, 2)),
            new HourlyEmployeeTimesheet("Molly", "Smith", 32, BigDecimal.valueOf(1500, 2)),
            new SalariedEmployeeTimesheet("Joan", "Han", BigDecimal.valueOf(2600)),
            new CommissionedEmployeeTimesheet("Mary", "Butler", BigDecimal.valueOf(10_000))
        );

    }
}
