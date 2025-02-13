package org.office.department;

import org.office.employee.*;

import java.util.*;

public class Department {
    private final String departmentName;
    private final Manager manager;
    private List<Worker> workers;
    private final int departmentSize;

    public Department(String departmentName, Manager manager, List<Worker> workers) {
        this.departmentName = departmentName;
        this.manager = manager;
        this.workers = workers;
        this.departmentSize = workers.size() + 1;
    }

    public String getDepartmentName() { return this.departmentName; }
    public Manager getManager() { return this.manager; }
    public List<Worker> getWorkers() { return this.workers; }
    public int getDepartmentSize() { return this.departmentSize; }

    private Double countAvgSalary() {
        List<Double> workersSalary = new ArrayList<>(this.workers.stream()
                .map(Worker::getSalary)
                .toList());

        Double managerSalary = this.manager.getSalary();
        workersSalary.add(managerSalary);

        return workersSalary.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    public String getDepartmentStats() { return String.format("%d,%.2f", this.departmentSize, countAvgSalary()); }

    public void sortDepartment(String sort, String order) {
        List<Worker> workers = new ArrayList<>(this.workers);
        switch (sort) {
            case ("name"):
                workers.sort(Comparator.comparing(Worker::getName));
                break;

            case ("salary"):
                workers.sort(Comparator.comparing(Worker::getSalary));
                break;
        }

        if (order.equals("desc")) Collections.reverse(workers);

        this.workers = workers;
    }

    public void printDepartmentInfo() {
        System.out.println(this.departmentName);
        this.manager.printEmployee();
        for (Worker worker:workers) worker.printEmployee();
        String stats = getDepartmentStats();
        System.out.println(stats);
    }
}
