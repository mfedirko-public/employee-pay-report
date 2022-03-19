package com.example.report;

import com.example.timesheet.CommissionedEmployeeTimesheet;
import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.HourlyEmployeeTimesheet;
import com.example.timesheet.SalariedEmployeeTimesheet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalariedEmployeeReportExtensionTest extends AbstractPayReportTest {
    @Test
    void whenNoBonus_thenSameAsDefaults() {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new SalariedEmployeeReportExtension());
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        String[] lines = text.split(System.lineSeparator());
        assertHasDefaultHeaders(lines[0]);
        validateRow("John Doe", "Salaried",  lines[1]);
        assertFalse(lines[1].trim().endsWith("*"),
                "Did not expected '*' to be added to salary");
        validateRow("Jane Doe", "Hourly",  lines[2]);
        validateRow("Jose Gomez", "Commissioned",  lines[3]);
        assertTotalMatches(timesheets, text);
    }

    @Test
    void whenSalariedBonus_thenAsteriskNextToPay() {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2))
            .addBonusPercentage(BigDecimal.valueOf(10, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new SalariedEmployeeReportExtension());
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        String[] lines = text.split(System.lineSeparator());
        assertHasDefaultHeaders(lines[0]);
        validateRow("John Doe", "Salaried",  lines[1]);
        assertTrue(lines[1].trim().endsWith("*"),
                "Expected '*' to be added to salary");
        validateRow("Jane Doe", "Hourly",  lines[2]);
        validateRow("Jose Gomez", "Commissioned",  lines[3]);
        assertTotalMatches(timesheets, text);
    }
}