package com.example.report;

import com.example.timesheet.CommissionedEmployeeTimesheet;
import com.example.timesheet.EmployeeTimesheet;
import com.example.timesheet.HourlyEmployeeTimesheet;
import com.example.timesheet.SalariedEmployeeTimesheet;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeePayReportTest extends AbstractPayReportTest {

    @Test
    void noTimeSheets() throws Exception {
        final List<EmployeeTimesheet> timesheets = Collections.emptyList();

        EmployeePayReport rpt = new EmployeePayReport(timesheets);
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        assertHasDefaultHeaders(text);
        assertTotalEquals("$0.00", text);
    }

    @Test
    void multipleSameClass() throws Exception {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(542199, 2)));
        timesheets.add(new SalariedEmployeeTimesheet("Jane", "Doe", BigDecimal.valueOf(104312, 2)));
        timesheets.add(new SalariedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(254000, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets);
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        String[] lines = text.split(System.lineSeparator());
        assertHasDefaultHeaders(lines[0]);
        validateRow("John Doe", "Salaried", lines[1]);
        validateRow("Jane Doe", "Salaried",  lines[2]);
        validateRow("Jose Gomez", "Salaried",  lines[3]);
        assertTotalMatches(timesheets, text);
    }

    @Test
    void multipleDifferentClasses() throws Exception {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets);
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        String[] lines = text.split(System.lineSeparator());
        assertHasDefaultHeaders(lines[0]);
        validateRow("John Doe", "Salaried",  lines[1]);
        validateRow("Jane Doe", "Hourly",  lines[2]);
        validateRow("Jose Gomez", "Commissioned",  lines[3]);
        assertTotalMatches(timesheets, text);
    }

    @Test
    void extensionAddsToDefaults() {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        final String expectedCellValue = "My header value!";

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new EmployeePayReportExtension() {

                    @Override
                    public boolean supportsCell(String header, EmployeeTimesheet line) {
                        return "MyHdr".equals(header);
                    }

                    @Override
                    public Object getCellValue(String header, EmployeeTimesheet line) {
                        return expectedCellValue;
                    }

                    @Override
                    public List<TableHeader> getHeaders() {
                        return Collections.singletonList(
                                new TableHeader("MyHdr", 7, 50));
                    }
                });
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        final String headerLine = text.split(System.lineSeparator())[0].trim();
        final String multiSpace = "\\s{2,}";
        String[] headers = headerLine.split(multiSpace);
        assertArrayEquals(new String[]{"Name", "Class", "MyHdr", "Weekly Pay"}, headers);
        Arrays.stream(text.split(System.lineSeparator()))
                .skip(1)
                .limit(3)
                .forEach(row -> assertTrue(row.contains(expectedCellValue)));
        assertTotalMatches(timesheets, text);
    }

    @Test
    void extensionOverridesDefaults() {
        List<EmployeeTimesheet> timesheets = new ArrayList<>();
        timesheets.add(new SalariedEmployeeTimesheet("John", "Doe", BigDecimal.valueOf(5421_99, 2)));
        timesheets.add(new HourlyEmployeeTimesheet("Jane", "Doe", 36, BigDecimal.valueOf(14_50, 2)));
        timesheets.add(new CommissionedEmployeeTimesheet("Jose", "Gomez", BigDecimal.valueOf(250_000_00, 2)));

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new EmployeePayReportExtension() {

                    @Override
                    public boolean supportsCell(String header, EmployeeTimesheet line) {
                        return "Name".equals(header)
                                || "Class".equals(header) && line instanceof SalariedEmployeeTimesheet;
                    }

                    @Override
                    public Object getCellValue(String header, EmployeeTimesheet line) {
                        if ("Name".equals(header)) {
                            return line.getLastName() + ", " + line.getFirstName();
                        } else {
                            return line.getEmployeeType().toUpperCase();
                        }
                    }

                    @Override
                    public List<TableHeader> getHeaders() {
                        return Arrays.asList(
                            new TableHeader("Name", 0, 80),
                            new TableHeader("Class", 1, 25)
                        );
                    }
                });
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        String[] lines = text.split(System.lineSeparator());
        assertHasDefaultHeaders(lines[0]);
        validateRow("Doe, John", "SALARIED",  lines[1]);
        validateRow("Doe, Jane", "Hourly",  lines[2]);
        validateRow("Gomez, Jose", "Commissioned",  lines[3]);
        assertTotalMatches(timesheets, text);
    }

    @Test
    public void whenExtensionHeader_andNoData_thenExtensionHeaderVisible() {
        List<EmployeeTimesheet> timesheets = Collections.emptyList();

        EmployeePayReport rpt = new EmployeePayReport(timesheets)
                .withExtension(new EmployeePayReportExtension() {

                    @Override
                    public boolean supportsCell(String header, EmployeeTimesheet line) {
                        return "MyHdr".equals(header);
                    }

                    @Override
                    public Object getCellValue(String header, EmployeeTimesheet line) {
                        return "My header value!";
                    }

                    @Override
                    public List<TableHeader> getHeaders() {
                        return Collections.singletonList(
                                new TableHeader("MyHdr", 7, 50));
                    }
                });
        rpt.printHeader(out);
        rpt.printRows(out);
        rpt.printFooter(out);

        final String text = out.toString();
        final String headerLine = text.split(System.lineSeparator())[0].trim();
        final String multiSpace = "\\s{2,}";
        String[] headers = headerLine.split(multiSpace);
        assertArrayEquals(new String[]{"Name", "Class", "MyHdr", "Weekly Pay"}, headers);
        assertTotalMatches(timesheets, text);
    }



}