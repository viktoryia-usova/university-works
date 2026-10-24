package org.example.Server.Service;
import org.example.Server.DAO.ProfileDAO;
import org.example.Server.DAO.UserDAO;
import org.example.Server.Entities.Profile;
import org.example.Server.Interfaces.Service;
import org.example.Server.Utility.HibernateUtil;

import java.util.List;

public class ProfileService  implements Service<Profile> {

        private ProfileDAO profileDAO;
        public UserDAO userDAO;


        public ProfileService(){
            this.profileDAO = new ProfileDAO(HibernateUtil.getSessionFactory());
            this.userDAO = new UserDAO(HibernateUtil.getSessionFactory());

        }

        @Override
        public void insert(Profile profile) {
            if (profile.getUser() != null) {
                profileDAO.insert(profile);
            }

        }

        @Override
        public void update(Profile profile) {
            if (profile.getUser() != null) {
                userDAO.update(profile.getUser());
            }
            profileDAO.update(profile);
        }


        @Override
        public void delete(int id) {
            profileDAO.delete(id);
        }

        @Override
        public Profile findById(int id) {
            return profileDAO.find(id);
        }

        @Override
        public List<Profile> findAll() {
            return profileDAO.findAll();
        }



        public Profile getProfileByUserId(int userId) {
            Profile profile = profileDAO.findByUserId(userId);

            System.out.println(profile);
            return profile;
        }
        public Integer findUserIdByProfileId(int profileId){
            return profileDAO.findUserIdByProfileId(profileId);
        }
    }


