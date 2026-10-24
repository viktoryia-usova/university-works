package org.example.Server.Service;

import org.example.Server.DAO.CourseDAO;
import org.example.Server.DAO.UserDAO;
import org.example.Server.Entities.Course;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class CourseService implements Service<Course> {
    private CourseDAO courseDAO;
    public CourseService() {
        courseDAO = new CourseDAO(HibernateUtil.getSessionFactory());
    }


    @Override
    public void insert(Course course) {
        courseDAO.insert(course);
    }

    @Override
    public void update(Course course) {
        courseDAO.update(course);
    }

    @Override
    public void delete(int id) {
        courseDAO.delete(id);
    }

    @Override
    public Course findById(int id) {
        Course course = courseDAO.find(id);
        return course;
    }

    @Override
    public List<Course> findAll() {
        return courseDAO.findAll();
    }
}
