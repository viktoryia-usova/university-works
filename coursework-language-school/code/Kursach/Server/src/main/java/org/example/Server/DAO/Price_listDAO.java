package org.example.Server.DAO;


import org.example.Server.Entities.Price_list;
import org.example.Server.Entities.Profile;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class Price_listDAO implements DAO<Price_list> {
    private final SessionFactory sessionFactory;
    public Price_listDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Price_list priceList) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(priceList);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Price_list priceList) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(priceList);
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
            Price_list priceList = session.get(Price_list.class, id);
            if (priceList != null) {
                session.delete(priceList);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Price_list find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Price_list.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Price_list> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Price_list> query = session.createQuery("from Price_list", Price_list.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Price_list findByCourseId(int courseId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Price_list> query = session.createQuery("FROM Price_list WHERE course.id = :courseId", Price_list.class);
            query.setParameter("courseId", courseId);
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("Ошибка при получении курса по course_id: " + e.getMessage());
            return null;
        }
    }
    public List<Integer> findAllCourseIds() {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL-запрос для получения всех значений course_id
            Query<Integer> query = session.createQuery("SELECT pl.course.id FROM Price_list pl", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
    public void updatePriceByCourseId(int courseId, int newPrice) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query query = session.createQuery("UPDATE Price_list SET price = :newPrice WHERE course.id = :courseId");
            query.setParameter("newPrice", newPrice);
            query.setParameter("courseId", courseId);
            int result = query.executeUpdate();

            if (result == 0) {
                System.out.println("Курс с указанным ID не найден.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }


    public List<Price_list> findAllWithCourseName() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT pl, c.course_type FROM Price_list pl JOIN pl.course c";  // Используем JOIN для извлечения курса
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> results = query.getResultList();

            List<Price_list> priceLists = new ArrayList<>();
            for (Object[] result : results) {
                Price_list priceList = (Price_list) result[0];
                String courseName = (String) result[1];
                priceList.setCourseName(courseName); // Устанавливаем название курса
                priceLists.add(priceList);
            }

            return priceLists;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
