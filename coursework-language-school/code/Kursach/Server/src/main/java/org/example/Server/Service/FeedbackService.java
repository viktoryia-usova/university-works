package org.example.Server.Service;

import org.example.Server.DAO.FeedbackDAO;
import org.example.Server.Entities.Feedback;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class FeedbackService implements Service<Feedback> {
    private FeedbackDAO feedbackDAO;
    public FeedbackService() {
        feedbackDAO = new FeedbackDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Feedback feedback) {
        feedbackDAO.insert(feedback);
    }

    @Override
    public void update(Feedback feedback) {
        feedbackDAO.update(feedback);
    }

    @Override
    public void delete(int id) {
        feedbackDAO.delete(id);
    }

    @Override
    public Feedback findById(int id) {
        Feedback feedback = feedbackDAO.find(id);
        return feedback;
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackDAO.findAll();
    }
}
