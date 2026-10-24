package org.example.Server.DAO;

import org.example.Server.Entities.Role;
import org.example.Server.Entities.Schedule;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO implements DAO<Schedule> {
    private final SessionFactory sessionFactory;
    public ScheduleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Schedule schedule) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(schedule);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Schedule schedule) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(schedule);
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
            Schedule schedule = session.get(Schedule.class, id);
            if (schedule != null) {
                session.delete(schedule);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Schedule find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Schedule.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Schedule> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Schedule> query = session.createQuery("from Schedule", Schedule.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllScheduleIds() {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL-запрос для получения всех значений course_id
            Query<Integer> query = session.createQuery("SELECT sh.id FROM Schedule sh", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
}
