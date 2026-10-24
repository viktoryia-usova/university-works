package org.example.Server.Service;

import org.example.Server.DAO.TeacherDAO;
import org.example.Server.DAO.TestDAO;
import org.example.Server.Entities.Test;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class TestService implements Service<Test> {
    TestDAO testDAO;
    public TestService() {
        testDAO = new TestDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Test test) {
        testDAO.insert(test);
    }

    @Override
    public void update(Test test) {
        testDAO.update(test);
    }

    @Override
    public void delete(int id) {
        testDAO.delete(id);
    }

    @Override
    public Test findById(int id) {
        Test test = testDAO.find(id);
        return test;
    }

    @Override
    public List<Test> findAll() {
       return testDAO.findAll();
    }
}
