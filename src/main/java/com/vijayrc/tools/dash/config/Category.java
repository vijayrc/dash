package com.vijayrc.tools.dash.config;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
public class Category implements Comparable {
    public String name;
    public String unit;
    public String fields;

    public Category(String name, String unit, String fields) {
        this.name = name;
        this.unit = unit;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Category [name=" + name + "unit=" + unit + "|fields=" + fields + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFields() {
        return fields;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((Category) o).name);
    }
}
