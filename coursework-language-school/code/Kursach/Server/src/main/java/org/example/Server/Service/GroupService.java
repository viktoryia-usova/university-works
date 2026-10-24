package org.example.Server.Service;

import org.example.Server.DAO.CourseDAO;
import org.example.Server.DAO.GroupDAO;
import org.example.Server.Entities.Group;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class GroupService implements Service<Group> {
    private GroupDAO groupDAO;
    public GroupService() {
        groupDAO = new GroupDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Group group) {
        groupDAO.insert(group);
    }

    @Override
    public void update(Group group) {
        groupDAO.update(group);

    }

    @Override
    public void delete(int id) {
        groupDAO.delete(id);
    }

    @Override
    public Group findById(int id) {
       Group group = groupDAO.find(id);
       return group;
    }

    @Override
    public List<Group> findAll() {
       return groupDAO.findAll();
    }
    public List<Integer> findAllGroupIds() {
        return groupDAO.findAllGroupIds();
    }
    public List<Integer> findAllGroupIds(int currentProfileId) {
        return groupDAO.findAllGroupIds(currentProfileId);
    }
}
