package org.office.employee;

public abstract class Employee {
    protected String position;
    protected int id;
    protected String name;
    protected double salary;

    public Employee(String position, int id, String name, double salary) {
        this.position = position;
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public String getPosition() { return position; }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }

    public String getEmployeeInfo() { return String.format("%s,%d,%s,%.2f",
            this.getPosition(), this.getId(), this.getName(), this.getSalary());}

    public void printEmployee() {
        String employee = getEmployeeInfo();
        System.out.println(employee); }

}
