package org.example.Server.Service;

import org.example.Server.DAO.GroupDAO;
import org.example.Server.DAO.ScheduleDAO;
import org.example.Server.Entities.Schedule;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class ScheduleService implements Service<Schedule> {
    private ScheduleDAO scheduleDAO;
    public ScheduleService() {
        scheduleDAO = new ScheduleDAO(HibernateUtil.getSessionFactory());
    }
    @Override
    public void insert(Schedule schedule) {
        scheduleDAO.insert(schedule);
    }

    @Override
    public void update(Schedule schedule) {
        scheduleDAO.update(schedule);
    }

    @Override
    public void delete(int id) {
        scheduleDAO.delete(id);
    }

    @Override
    public Schedule findById(int id) {
       Schedule schedule = scheduleDAO.find(id);
       return schedule;
    }
    public List<Integer> getAllScheduleIds() {
        return scheduleDAO.findAllScheduleIds();
    }
    @Override
    public List<Schedule> findAll() {
        return scheduleDAO.findAll();
    }
}
