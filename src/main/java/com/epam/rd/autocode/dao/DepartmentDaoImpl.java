package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao{

    private final ConnectionSource connection = ConnectionSource.instance();

    private final String GET_BY_ID = "SELECT * FROM department WHERE id=?";

    private final String GET_ALL = "SELECT * FROM department";

    private final String INSERT = "INSERT INTO department (NAME,LOCATION,ID) VALUES (?,?,?)";

    private final  String UPDATE = "UPDATE department SET NAME=?, LOCATION=? WHERE id=?";

    private final String DELETE = "DELETE FROM department WHERE id = ?";
    @Override
    public Optional<Department> getById(BigInteger Id) {
        Department department = null;
        try(Connection conn = connection.createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, Id.intValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                department = getDepartment(resultSet);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.ofNullable(department);
    }

    @Override
    public List<Department> getAll() {
        List<Department> departments = new ArrayList<>();
        try(Connection conn = connection.createConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(GET_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                departments.add(getDepartment(resultSet));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return departments;
    }

    @Override
    public Department save(Department department) {
        PreparedStatement preparedStatement;
        try(Connection conn = connection.createConnection()) {
            if(getById(department.getId()).isPresent()){
                preparedStatement = conn.prepareStatement(UPDATE);
            }
            else {
                preparedStatement = conn.prepareStatement(INSERT);
            }
            preparedStatement.setString(1,department.getName());
            preparedStatement.setInt(3,department.getId().intValue());
            preparedStatement.setString(2,department.getLocation());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return department;
    }

    @Override
    public void delete(Department department) {
        try(Connection conn = connection.createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(DELETE)){
            preparedStatement.setInt(1,department.getId().intValue());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private Department getDepartment(ResultSet resultSet) throws SQLException{
            String location = resultSet.getString("LOCATION");
            BigInteger bigInteger = BigInteger.valueOf(resultSet.getInt("ID"));
            String name = resultSet.getString("NAME");
        return new Department(bigInteger,name,location);
    }
}
