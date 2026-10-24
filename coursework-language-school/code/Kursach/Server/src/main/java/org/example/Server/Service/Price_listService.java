package org.example.Server.Service;

import org.example.Server.DAO.CourseDAO;
import org.example.Server.DAO.Price_listDAO;
import org.example.Server.Entities.Price_list;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class Price_listService implements Service<Price_list> {
    private Price_listDAO price_listDAO;

    public Price_listService() {
        price_listDAO = new Price_listDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public  void insert(Price_list price_list) {
        price_listDAO.insert(price_list);
    }
    public List<Integer> getAllCourseIds() {
        return price_listDAO.findAllCourseIds();
    }
    public void updatePriceByCourseId(int courseId, int newPrice){
        price_listDAO.updatePriceByCourseId(courseId, newPrice);
    }
    @Override
    public void update(Price_list price_list) {
        price_listDAO.update(price_list);
    }

    @Override
    public void delete(int id) {
        price_listDAO.delete(id);
    }

    @Override
    public Price_list findById(int id) {
        Price_list price_list = price_listDAO.find(id);
        return price_list;
    }

    @Override
    public List<Price_list> findAll() {
        return price_listDAO.findAll();
    }
    public Price_list getProfileByCourseId(int courseId) {
        Price_list price_list = price_listDAO.findByCourseId(courseId);
        return price_list;
    }
    public Price_list findByCourseId(int courseId) {
        Price_list price_list = price_listDAO.findByCourseId(courseId);
        return price_list;
    }
}
