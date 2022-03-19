package com.example.report;

import java.util.Objects;

class TableHeader {
    private final String name;
    private final int position;
    private final int columnWidth;

    TableHeader(String name, int position, int columnWidth) {
        this.name = name;
        this.position = position;
        this.columnWidth = Math.max(name.length() + 1, columnWidth);
    }

    public String getName() {
        return name;
    }

    // position of the header from left to right.
    public int getPosition() {
        return position;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableHeader that = (TableHeader) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
