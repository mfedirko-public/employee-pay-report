package com.example.report;

import com.example.timesheet.EmployeeTimesheet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractPayReportTest {
    protected OutputStream out = new ByteArrayOutputStream();


    protected void validateRow(String name, String empClass, String row) {
        assertTrue(row.contains(name), "Expected row to contain name of " + name + ". Actual: \n" + row);
        assertTrue(row.contains(empClass), "Expected row to contain class of " + empClass + ". Actual: \n" + row);
        assertTrue(row.contains(empClass), "Expected row to contain weekly pay of " + "$" + ". Actual: \n" + row);
    }

    protected void assertTotalMatches(List<EmployeeTimesheet> timesheets, String text) {
        BigDecimal total = BigDecimal.ZERO;
        for (EmployeeTimesheet ts : timesheets) {
            total = total.add(ts.getPayAmount());
        }
        String expectedTotalStr = NumberFormat.getCurrencyInstance(Locale.US).format(total);
        assertTotalEquals(expectedTotalStr, text);
    }

    protected void assertTotalEquals(String total, String text) {
        assertTrue(text.contains("TOTAL " + total),
                "Expected Total of " + total + ". Actual: \n" + text);
    }

    protected void assertHasDefaultHeaders(String text) {
        final String headerLine = text.split(System.lineSeparator())[0].trim();
        final String multiSpace = "\\s{2,}";
        String[] headers = headerLine.split(multiSpace);
        assertArrayEquals(new String[]{"Name", "Class", "Weekly Pay"}, headers,
                "Expected default headers. Actual: " + headerLine);

    }
}
