package org.example.Server.DAO;

import org.example.Server.Entities.Course;
import org.example.Server.Entities.Profile;
import org.example.Server.Entities.Role;
import org.example.Server.Entities.User;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CourseDAO implements DAO<Course> {
    private final SessionFactory sessionFactory;

    public CourseDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) { // Создаем сессию
            transaction = session.beginTransaction(); // Начинаем транзакцию

            session.save(course); // Сохраняем курс

            transaction.commit(); // Завершаем транзакцию
        } catch (Exception e) {
            // Если произошла ошибка, откатываем транзакцию
            if (transaction != null) {
                try {
                    transaction.rollback(); // Откат транзакции
                } catch (Exception rollbackEx) {
                    // Логирование ошибки отката
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace(); // Логируем основную ошибку
        }
    }


    @Override
    public void update(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(course);
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
            Course course = session.get(Course.class, id);
            if (course != null) {
                session.delete(course);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Course find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Course.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Course> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Course> query = session.createQuery("from Course", Course.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
