package org.office.employee;


public class Manager extends Employee {
    private final String department;

    public Manager(String position, int id, String name, double salary, String department) {
        super(position, id, name, salary);
        this.department = department;
    }

    public String getDepartment() { return department; }
}