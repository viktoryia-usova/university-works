package org.example.Server.DAO;

import org.example.Server.Entities.Student;
import org.example.Server.Entities.Teacher;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class TeacherDAO implements DAO<Teacher> {
    private final SessionFactory sessionFactory;
    public TeacherDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Teacher teacher) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(teacher);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Teacher teacher) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(teacher);
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
            Teacher teacher = session.get(Teacher.class, id);
            if (teacher != null) {
                session.delete(teacher);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Teacher find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Teacher.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Teacher> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Teacher> query = session.createQuery("from Teacher", Teacher.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllTechersIds() {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL-запрос для получения всех значений course_id
            Query<Integer> query = session.createQuery("SELECT tc.id FROM Teacher tc", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
    public Integer findProfileIdByTeacherId(int teacherId) {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL для выборки profile_id на основе studentId
            Query<Integer> query = session.createQuery(
                    "SELECT s.profile.id FROM Teacher s WHERE s.id = :teacherId", Integer.class);
            query.setParameter("teacherId", teacherId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
