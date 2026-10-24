package org.example.Server.Service;

import org.example.Server.DAO.UserDAO;
import org.example.Server.Entities.Role;
import org.example.Server.Entities.User;
import org.example.Server.Utility.HibernateUtil;

import org.example.Server.Interfaces.Service;
import org.hibernate.Session;

import java.util.List;


public class UserService implements Service<User> {

    private static UserDAO userDAO ;

    public UserService() {
        userDAO = new UserDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(User user) {
        userDAO.insert(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public void delete(int id) {
        userDAO.delete(id);
    }

    @Override
    public User findById(int id) {
        User user = userDAO.find(id);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    public static boolean isUsernameExists(String username) {
        User existingUser = userDAO.findByUsername(username);
        return existingUser != null;
    }

    public User login(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }

        return user;
    }
    public Role getRoleById(int roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Role.class, roleId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
