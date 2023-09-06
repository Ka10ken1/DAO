package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao{
    private final String GET_BY_ID = "SELECT * FROM Employee WHERE ID=?";

    private final String GET_ALL = "SELECT * FROM Employee";

    private final String INSERT = "INSERT INTO Employee " +
            "(id,firstname,lastname,middlename,position,manager,hiredate,salary,department)" +
            "VALUES (?,?,?,?,?,?,?,?,?)";

    private final String DELETE = "DELETE FROM Employee WHERE ID=?";

    private final String GET_BY_DEPARTMENT = "SELECT e.* FROM Employee e WHERE e.DEPARTMENT=?";

    private final String GET_BY_MANAGER = "SELECT e.* FROM Employee e WHERE e.MANAGER=?";

    private final ConnectionSource connection = ConnectionSource.instance();
    @Override
    public Optional<Employee> getById(BigInteger Id) {
        Employee employee = null;
        try(Connection conn = connection.createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_ID)){
            preparedStatement.setInt(1, Id.intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                employee = getEmployee(resultSet);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
      return   Optional.ofNullable(employee);
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try(Connection conn = connection.createConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(GET_ALL)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                employees.add(getEmployee(resultSet));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public Employee save(Employee employee) {
        PreparedStatement preparedStatement;
        try(Connection conn = connection.createConnection()) {
            preparedStatement = conn.prepareStatement(INSERT);
            preparedStatement.setInt(1,employee.getId().intValue());
            preparedStatement.setString(2,employee.getFullName().getFirstName());
            preparedStatement.setString(3,employee.getFullName().getLastName());
            preparedStatement.setString(4,employee.getFullName().getMiddleName());
            preparedStatement.setString(5,employee.getPosition().toString());
            preparedStatement.setInt(6,employee.getManagerId().intValue());
            Date date = Date.valueOf(employee.getHired());
            preparedStatement.setDate(7,date);
            preparedStatement.setDouble(8,employee.getSalary().doubleValue());
            preparedStatement.setInt(9,employee.getDepartmentId().intValue());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return employee;
    }

    @Override
    public void delete(Employee employee) {
        try(Connection conn = connection.createConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(DELETE)){
            preparedStatement.setInt(1,employee.getId().intValue());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        List<Employee> employees = new ArrayList<>();
        try(Connection conn = connection.createConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_DEPARTMENT)){
            preparedStatement.setInt(1,department.getId().intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employees.add(getEmployee(resultSet));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public List<Employee> getByManager(Employee employee) {
        List<Employee> employees = new ArrayList<>();
        try(Connection conn = connection.createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_MANAGER)){
            preparedStatement.setInt(1,employee.getId().intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employees.add(getEmployee(resultSet));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return employees;
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
        BigInteger Id = BigInteger.valueOf(resultSet.getInt("ID"));

        String firstName = resultSet.getString("FIRSTNAME");
        String lastName = resultSet.getString("LASTNAME");
        String middleName = resultSet.getString("MIDDLENAME");
        FullName fullName = new FullName(firstName,lastName,middleName);

        Position position = Position.valueOf(resultSet.getString("POSITION"));

        LocalDate localDate = resultSet.getDate("HIREDATE").toLocalDate();

        BigDecimal salary = resultSet.getBigDecimal("SALARY");

        BigInteger managerId = BigInteger.valueOf(resultSet.getInt("MANAGER"));

        BigInteger departmentId = BigInteger.valueOf(resultSet.getInt("DEPARTMENT"));

        return new Employee(Id,fullName,position,localDate,salary,managerId,departmentId);

    }

}
