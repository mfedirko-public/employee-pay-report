package com.example.report;

import com.example.timesheet.EmployeeTimesheet;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeePayReport {
    public static final NumberFormat MONEY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    private final List<EmployeeTimesheet> timesheets;
    private List<TableHeader> headers;
    private final List<EmployeePayReportExtension> extensions = new ArrayList<>();

    public EmployeePayReport(List<EmployeeTimesheet> timesheets) {
        this.timesheets = timesheets;
        this.extensions.add(new SalariedEmployeeReportExtension());
        this.extensions.add(new CommissionedEmployeeReportExtension());
        this.extensions.add(new HourlyEmployeeReportExtension());
    }

    public void addExtension(EmployeePayReportExtension ext) {
        this.extensions.add(ext);
    }

    public void printHeader(OutputStream os) {
        PrintWriter writer = new PrintWriter(os, true);
        getHeaders().forEach(hdr -> writer.printf(getHeaderPrintFormat(hdr), hdr.getName()));
        writer.println();
    }

    public void printRows(OutputStream os) {
        PrintWriter writer = new PrintWriter(os, true);
        for (EmployeeTimesheet line : timesheets) {
            for (TableHeader hdr : getHeaders()) {
                final Object rawValue = getCellValue(hdr, line);
                final String cellValue = rawValue == null ? "" : String.valueOf(rawValue).trim();
                writer.printf(getHeaderPrintFormat(hdr), cellValue);
            }
            writer.println();
        }
    }

    private List<TableHeader> getHeaders() {
        if (this.headers == null) {
            List<TableHeader> hdrs = new ArrayList<>();
            getEmployeeTypes(this.timesheets).forEach(empType -> addExtendedHeaders(empType, hdrs));
            addDefaultHeaders(hdrs);
            this.headers = hdrs.stream()
                    // sort by position. For any duplicate positions, sort them by name
                    .sorted(Comparator.comparingInt(TableHeader::getPosition).thenComparing(TableHeader::getName))
                    .distinct()
                    .collect(Collectors.toList());
        }
        return this.headers;
    }

    private String getHeaderPrintFormat(TableHeader hdr) {
        return "%-" + hdr.getColumnWidth() + "s";
    }

    public void printFooter(OutputStream os) {
        PrintWriter writer = new PrintWriter(os, true);
        writer.println();
        writer.printf("TOTAL %s\n", MONEY_FORMAT.format(getTotalPayInReport()));
        writer.println();
        writer.println(getFooterFromExtensions());
    }

    private BigDecimal getTotalPayInReport() {
        return timesheets.stream()
                .map(EmployeeTimesheet::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getFooterFromExtensions() {
        return extensions.stream()
                .map(EmployeePayReportExtension::getAdditionalFooter)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
    }


    private Collection<String> getEmployeeTypes(Collection<EmployeeTimesheet> timesheets) {
        return timesheets.stream()
                .map(EmployeeTimesheet::getEmployeeType)
                .collect(Collectors.toSet());
    }

    private void addExtendedHeaders(String empType, List<TableHeader> headers) {
        extensions.forEach(ext -> headers.addAll(ext.getHeaders(empType)));
    }

    private void addDefaultHeaders(List<TableHeader> headers) {
        headers.add(new TableHeader("Name", 0, 30));
        headers.add(new TableHeader("Class", 1, 20));
        headers.add(new TableHeader("Weekly Pay", 100, 10));
    }

    private Object getCellValue(TableHeader hdr, EmployeeTimesheet line) {
        return getCellValueFromExtensions(hdr, line)
            .orElseGet(() -> getDefaultCellValues(hdr, line));
    }

    private Optional<Object> getCellValueFromExtensions(TableHeader hdr, EmployeeTimesheet line) {
        return extensions.stream()
                .filter(ext -> ext.supportsCell(hdr.getName(), line))
                .findFirst()
                .map(ext -> ext.getCellValue(hdr.getName(), line));
    }

    private Object getDefaultCellValues(TableHeader hdr, EmployeeTimesheet line) {
        switch (hdr.getName()) {
            case "Name":
                return line.getFirstName() + " " + line.getLastName();
            case "Class":
                return line.getEmployeeType();
            case "Weekly Pay":
                return MONEY_FORMAT.format(line.getPayAmount());
            default:
                return "";
        }
    }
}
