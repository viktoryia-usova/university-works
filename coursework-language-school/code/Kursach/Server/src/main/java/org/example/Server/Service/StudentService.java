package org.example.Server.Service;

import org.example.Server.DAO.ScheduleDAO;
import org.example.Server.DAO.StudentDAO;
import org.example.Server.Entities.Schedule;
import org.example.Server.Entities.Student;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class StudentService implements Service<Student> {
    private StudentDAO studentDAO;
    public StudentService() {
        studentDAO = new StudentDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Student student) {
        studentDAO.insert(student);
    }

    @Override
    public void update(Student entity) {
        studentDAO.update(entity);
    }

    @Override
    public void delete(int id) {
        studentDAO.delete(id);
    }

    @Override
    public Student findById(int id) {
        Student student = studentDAO.find(id);
        return student;
    }
    public Student findByProfileId(int profileId){
        Student student = studentDAO.findByProfileId(profileId);
        return student;
    }
    @Override
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    public List<Student> getStudentsByGroupId(int groupId){
        return studentDAO.getStudentsByGroupId(groupId);
    }
    public Integer findProfileIdByStudentId(int studentId){
        return studentDAO.findProfileIdByStudentId(studentId);
    }
    public List<Integer> getAllStudentIds() {
        return studentDAO.findAllStudentsIds();
    }
    public int getStudentCountInGroup(int groupId){
        return studentDAO.getStudentCountInGroup(groupId);
    }
    public List<Student> findStudentsWithoutGroup(){
        return studentDAO.findStudentsWithoutGroup();
    }
}
