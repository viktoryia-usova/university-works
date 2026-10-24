package org.example.Server.DAO;

import org.example.Server.Entities.Feedback;
import org.example.Server.Entities.Language;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class LanguageDAO implements DAO<Language> {
    private final SessionFactory sessionFactory;
    public LanguageDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public void insert(Language language) {

    }

    @Override
    public void update(Language language) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Language find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Language.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Language> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Language> query = session.createQuery("from Language", Language.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> findAllLanguageIds() {
        try (Session session = sessionFactory.openSession()) {
            // Используем HQL-запрос для получения всех значений course_id
            Query<Integer> query = session.createQuery("SELECT l.id FROM Language l", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
}
