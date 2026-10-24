package org.example.Server.DAO;

import org.example.Server.Entities.Group;
import org.example.Server.Entities.Price_list;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class GroupDAO implements DAO<Group> {
    private final SessionFactory sessionFactory;
    public GroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public void insert(Group group) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(group);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Group group) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(group);
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
            Group group = session.get(Group.class, id);
            if (group != null) {
                session.delete(group);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Group find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Group.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Group> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Group> query = session.createQuery("from Group", Group.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllGroupIds() {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createQuery("select id from Group", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllGroupIds(int currentProfileId) {
        try (Session session = sessionFactory.openSession()) {
            // Запрос с использованием JOIN для поиска групп по currentProfileId
            Query<Integer> query = session.createQuery(
                    "select g.id from Group g " +
                            "join g.teacher t " + // Связь между Group и Teacher
                            "join t.profile p " + // Связь между Teacher и Profile
                            "where p.id = :currentProfileId", Integer.class
            );

            // Устанавливаем параметр currentProfileId
            query.setParameter("currentProfileId", currentProfileId);

            // Возвращаем список номеров групп
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
