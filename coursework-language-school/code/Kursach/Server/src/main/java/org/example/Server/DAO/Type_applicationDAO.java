package org.example.Server.DAO;

import org.example.Server.Entities.Application;
import org.example.Server.Entities.Type_application;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;


public class Type_applicationDAO implements DAO<Type_application> {
    private final SessionFactory sessionFactory;
    public Type_applicationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Type_application typeApplication) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(typeApplication);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Type_application typeApplication) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(typeApplication);
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
            Type_application typeApplication = session.get(Type_application.class, id);
            if (typeApplication != null) {
                session.delete(typeApplication);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Type_application find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Type_application.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Type_application> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Type_application> query = session.createQuery("from Type_application ", Type_application.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
