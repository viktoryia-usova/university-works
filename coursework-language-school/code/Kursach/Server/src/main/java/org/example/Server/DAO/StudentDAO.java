package org.example.Server.DAO;

import org.example.Server.Entities.Schedule;
import org.example.Server.Entities.Student;
import org.example.Server.Interfaces.DAO;
import org.example.Server.Utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements DAO<Student> {
    private final SessionFactory sessionFactory;
    public StudentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Student student) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Student student) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Student find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Student.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Student> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Student> query = session.createQuery("from Student", Student.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Student> getStudentsByGroupId(int groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Используем JPQL для выборки только нужных полей
            return session.createQuery(
                            "SELECT new org.example.Server.Entities.Student(p.name, p.lastname, p.phonenumber) " +
                                    "FROM Student s " +
                                    "JOIN s.profile p " +
                                    "WHERE s.group.id = :groupId", Student.class)
                    .setParameter("groupId", groupId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Student findByProfileId(int profileId) {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL для выборки студента по profile_id
            Query<Student> query = session.createQuery(
                    "FROM Student s WHERE s.profile.id = :profileId", Student.class);
            query.setParameter("profileId", profileId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getStudentCountInGroup(int groupId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(s.id) from Student s where s.group.id = :groupId", Long.class);
            query.setParameter("groupId", groupId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Integer findProfileIdByStudentId(int studentId) {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL для выборки profile_id на основе studentId
            Query<Integer> query = session.createQuery(
                    "SELECT s.profile.id FROM Student s WHERE s.id = :studentId", Integer.class);
            query.setParameter("studentId", studentId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllStudentsIds() {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL-запрос для получения всех значений course_id
            Query<Integer> query = session.createQuery("SELECT s.id FROM Student s", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
    public List<Student> findStudentsWithoutGroup() {
        try (Session session = sessionFactory.openSession()) {
            // HQL-запрос для поиска студентов без группы
            Query<Student> query = session.createQuery(
                    "FROM Student s WHERE s.group IS NULL", Student.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }


}
