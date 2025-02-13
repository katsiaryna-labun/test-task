package org.office.employee;

public class Worker extends Employee{
    private final int managerId;

    public Worker(String position, int id, String name, double salary, int managerId) {
        super(position, id, name, salary);
        this.managerId = managerId;
    }

    public int getManagerId() { return managerId; }
}