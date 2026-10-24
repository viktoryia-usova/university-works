package org.example.Server.Service;

import org.example.Server.DAO.ApplicationDAO;
import org.example.Server.DAO.CourseDAO;
import org.example.Server.Entities.Application;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class ApplicationService implements Service<Application> {
   private ApplicationDAO applicationDAO;
    public ApplicationService() {
        applicationDAO = new ApplicationDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Application application) {
        applicationDAO.insert(application);
    }

    @Override
    public void update(Application application) {
        applicationDAO.update(application);
    }

    @Override
    public void delete(int id) {
        applicationDAO.delete(id);
    }

    @Override
    public Application findById(int id) {
        Application application = applicationDAO.find(id);
        return application;
    }

    @Override
    public List<Application> findAll() {
        return applicationDAO.findAll();
    }
    public List<Application> findByType(int type_application_id) {
       return applicationDAO.findByType(type_application_id);

    }
    public void deleteByProfileId(int profileId){
        applicationDAO.deleteByProfileId(profileId);
    }
}
