package com.example.report;

import com.example.timesheet.CommissionedEmployeeTimesheet;
import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.HourlyEmployeeTimesheet;
import com.example.timesheet.SalariedEmployeeTimesheet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HourlyEmployeeReportExtensionTest extends AbstractPayReportTest {

    @Test
    void addsHoursAndRateColumns() {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new HourlyEmployeeReportExtension());
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        final String headerLine = text.split(System.lineSeparator())[0].trim();
        final String multiSpace = "\\s{2,}";
        String[] headers = headerLine.split(multiSpace);
        assertArrayEquals(new String[]{"Name", "Class", "Hours", "Rate", "Weekly Pay"}, headers);
        String[] lines = text.split(System.lineSeparator());
        assertTrue(lines[2].contains(" 36 "), "Expected line to contain hours. Actual: " + lines[2]);
        assertTrue(lines[2].contains(" $14.50 "), "Expected line to contain hourly rate. Actual: " + lines[2]);
        assertTotalMatches(timesheets, text);
    }

}