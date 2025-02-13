package org.office;

import org.office.department.Department;
import org.office.employee.*;
import org.office.exception.*;


import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {
    private static final List<Manager> managers = new ArrayList<Manager>();
    private static final List<Worker> workers = new ArrayList<Worker>();
    private static final List<String> errors = new ArrayList<String>();
    private static final List<Department> departments = new ArrayList<Department>();

    private static String inputFile = "";
    private static String sort = "";
    private static String order = "";
    private static String output = "";
    private static String path = "";

    private final static List<String> sortingTypes = Arrays.asList("name", "salary");
    private final static List<String> orderTypes = Arrays.asList("asc", "desc");
    private final static List<String> outputTypes = Arrays.asList("console", "file");
    private final static List<String> employeePositions = Arrays.asList("manager", "employee");


    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", "input", true, "Input File");
        options.addOption("s", "sort", true, "Sorting");
        options.addOption("o", "order", true, "Order of sorting");
        options.addOption("o", "output", true, "Type of output");
        options.addOption("p", "path", true, "Path to output file");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("input")) inputFile = cmd.getOptionValue("input").trim();

            if (cmd.hasOption("sort")) sort = cmd.getOptionValue("sort");
            if (!sort.isEmpty() && !sortingTypes.contains(sort)) {
                throw new SortingParametersException("Wrong value of sorting type.");
            }

            if (cmd.hasOption("order")) order = cmd.getOptionValue("order");
            if (sort.isEmpty() && !order.isEmpty()) {
                throw new SortingParametersException("Order can not be specified without type of sorting.");
            }
            if (!sort.isEmpty() && order.isEmpty()) {
                throw new SortingParametersException("Sorting can not be specified without order type.");
            }
            if (!sort.isEmpty() && !orderTypes.contains(order)) {
                throw new SortingParametersException("Wrong value of order.");
            }

            if (cmd.hasOption("output")) output = cmd.getOptionValue("output");
            if (!output.isEmpty() && !outputTypes.contains(output))
            {
                throw new OutputParametersException("Wrong value of output type.");
            }

            if (cmd.hasOption("path")) path = cmd.getOptionValue("path").trim();
            if (!output.equals("file") && !path.isEmpty()) {
                throw new OutputParametersException("Path can not be specified without --output=file.");
            }
            if (output.equals("file") && path.isEmpty()) {
                throw new OutputParametersException("Writing to file can not be specified without path.");
            }

            processInputFile(inputFile);


            for (Manager manager:managers) {
                String departmentName = manager.getDepartment();
                int managerId = manager.getId();
                List<Worker> workersInDepartment = workers.stream()
                        .filter(w -> w.getManagerId() == managerId)
                        .toList();

                Department department = new Department(departmentName, manager, workersInDepartment);
                departments.add(department);
            }

            departments.sort(Comparator.comparing(Department::getDepartmentName));

            if (!sort.isEmpty()) {
                for (Department department:departments)
                    department.sortDepartment(sort, order);
            }

            if (!output.equals("file")) printOutputData();
            else {
                System.out.println("Writing output to file.");
                writeOutputFile(path);
            };

        } catch (ParseException|SortingParametersException|OutputParametersException|IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void processInputFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Set<Integer> ids = new HashSet<>();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 5) {
                    errors.add(line);
                    continue;
                }

                try {
                    String position = data[0].trim();
                    if (!employeePositions.contains(position.toLowerCase()))
                    {
                        errors.add(line);
                        continue;
                    }

                    int id = Integer.parseInt(data[1].trim());

                    if (ids.contains(id)) {
                        errors.add(line);
                        continue;
                    } else {
                        ids.add(id);
                    }

                    String name = data[2].trim();
                    double salary = Double.parseDouble(data[3].trim());
                    String identifier = data[4].trim();

                    if (salary < 0) {
                        errors.add(line);
                        continue;
                    }

                    if (position.equalsIgnoreCase("manager")) {
                        Manager manager = new Manager(position, id, name, salary, identifier);
                        managers.add(manager);
                    } else {
                        int managerId = Integer.parseInt(identifier);
                        Worker worker = new Worker(position, id, name, salary, managerId);
                        workers.add(worker);
                    }

                } catch (NumberFormatException e) {
                    errors.add(line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage());
        }
    }


    private static void writeOutputFile(String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            String line;
            for (Department department:departments) {
                writer.write(department.getDepartmentName());
                writer.newLine();

                Manager manager = department.getManager();
                line = manager.getEmployeeInfo();
                writer.write(line);
                writer.newLine();

                List<Worker> workersInDepartment = department.getWorkers();
                for (Worker worker : workersInDepartment) {
                    line = worker.getEmployeeInfo();
                    writer.write(line);
                    writer.newLine();
                }
                line = department.getDepartmentStats();
                writer.write(line);
                writer.newLine();
            }
            writer.write("Некорректные данные:");
            writer.newLine();
            for (String err: errors) {
                writer.write(err);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage());
        }
    }

    private static void printOutputData() {
        for (Department department : departments) {
            department.printDepartmentInfo();
        }
        System.out.println("Некорректные данные");
        for (String err: errors) {
            System.out.println(err);
        }
    }

}
