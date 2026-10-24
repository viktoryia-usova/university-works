package org.example.Server.Service;

import org.example.Server.DAO.StudentDAO;
import org.example.Server.DAO.TeacherDAO;
import org.example.Server.Entities.Teacher;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class TeacherService implements Service<Teacher> {
    private TeacherDAO teacherDAO;
    public TeacherService() {
        teacherDAO = new TeacherDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Teacher teacher) {
        teacherDAO.insert(teacher);
    }

    @Override
    public void update(Teacher teacher) {
        teacherDAO.update(teacher);
    }

    @Override
    public void delete(int id) {
        teacherDAO.delete(id);
    }

    @Override
    public Teacher findById(int id) {
        Teacher teacher = teacherDAO.find(id);
        return teacher;
    }
    public List<Integer> getAllTeachersIds() {
        return teacherDAO.findAllTechersIds();
    }
    @Override
    public List<Teacher> findAll() {
        return teacherDAO.findAll();
    }
    public Integer findProfileIdByTeacherId(int teacherId) {
        return teacherDAO.findProfileIdByTeacherId(teacherId);
    }
}
