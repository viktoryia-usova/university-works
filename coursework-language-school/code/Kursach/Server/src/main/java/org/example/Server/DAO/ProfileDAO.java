package org.example.Server.DAO;

import org.example.Server.Entities.Profile;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProfileDAO implements DAO<Profile> {
    private final SessionFactory sessionFactory;

    public ProfileDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void insert(Profile profile) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(profile);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Profile find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Profile.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(Profile profile) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(profile);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Profile profile = session.get(Profile.class, id);
            if (profile != null) {
                session.delete(profile);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }



    public List<Profile> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Profile> query = session.createQuery("from Profile", Profile.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Profile findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<Profile> query = session.createQuery("from Profile p where p.user.username = :username", Profile.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Profile findByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Profile> query = session.createQuery("FROM Profile WHERE user.id = :userId", Profile.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("Ошибка при получении профиля по user_id: " + e.getMessage());
            return null;
        }
    }
    public Integer findUserIdByProfileId(int profileId) {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL для выборки user_id на основе profileId
            Query<Integer> query = session.createQuery(
                    "SELECT p.user.id FROM Profile p WHERE p.id = :profileId", Integer.class);
            query.setParameter("profileId", profileId);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Ошибка при получении user_id по profile_id: " + e.getMessage());
            return null;
        }
    }


}
