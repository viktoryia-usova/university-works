package org.example.Server.DAO;

import org.example.Server.Entities.Application;
import org.example.Server.Entities.Price_list;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ApplicationDAO implements DAO<Application> {
    private final SessionFactory sessionFactory;
    public ApplicationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void insert(Application application) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(application);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Application application) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(application);
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
            Application application = session.get(Application.class, id);
            if (application != null) {
                session.delete(application);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Application find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Application.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Application> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Application> query = session.createQuery("from Application", Application.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Application> findByType(int typeApplicationId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Application> query = session.createQuery("from Application where type_application.id = :typeApplicationId", Application.class);
            query.setParameter("typeApplicationId", typeApplicationId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void deleteByProfileId(int profileId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Запрос для получения всех заявок по profile_id
            Query<Application> query = session.createQuery("from Application where profile.id = :profileId", Application.class);
            query.setParameter("profileId", profileId);
            List<Application> applications = query.getResultList();

            // Удаление всех найденных заявок
            for (Application application : applications) {
                session.delete(application);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

}
