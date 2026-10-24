package org.example.Server.Service;

import org.example.Server.DAO.LanguageDAO;
import org.example.Server.DAO.Price_listDAO;
import org.example.Server.Entities.Language;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class LanguageService implements Service<Language> {
    private LanguageDAO languageDAO;
    public LanguageService() {
        languageDAO = new LanguageDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Language entity) {

    }

    @Override
    public void update(Language entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Language findById(int id) {
        Language language = languageDAO.find(id);
        return language;
    }
    public List<Integer> getAllLanguageIds() {
        return languageDAO.findAllLanguageIds();
    }
    @Override
    public List<Language> findAll() {
        return languageDAO.findAll();
    }
}
