package org.example.Server.Utility;


import com.google.gson.reflect.TypeToken;
import org.example.Server.Entities.TCP.Request;
import org.example.Server.Entities.TCP.Response;
import org.example.Server.Entities.TCP.RequestType;
import org.example.Server.Entities.* ;

import org.example.Server.Service.*;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientThread implements Runnable {
    private Socket clientsSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Gson gson;
    private Response response;
    private Request request;
    private int currentProfileId = -1;
    private int currentStudentId = -1;



    private UserService userService = new UserService();
    private ProfileService profileService = new ProfileService();
    private CourseService courseService = new CourseService();
    private Price_listService priceListService = new Price_listService();
    private FeedbackService feedbackService = new FeedbackService();
    private LanguageService languageService = new LanguageService();
    private ApplicationService applicationService = new ApplicationService();
    private GroupService groupService = new GroupService();
    private ScheduleService scheduleService = new ScheduleService();
    private StudentService studentService = new StudentService();
    private TeacherService teacherService = new TeacherService();
    private TestService testService = new TestService();


    public ClientThread(Socket socket) throws IOException {
        this.clientsSocket = socket;
        request = new Request();
        gson = new Gson();
        response = new Response();
        writer = new PrintWriter(clientsSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));


    }

    @Override
    public void run() {
        try {
            while(clientsSocket.isConnected()) {
                String line = reader.readLine();
                if (line == null) break;
                request = gson.fromJson(line, Request.class);
                request.setRequestType(RequestType.valueOf(request.getRequestType().name()));

                switch(request.getRequestType()){
                    case REGISTER-> {
                        handleRegistration(request);
                        break;
                    }
                    case LOGIN -> {handleLogin(request);break;}
                    case MenuUser -> {handleMenuUser(request); break;}
                    case StudyApplication -> {handleStudyApplication(request); break;}
                    case TeachingApplication -> {handleTeachingApplication(request); break;}
                    case SubmitApplicationStudy -> {handleStudentApplication(request); break;}
                    case SubmitApplicationTeaching -> {handleTeacherApplication(request); break;}
                    case MenuAdmin -> {handleMenuAdmin(request); break;}
                    case StudentApplication -> {HandleAddStudentApplication(request);break;}
                    case TeacherApplication -> {HandleAddTeacherApplication(request);break;}
                    case StudentMenu -> {handleMenuStudent(request);break;}
                    case TeacherMenu -> {handleTeacherMenu(request);break;}
                    case GetGroups -> {handleloadgroups(request);break;}
                    case GetGroupMembers -> {handleGetGroupMembers(request,response);break;}
                    case AddCourse -> {handleAddCourse(request);break;}
                    case DeleteCourse -> {handleDeleteCourse(request);break;}
                    case CourseIds -> {handleGetCourseIds(request);break;}
                    case UpdatePrice -> {UpdatePriceInLists(request);break;}
                    case DeleteFeedback -> {handleDeleteFeedback(request);break;}
                    case addFeedback -> {handleAddFeedback(request);break;}
                    case testResult -> {handleSendResult(request);break;}
                    case AddSchedule -> {handleAddSchedule(request);break;}
                    case DeleteSchedule -> {handleDeleteSchedule(request);break;}
                    case ScheduleIds -> {handleGetScheduleIds(request);break;}
                    case editSchedule -> {UpdateSchedule(request);break;}
                    case TeacherIds -> {handleGetTeacherIds(request);break;}
                    case LanguageIds -> {handleGetLanguageIds(request);break;}
                    case addGroup -> {AddGroup(request);break;}
                    case DeleteGroup -> {handleDeleteGroup(request);break;}
                    case DeleteStudent -> {handleDeleteStudent(request);break;}
                    case DeleteTeacher -> {handleDeleteTeacher(request);break;}
                    case editTeacher -> {UpdateTeacher(request);break;}
                    case StudentIds -> {handleGetStudentIds(request);break;}
                    case editStudent -> {UpdateStudent(request);break;}
                    case AddStudent -> {handleAddStudent(request);break;}
                    case AddTeacher -> {handleAddTeacher(request);break;}
                    case GroupIds -> {handleGetGroupsIds(request);break;}
                    case loadStudent -> {loadStudentsWithoutGroups(request);break;}
                    case AddToGroup -> {AddStudentToGroup(request);break;}
                    case TeacherNames -> {handleGetTeacherNames(request); break;}
                    case LanguageNames -> {handleGetLanguageNames(request); break;}
                    case CourseNames -> {handleGetCourseNames(request); break;}
                    case ScheduleNames -> {handleGetScheduleNames(request); break;}
                    case GetTeacherData -> {handleGetTeacherData(request); break;}
                    case StudentNames -> {handleStudentNames(request); break;}
                    case GetStudentData -> {handleGetStudentData(request);break;}
                    case LOGOUT-> {}

                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка в потоке клиента: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientsSocket != null) clientsSocket.close();
                System.out.println("Соединение с клиентом закрыто.");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
    private void handleAddFeedback(Request request) {
        try {
            Feedback feedback = gson.fromJson(request.getMessage(), Feedback.class);
            System.out.println("Получен отзыв: " + feedback.getText());

            if (feedback.getText() == null || feedback.getText().isEmpty()) {
                throw new IllegalArgumentException("Текст отзыва не может быть пустым.");
            }

            // Получение объекта User из базы по userId
            int userId = feedback.getUser().getId();
            User user = userService.findById(userId);

            if (user == null) {
                throw new IllegalArgumentException("Пользователь с id=" + userId + " не найден.");
            }

            feedback.setUser(user); // Устанавливаем связь с пользователем

            feedbackService.insert(feedback);

            Response response = new Response();
            response.setResponseType(RequestType.addFeedback);
            response.setSuccess(true);
            response.setMessage("Отзыв успешно добавлен.");
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка: " + e.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        }
    }
    private void handleAddTeacher(Request request) {
        try {
            // Десериализуем объект студента из тела запроса
            Teacher teacher = gson.fromJson(request.getMessage(), Teacher.class);

            // Используем DAO для сохранения студента
            TeacherService teacherService = new TeacherService();
            teacherService.insert(teacher);
            int id =teacherService.findProfileIdByTeacherId(teacher.getId());
            Profile profile = profileService.findById(id);
            int userid =profileService.findUserIdByProfileId(id);
            User user = userService.findById(userid);
            Role userRole = userService.getRoleById(4);
            user.setRole(userRole);
            profile.setUser(user);
            userService.update(user);
            applicationService.deleteByProfileId(id);
            // Отправляем успешный ответ
            Response response = new Response();
            response.setResponseType(RequestType.AddTeacher);
            response.setSuccess(true);
            response.setMessage("Преподаватель успешно добавлен.");
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {

            e.printStackTrace();
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка: " + e.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    private void handleAddStudent(Request request) {
        try {
            // Десериализуем объект студента из тела запроса
            Student student = gson.fromJson(request.getMessage(), Student.class);

            // Используем DAO для сохранения студента
            StudentService studentService = new StudentService();
            int studentCount = studentService.getStudentCountInGroup(student.getGroup().getId());

            if (studentCount >= 10) {
                // Если студентов 10 или больше, отправляем сообщение об ошибке
                Response response = new Response();
                response.setResponseType(RequestType.AddStudent);
                response.setSuccess(false);
                response.setMessage("Группа уже заполнена (максимум 10 студентов).");
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }
            studentService.insert(student);
            int id =studentService.findProfileIdByStudentId(student.getId());
            Profile profile = profileService.findById(id);
            int userid =profileService.findUserIdByProfileId(id);
            User user = userService.findById(userid);
            Role userRole = userService.getRoleById(3);
            user.setRole(userRole);
            profile.setUser(user);
            userService.update(user);
            applicationService.deleteByProfileId(id);
            // Отправляем успешный ответ
            Response response = new Response();
            response.setResponseType(RequestType.AddStudent);
            response.setSuccess(true);
            response.setMessage("Студент успешно добавлен.");
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {

            e.printStackTrace();
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка: " + e.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    private void AddGroup(Request request) {
        try {
            // Парсинг данных из сообщения
            String message = request.getMessage();
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>() {}.getType());

            int teacherId = ((Double) data.get("teacherId")).intValue();
            int languageId = ((Double) data.get("languageId")).intValue();
            int courseId = ((Double) data.get("courseId")).intValue();
            int scheduleId = ((Double) data.get("scheduleId")).intValue();

            Teacher teacher = teacherService.findById(teacherId);
            Language language = languageService.findById(languageId);
            Course course = courseService.findById(courseId);
            Schedule schedule = scheduleService.findById(scheduleId);
            // Добавление группы в базу данных
            Group newGroup = new Group();
            newGroup.setTeacher(teacher);
            newGroup.setLanguage(language);
            newGroup.setCourse(course);
            newGroup.setSchedule(schedule);

            groupService.insert(newGroup);

            // Отправка успешного ответа
            Response response = new Response();
            response.setSuccess(true);
            response.setMessage("Группа успешно добавлена.");
            response.setResponseType(RequestType.addGroup);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();

            // Отправка ответа об ошибке
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    private void handleSendResult(Request request) {
        try {
            Test test = gson.fromJson(request.getMessage(), Test.class);
            System.out.println("Получен результат: " + test.getTest_result() + test.getStudent().getId());

            int studentId = currentStudentId;
            System.out.println(studentId);
            Student student= studentService.findById(studentId);


            test.setStudent(student); // Устанавливаем связь с пользователем

            testService.insert(test);

            Response response = new Response();
            response.setResponseType(RequestType.testResult);
            response.setSuccess(true);
            response.setMessage("Тест успешно добавлен.");
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка: " + e.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        }
    }
    public void handleRegistration(Request request) {
        Profile profile = gson.fromJson(request.getMessage(), Profile.class);
        System.out.println("Полученный клиент: " + profile);

        if (userService.isUsernameExists(profile.getUser().getUsername())) {
            response.setMessage("Ошибка: Имя пользователя уже занято.");
        } else {
            try {
                Role userRole = userService.getRoleById(2);
                profile.getUser().setRole(userRole);
                String hashedPassword = PasswordUtils.hashPassword(profile.getUser().getPassword());
                profile.getUser().setPassword(hashedPassword);
                userService.insert(profile.getUser());
                profileService.insert(profile);
                response.setMessage("Регистрация прошла успешно.");
                response.setSuccess(true);
            } catch (Exception e) {
                response.setMessage("Ошибка регистрации: " + e.getMessage());
                response.setSuccess(false);
                e.printStackTrace();
            }
        }
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleLogin(Request request) {
        Response response = new Response();
        try {
            // Извлекаем данные пользователя из запроса
            User user = gson.fromJson(request.getMessage(), User.class);
            System.out.println("Попытка входа для пользователя: " + user.getUsername());

            // Находим пользователя в базе данных
            User foundUser = userService.login(user.getUsername());
            if (foundUser == null) {
                response.setMessage("Ошибка: Пользователь не найден.");
                response.setSuccess(false);
            } else {
                // Проверяем введённый пароль с хешированным
                boolean passwordMatches = PasswordUtils.checkPassword(user.getPassword(), foundUser.getPassword());
                if (!passwordMatches) {
                    response.setMessage("Ошибка: Неверный пароль.");
                    response.setSuccess(false);
                } else {
                    // Получаем профиль пользователя
                    Profile profile = profileService.getProfileByUserId(foundUser.getId());
                    if (profile != null) {
                        currentProfileId = profile.getId();
                        System.out.println("Текущий profile_id: " + currentProfileId);

                        // Если роль - студент, находим связанные данные
                        if (foundUser.getRole().getRoleType().equalsIgnoreCase("student")) {
                            Student student = studentService.findByProfileId(profile.getId());
                            currentStudentId = student.getId();
                        }

                        response.setMessage(new Gson().toJson(profile));
                        response.setSuccess(true);
                        response.setResponseType(RequestType.LOGIN);
                    } else {
                        response.setMessage("Ошибка: Профиль, связанный с пользователем, не найден.");
                        response.setSuccess(false);
                    }
                }
            }
        } catch (Exception e) {
            response.setMessage("Ошибка: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        // Отправляем ответ клиенту
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleDeleteCourse(Request request) {
        Response response = new Response();
        try {
            int courseId = Integer.parseInt(request.getMessage()); // ID курса из запроса
            courseService.delete(courseId); // Удаление курса через сервис
            response.setMessage("Курс успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteCourse);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении курса: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleDeleteSchedule(Request request) {
        Response response = new Response();
        try {
            int scheduleId = Integer.parseInt(request.getMessage());
            scheduleService.delete(scheduleId);
            response.setMessage("Расписание успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteSchedule);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении расписания: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleDeleteGroup(Request request) {
        Response response = new Response();
        try {
            int groupId = Integer.parseInt(request.getMessage());
            groupService.delete(groupId);
            response.setMessage("Группа успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteGroup);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении группы: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleDeleteTeacher(Request request) {
        Response response = new Response();
        try {
            int teacherId = Integer.parseInt(request.getMessage());
            int profileId = teacherService.findProfileIdByTeacherId(teacherId);
            teacherService.delete(teacherId);
            int userId = profileService.findUserIdByProfileId(profileId);
            profileService.delete(profileId);
            userService.delete(userId);
            response.setMessage("Преподаватель успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteTeacher);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении преподавателя: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    } private void handleDeleteStudent(Request request) {
        Response response = new Response();
        try {
            int studentId = Integer.parseInt(request.getMessage());

            int profileid = studentService.findProfileIdByStudentId(studentId);
            studentService.delete(studentId);
            int userid = profileService.findUserIdByProfileId(profileid);
            profileService.delete(profileid);
            userService.delete(userid);
            response.setMessage("Студент успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteStudent);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении студента: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleDeleteFeedback(Request request) {
        Response response = new Response();
        try {
            int feedbackId = Integer.parseInt(request.getMessage()); // ID курса из запроса
            feedbackService.delete(feedbackId); // Удаление курса через сервис
            response.setMessage("Отзыв успешно удалён.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DeleteFeedback);
        } catch (Exception e) {
            response.setMessage("Ошибка при удалении отзыва: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }
    public void handleMenuUser(Request request) {
        try {
            // Получение списков курсов, прайс-листов и отзывов
            List<Course> courses = courseService.findAll();
            List<Price_list> price_lists = priceListService.findAll();
            List<Feedback> feedbacks = feedbackService.findAll();

            // Создание карты с курсами, где ключ - это courseId, а значение - название курса
            Map<Integer, String> courseNames = new HashMap<>();
            for (Course course : courses) {
                courseNames.put(course.getId(), course.getCourse_type());
            }

            // Связывание прайс-листов с названиями курсов
            for (Price_list price_list : price_lists) {
                String courseName = courseNames.get(price_list.getCourse().getId());
                price_list.setCourseName(courseName);
            }

            // Установка имени пользователя для каждого отзыва
            for (Feedback feedback : feedbacks) {
                if (feedback.getUser() != null) {
                    feedback.setAuthor(feedback.getUser().getUsername());
                }
            }

            // Преобразование списков в JSON
            String coursesJson = gson.toJson(courses);
            String priceListsJson = gson.toJson(price_lists);
            String feedbacksJson = gson.toJson(feedbacks);

            // Отправка ответа клиенту
            response.setMessage(coursesJson + ";" + priceListsJson + ";" + feedbacksJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.MenuUser);

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    public void handleStudyApplication(Request request) {
        List<Language> languages = languageService.findAll();
        String languagesJson = gson.toJson(languages);
        System.out.println(languagesJson);
        response.setMessage(languagesJson);
        response.setSuccess(true);
        response.setResponseType(RequestType.StudyApplication);
        writer.println(gson.toJson(response));
        writer.flush();
        System.out.println("Ответ отправлен на клиент");
    }
    public void handleTeachingApplication(Request request) {
        List<Language> languages = languageService.findAll();
        String languagesJson = gson.toJson(languages);
        System.out.println(languagesJson);
        response.setMessage(languagesJson);
        response.setSuccess(true);
        response.setResponseType(RequestType.TeachingApplication);
        writer.println(gson.toJson(response));
        writer.flush();
        System.out.println("Ответ отправлен на клиент");
    }
    public void handleStudentApplication(Request request) {
        System.out.println("Полученная заявка: " + request.getMessage());
        Application application = gson.fromJson(request.getMessage(), Application.class);

        try {
            // Проверяем, существует ли профиль в базе данных
            Profile existingProfile = profileService.findById(application.getProfile().getId());
            if (existingProfile == null) {
                throw new IllegalStateException("Профиль не найден в базе данных. Сначала сохраните профиль.");
            }

            // Устанавливаем существующий профиль в заявке
            application.setProfile(existingProfile);

            // Сохраняем заявку
            applicationService.insert(application);

            response.setMessage("Заявка успешно отправлена.");
            response.setSuccess(true);
            response.setResponseType(RequestType.SubmitApplicationStudy);
        } catch (Exception e) {
            response.setMessage("Ошибка обработки заявки: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }
    public void handleTeacherApplication(Request request) {
        System.out.println("Полученная заявка: " + request.getMessage());
        Application application = gson.fromJson(request.getMessage(), Application.class);

        try {
            // Проверяем, существует ли профиль в базе данных
            Profile existingProfile = profileService.findById(application.getProfile().getId());
            if (existingProfile == null) {
                throw new IllegalStateException("Профиль не найден в базе данных. Сначала сохраните профиль.");
            }

            // Устанавливаем существующий профиль в заявке
            application.setProfile(existingProfile);

            // Сохраняем заявку
            applicationService.insert(application);

            response.setMessage("Заявка успешно отправлена.");
            response.setSuccess(true);
            response.setResponseType(RequestType.SubmitApplicationTeaching);
        } catch (Exception e) {
            response.setMessage("Ошибка обработки заявки: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }
    public void handleMenuStudent(Request request){
        try {
            // Получение списков курсов, прайс-листов и отзывов
            List<Course> courses = courseService.findAll();
            List<Price_list> price_lists = priceListService.findAll();
            List<Feedback> feedbacks = feedbackService.findAll();
            List<Group> groups = groupService.findAll();
            List<Schedule> schedules = scheduleService.findAll();

            // Создание карты с курсами, где ключ - это courseId, а значение - название курса
            Map<Integer, String> courseNames = new HashMap<>();
            for (Course course : courses) {
                courseNames.put(course.getId(), course.getCourse_type());
            }

            // Связывание прайс-листов с названиями курсов
            for (Price_list price_list : price_lists) {
                String courseName = courseNames.get(price_list.getCourse().getId());
                price_list.setCourseName(courseName);
            }

            // Установка имени пользователя для каждого отзыва
            for (Feedback feedback : feedbacks) {
                if (feedback.getUser() != null) {
                    feedback.setAuthor(feedback.getUser().getUsername());
                }
            }
            for (Group group : groups) {
                if (group.getTeacher() != null) {
                    group.setTeacherName(group.getTeacher().getProfile().getLastname());
                }
                if (group.getLanguage() != null) {
                    group.setLanguageName(group.getLanguage().getLanguageType());
                }
                if (group.getSchedule() != null) {
                    group.setScheduleDate(group.getSchedule().getDate());
                    group.setScheduleTime(group.getSchedule().getTime());
                }
                for (Schedule schedule : schedules) {
                    if (group.getSchedule() != null){
                        schedule.setGroupa(group.getId());
                    }
                }

            }
            // Преобразование списков в JSON
            String coursesJson = gson.toJson(courses);
            String priceListsJson = gson.toJson(price_lists);
            String feedbacksJson = gson.toJson(feedbacks);
            String schedulesJson = gson.toJson(schedules);

            // Отправка ответа клиенту
            response.setMessage(coursesJson + ";" + priceListsJson + ";" + feedbacksJson + ";" + schedulesJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.StudentMenu);

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    private void UpdateSchedule(Request request){
        try {
            String message = request.getMessage();
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>() {}.getType());

            // Чтение данных из Map
            Object idObj = data.get("id");
            int Id = (idObj instanceof Double) ? ((Double) idObj).intValue() : Integer.parseInt(idObj.toString());
            String date = (String) data.get("newDate");
            String time = (String) data.get("newTime");
            String room = (String) data.get("newRoom");

            Schedule schedule = scheduleService.findById(Id);
            if (schedule != null) {
                schedule.setDate(date);
                schedule.setTime(time);
                schedule.setRoom(room);
                scheduleService.update(schedule);

                response.setSuccess(true);
                response.setMessage("Расписание успешно обновлено.");
                response.setResponseType(RequestType.editSchedule);
                writer.println(gson.toJson(response));
            } else {

                response.setSuccess(false);
                response.setMessage("Расписание с указанным ID не найден.");
                writer.println(gson.toJson(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Формируем ответ об ошибке
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            writer.println(gson.toJson(response));
        }

    }
    private void UpdateTeacher(Request request){
        try {
            String message = request.getMessage();
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>() {}.getType());

            // Чтение данных из Map
            Object idObj = data.get("id");
            int Id = (idObj instanceof Double) ? ((Double) idObj).intValue() : Integer.parseInt(idObj.toString());
            String name = (String) data.get("newName");
            String lastname = (String) data.get("newLastname");
            String phone = (String) data.get("newPhone");

            Teacher teacher = teacherService.findById(Id);
            int profileid = teacher.getProfile().getId();
            Profile profile = profileService.findById(profileid);
            if (profile != null) {
                profile.setName(name);
                profile.setLastname(lastname);
                profile.setPhonenumber(phone);
                profileService.update(profile);

                response.setSuccess(true);
                response.setMessage("Данные успешно обновлено.");
                response.setResponseType(RequestType.editTeacher);
                writer.println(gson.toJson(response));
            } else {

                response.setSuccess(false);
                response.setMessage("Учитель с указанным ID не найден.");
                writer.println(gson.toJson(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Формируем ответ об ошибке
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            writer.println(gson.toJson(response));
        }

    }
    private void UpdateStudent(Request request){
        try {
            String message = request.getMessage();
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>() {}.getType());

            // Чтение данных из Map
            Object idObj = data.get("id");
            int Id = (idObj instanceof Double) ? ((Double) idObj).intValue() : Integer.parseInt(idObj.toString());
            String name = (String) data.get("newName");
            String lastname = (String) data.get("newLastname");
            String phone = (String) data.get("newPhone");

            Student student = studentService.findById(Id);
            int profileid = student.getProfile().getId();
            Profile profile = profileService.findById(profileid);
            if (profile != null) {
                profile.setName(name);
                profile.setLastname(lastname);
                profile.setPhonenumber(phone);
                profileService.update(profile);

                response.setSuccess(true);
                response.setMessage("Данные успешно обновлено.");
                response.setResponseType(RequestType.editStudent);
                writer.println(gson.toJson(response));
            } else {

                response.setSuccess(false);
                response.setMessage("Студент с указанным ID не найден.");
                writer.println(gson.toJson(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Формируем ответ об ошибке
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            writer.println(gson.toJson(response));
        }

    }
    private void UpdatePriceInLists(Request request){
        try {
            String message = request.getMessage();
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>() {}.getType());

            int courseId = ((Double) data.get("courseId")).intValue();
            int newPrice = ((Double) data.get("newPrice")).intValue();

            // Обновляем стоимость курса в базе данных

            Price_list priceList = priceListService.findByCourseId(courseId);

            if (priceList != null) {
                priceList.setPrice(newPrice);
                priceListService.update(priceList);

                response.setSuccess(true);
                response.setMessage("Стоимость курса успешно обновлена.");
                response.setResponseType(RequestType.UpdatePrice);
                writer.println(gson.toJson(response));
            } else {

                response.setSuccess(false);
                response.setMessage("Курс с указанным ID не найден.");
                writer.println(gson.toJson(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Формируем ответ об ошибке
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            writer.println(gson.toJson(response));
        }


    }
    private void handleGetLanguageIds(Request request){
        try {
            LanguageService languageService1 = new LanguageService();
            List<Integer> languageIds = languageService1.getAllLanguageIds();
            String languageIdsJson = gson.toJson(languageIds);

            response.setMessage(languageIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.LanguageIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    }
    private void handleGetTeacherIds(Request request){
        try {
            TeacherService teacherService1 = new TeacherService();
            List<Integer> teacherIds = teacherService1.getAllTeachersIds();
            String teachersIdsJson = gson.toJson(teacherIds);

            response.setMessage(teachersIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.TeacherIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    } private void handleGetStudentIds(Request request){
        try {
            StudentService studentService1 = new StudentService();
            List<Integer> studentIds = studentService1.getAllStudentIds();
            String studentIdsJson = gson.toJson(studentIds);

            response.setMessage(studentIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.StudentIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    }
    private void handleGetGroupsIds(Request request){
        try {
            GroupService groupService1 = new GroupService();
            List<Integer> groupsIds = groupService1.findAllGroupIds();
            String groupsIdsJson = gson.toJson(groupsIds);

            response.setMessage(groupsIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.GroupIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    }
    private void handleGetScheduleIds(Request request){
        try {
            ScheduleService scheduleService1 = new ScheduleService();
            List<Integer> scheduleIds = scheduleService1.getAllScheduleIds();
            String scheduleIdsJson = gson.toJson(scheduleIds);

            response.setMessage(scheduleIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.ScheduleIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    }
    private void handleGetCourseIds(Request request) {
        try {
            Price_listService priceListService = new Price_listService();
            List<Integer> courseIds = priceListService.getAllCourseIds();
            String courseIdsJson = gson.toJson(courseIds);

            response.setMessage(courseIdsJson);
            response.setSuccess(true);
            response.setResponseType(RequestType.CourseIds);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();


        }
    }
    public void loadStudentsWithoutGroups(Request request){
        try {
        List<Student> students = studentService.findStudentsWithoutGroup();
        for(Student student : students){
            if(student.getProfile() != null){
                student.setName(student.getProfile().getName());
                student.setLastname(student.getProfile().getLastname());
                student.setPhonenumber(student.getProfile().getPhonenumber());
            }
            if(student.getLanguage() != null){
                student.setLanguageName(student.getLanguage().getLanguageType());
            }
        }
        String studentsJson = gson.toJson(students);
        response.setMessage(studentsJson + ";");
        response.setSuccess(true);
        response.setResponseType(RequestType.loadStudent);
        System.out.println(response.getMessage());

        writer.println(gson.toJson(response));
        writer.flush();

    } catch (Exception e) {
        response.setMessage("Ошибка загрузки данных: " + e.getMessage());
        response.setSuccess(false);
        writer.println(gson.toJson(response));
        writer.flush();
    }
    }
    public void StudentCountInGroup(Request request){
        int groupId = Integer.parseInt(request.getMessage());
        int count = studentService.getStudentCountInGroup(groupId);
        response.setSuccess(true);
        response.setResponseType(RequestType.StudentCountInGroup);
        response.setMessage(String.valueOf(count));
    }
    public void AddStudentToGroup(Request request){
        try {

        Student student = gson.fromJson(request.getMessage(), Student.class);
        int id =studentService.findProfileIdByStudentId(student.getId());
        Profile profile =new Profile();
        profile.setId(id);
        student.setProfile(profile);

        // Используем DAO для сохранения студента
        StudentService studentService = new StudentService();
        int studentCount = studentService.getStudentCountInGroup(student.getGroup().getId());

        if (studentCount >= 10) {
            // Если студентов 10 или больше, отправляем сообщение об ошибке
            Response response = new Response();
            response.setResponseType(RequestType.AddStudent);
            response.setSuccess(false);
            response.setMessage("Группа уже заполнена (максимум 10 студентов).");
            writer.println(gson.toJson(response));
            writer.flush();
            return;
        }
        studentService.update(student);
        Response response = new Response();
        response.setResponseType(RequestType.AddToGroup);
        response.setSuccess(true);
        response.setMessage("Студент успешно добавлен.");
        writer.println(gson.toJson(response));
        writer.flush();

    } catch (Exception e) {

        e.printStackTrace();
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("Ошибка: " + e.getMessage());
        writer.println(gson.toJson(response));
        writer.flush();

    }}
    public void handleMenuAdmin(Request request) {
        try {
            // Получение списков курсов, прайс-листов и отзывов
            List<Course> courses = courseService.findAll();
            List<Price_list> price_lists = priceListService.findAll();
            List<Feedback> feedbacks = feedbackService.findAll();
            List<Group> groups = groupService.findAll();
            List<Teacher> teachers = teacherService.findAll();
            List<Schedule> schedules = scheduleService.findAll();
            List<Language> languages = languageService.findAll();
            List<Student> students = studentService.findAll();

            // Создание карты с курсами, где ключ - это courseId, а значение - название курса
            Map<Integer, String> courseNames = new HashMap<>();
            for (Course course : courses) {
                courseNames.put(course.getId(), course.getCourse_type());
            }

            // Связывание прайс-листов с названиями курсов
            for (Price_list price_list : price_lists) {
                String courseName = courseNames.get(price_list.getCourse().getId());
                price_list.setCourseName(courseName);
            }

            // Установка имени пользователя для каждого отзыва
            for (Feedback feedback : feedbacks) {
                if (feedback.getUser() != null) {
                    feedback.setAuthor(feedback.getUser().getUsername());
                }
            }
            for (Group group : groups) {
                if (group.getTeacher() != null) {
                    group.setTeacherName(group.getTeacher().getProfile().getLastname());
                }
                if (group.getLanguage() != null) {
                    group.setLanguageName(group.getLanguage().getLanguageType());
                }
                if (group.getSchedule() != null) {
                    group.setScheduleDate(group.getSchedule().getDate());
                    group.setScheduleTime(group.getSchedule().getTime());
                }
                for (Schedule schedule : schedules) {
                    if (group.getSchedule() != null){
                        schedule.setGroupa(group.getId());
                    }
                }

            }
            for (Teacher teacher: teachers){
                if(teacher.getProfile() != null){
                    teacher.setName(teacher.getProfile().getName());
                    teacher.setLastname(teacher.getProfile().getLastname());
                    teacher.setPhonenumber(teacher.getProfile().getPhonenumber());
                }
                if(teacher.getProfile() != null){
                    teacher.setLanguageName(teacher.getLanguage().getLanguageType());
                }
            }
            for (Student student:students){
                if(student.getProfile() != null){
                    student.setName(student.getProfile().getName());
                    student.setLastname(student.getProfile().getLastname());
                    student.setPhonenumber(student.getProfile().getPhonenumber());
                }
                if(student.getLanguage() != null){
                    student.setLanguageName(student.getLanguage().getLanguageType());
                }
                if(student.getGroup()!= null){
                    student.setGroupNumber(student.getGroup().getId());
                }

            }


            // Преобразование списков в JSON
            String coursesJson = gson.toJson(courses);
            String priceListsJson = gson.toJson(price_lists);
            String feedbacksJson = gson.toJson(feedbacks);
            String groupsJson = gson.toJson(groups);
            String schedulesJson = gson.toJson(schedules);
            String teachersJson = gson.toJson(teachers);
            String studentsJson = gson.toJson(students);

            // Отправка ответа клиенту
            response.setMessage(coursesJson + ";" + priceListsJson + ";" + feedbacksJson+ ";" + groupsJson + ";" + schedulesJson + ";" + teachersJson + ";" + studentsJson) ;
            response.setSuccess(true);
            response.setResponseType(RequestType.MenuAdmin);
            System.out.println(response.getMessage());

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    public void handleloadgroups(Request request) {
        List<Integer> groupIds = groupService.findAllGroupIds(currentProfileId);
        String groupIdsJson = gson.toJson(groupIds);
        response.setMessage( groupIdsJson+ ";" );
        response.setSuccess(true);
        response.setResponseType(RequestType.StudentApplication);
        System.out.println(response.getMessage());

        writer.println(gson.toJson(response));
        writer.flush();

    }
    private void handleAddSchedule(Request request) {
        try{
            Schedule schedule = gson.fromJson(request.getMessage(), Schedule.class);
             scheduleService.insert(schedule); // Формируем успешный ответ
            Response response = new Response();
            response.setMessage("Курс успешно добавлен.");
            response.setSuccess(true);
            response.setResponseType(RequestType.AddSchedule);

            writer.println(gson.toJson(response));
            writer.flush();
        } catch (IllegalArgumentException e) {
            // Формируем ответ с ошибкой, если данные невалидны
            Response response = new Response();
            response.setMessage("Ошибка данных: " + e.getMessage());
            response.setSuccess(false);

            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        } catch (Exception e) {
            // Общая ошибка сервера
            Response response = new Response();
            response.setMessage("Ошибка при добавлении расписания: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        }
    }
    private void handleAddCourse(Request request) {
        try {
            // Получаем сообщение, которое содержит два JSON объекта, разделённых ";"
            String combinedMessage = request.getMessage();

            // Разделяем строку по символу ";"
            String[] jsonParts = combinedMessage.split(";");

            if (jsonParts.length != 2) {
                throw new IllegalArgumentException("Некорректный формат данных.");
            }

            // Десериализуем оба объекта из JSON строк
            Course course = gson.fromJson(jsonParts[0], Course.class);
            Price_list priceList = gson.fromJson(jsonParts[1], Price_list.class);

            // Проверка данных на null, можно добавить дополнительные проверки в зависимости от требований
            if (course == null || priceList == null) {
                throw new IllegalArgumentException("Ошибка при десериализации данных.");
            }

            // Сохранение Course в базе данных
            courseService.insert(course);

            // Сохранение Price_list, ассоциируя с сохранённым курсом
            priceList.setCourse(course);
            priceListService.insert(priceList);

            // Формируем успешный ответ
            Response response = new Response();
            response.setMessage("Курс успешно добавлен.");
            response.setSuccess(true);
            response.setResponseType(RequestType.AddCourse);

            // Отправка успешного ответа обратно на клиент
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (IllegalArgumentException e) {
            // Формируем ответ с ошибкой, если данные невалидны
            Response response = new Response();
            response.setMessage("Ошибка данных: " + e.getMessage());
            response.setSuccess(false);
            response.setResponseType(RequestType.AddCourse);

            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        } catch (Exception e) {
            // Общая ошибка сервера
            Response response = new Response();
            response.setMessage("Ошибка при добавлении курса: " + e.getMessage());
            response.setSuccess(false);


            writer.println(gson.toJson(response));
            writer.flush();
            e.printStackTrace();
        }
    }

    public void handleTeacherMenu(Request request) {
        try {
            // Получение списков курсов, прайс-листов и отзывов
            List<Course> courses = courseService.findAll();
            List<Price_list> price_lists = priceListService.findAll();
            List<Feedback> feedbacks = feedbackService.findAll();
            List<Group> groups = groupService.findAll();
            List<Schedule> schedules = scheduleService.findAll();
            List<Test> tests = testService.findAll();


            // Создание карты с курсами, где ключ - это courseId, а значение - название курса
            Map<Integer, String> courseNames = new HashMap<>();
            for (Course course : courses) {
                courseNames.put(course.getId(), course.getCourse_type());
            }

            // Связывание прайс-листов с названиями курсов
            for (Price_list price_list : price_lists) {
                String courseName = courseNames.get(price_list.getCourse().getId());
                price_list.setCourseName(courseName);
            }

            // Установка имени пользователя для каждого отзыва
            for (Feedback feedback : feedbacks) {
                if (feedback.getUser() != null) {
                    feedback.setAuthor(feedback.getUser().getUsername());
                }
            }
            for (Group group : groups) {
                if (group.getTeacher() != null) {
                    group.setTeacherName(group.getTeacher().getProfile().getLastname());
                }
                if (group.getLanguage() != null) {
                    group.setLanguageName(group.getLanguage().getLanguageType());
                }
                if (group.getSchedule() != null) {
                    group.setScheduleDate(group.getSchedule().getDate());
                    group.setScheduleTime(group.getSchedule().getTime());
                }
                for (Schedule schedule : schedules) {
                    if (group.getSchedule() != null){
                        schedule.setGroupa(group.getId());
                    }
                }
                for(Test test: tests){
                    if(test.getStudent().getProfile() != null){
                        test.setLastname(test.getStudent().getProfile().getLastname());
                    }

                }

            }



            // Преобразование списков в JSON
            String coursesJson = gson.toJson(courses);
            String priceListsJson = gson.toJson(price_lists);
            String feedbacksJson = gson.toJson(feedbacks);
            String groupsJson = gson.toJson(groups);
            String schedulesJson = gson.toJson(schedules);
            String testsJson = gson.toJson(tests);


            // Отправка ответа клиенту
            response.setMessage(coursesJson + ";" + priceListsJson + ";" + feedbacksJson+ ";" + groupsJson + ";" + schedulesJson + ";"+testsJson+";") ;
            response.setSuccess(true);
            response.setResponseType(RequestType.TeacherMenu);
            System.out.println(response.getMessage());

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }
    private void handleGetGroupMembers(Request request, Response response) {
        try {
            int groupId = Integer.parseInt(request.getMessage());
            List<Student> students = studentService.getStudentsByGroupId(groupId);
            for(Student student : students){
                if(student.getProfile() != null){
                    student.setLastname(student.getProfile().getLastname());
                    student.setName(student.getProfile().getName());
                    student.setPhonenumber(student.getProfile().getPhonenumber());
                }
            }


            String studentsJson = gson.toJson(students);
            response.setSuccess(true);
            response.setMessage(studentsJson+";");
            System.out.println(response.getMessage());
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при получении студентов группы: " + e.getMessage());
        }
    }

    public void HandleAddStudentApplication(Request request){
        List<Application> studentApplications = applicationService.findByType(1);
        for (Application studentApplication : studentApplications) {
            if (studentApplication.getType_application().getId()== 1){
                if(studentApplication.getProfile() != null) {
                    studentApplication.setName(studentApplication.getProfile().getName());
                    studentApplication.setLastname(studentApplication.getProfile().getLastname());
                }
                if(studentApplication.getLanguage() != null) {
                    studentApplication.setLanguageName(studentApplication.getLanguage().getLanguageType());
                }
            }
            else continue;
        }
        List<Integer> groupIds = groupService.findAllGroupIds();
        String groupIdsJson = gson.toJson(groupIds);
        String StApplicationJson = gson.toJson(studentApplications);
        response.setMessage(StApplicationJson + ";" + groupIdsJson);
        response.setSuccess(true);
        response.setResponseType(RequestType.StudentApplication);
        System.out.println(response.getMessage());

        writer.println(gson.toJson(response));
        writer.flush();
    }
    public void HandleAddTeacherApplication(Request request){
        List<Application> teacherApplications = applicationService.findByType(2);
        for (Application teacherApplication : teacherApplications) {
            if (teacherApplication.getType_application().getId()== 2){
                if(teacherApplication.getProfile() != null) {
                    teacherApplication.setName(teacherApplication.getProfile().getName());
                    teacherApplication.setLastname(teacherApplication.getProfile().getLastname());
                }
                if(teacherApplication.getLanguage() != null) {
                    teacherApplication.setLanguageName(teacherApplication.getLanguage().getLanguageType());
                }
            }
            else continue;
        }
        String TrApplicationJson = gson.toJson(teacherApplications);
        response.setMessage(TrApplicationJson + ";");
        response.setSuccess(true);
        response.setResponseType(RequestType.TeacherApplication);
        System.out.println(response.getMessage());

        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetTeacherNames(Request request) {
        try {
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> teacherIds = gson.fromJson(request.getMessage(), listType);

            Map<Integer, String> teacherNames = new HashMap<>();
            for (Integer id : teacherIds) {
                Teacher teacher = teacherService.findById(id);
                if (teacher != null && teacher.getProfile() != null) {
                    teacherNames.put(id, teacher.getProfile().getLastname() + " " + teacher.getProfile().getName());
                }
            }

            response.setMessage(gson.toJson(teacherNames));
            response.setSuccess(true);
            response.setResponseType(RequestType.TeacherNames);
        } catch (Exception e) {
            response.setMessage("Ошибка получения имен преподавателей: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetLanguageNames(Request request) {
        try {
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> languageIds = gson.fromJson(request.getMessage(), listType);

            Map<Integer, String> languageNames = new HashMap<>();
            for (Integer id : languageIds) {
                Language language = languageService.findById(id);
                if (language != null) {
                    languageNames.put(id, language.getLanguageType());
                }
            }

            response.setMessage(gson.toJson(languageNames));
            response.setSuccess(true);
            response.setResponseType(RequestType.LanguageNames);
        } catch (Exception e) {
            response.setMessage("Ошибка получения названий языков: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetCourseNames(Request request) {
        try {
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> courseIds = gson.fromJson(request.getMessage(), listType);

            Map<Integer, String> courseNames = new HashMap<>();
            for (Integer id : courseIds) {
                Course course = courseService.findById(id);
                if (course != null) {
                    courseNames.put(id, course.getCourse_type());
                }
            }

            response.setMessage(gson.toJson(courseNames));
            response.setSuccess(true);
            response.setResponseType(RequestType.CourseNames);
        } catch (Exception e) {
            response.setMessage("Ошибка получения названий курсов: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetScheduleNames(Request request) {
        try {
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> scheduleIds = gson.fromJson(request.getMessage(), listType);

            Map<Integer, String> scheduleNames = new HashMap<>();
            for (Integer id : scheduleIds) {
                Schedule schedule = scheduleService.findById(id);
                if (schedule != null) {
                    scheduleNames.put(id, schedule.getDate() + " " + schedule.getTime());
                }
            }

            response.setMessage(gson.toJson(scheduleNames));
            response.setSuccess(true);
            response.setResponseType(RequestType.ScheduleNames);
        } catch (Exception e) {
            response.setMessage("Ошибка получения названий расписаний: " + e.getMessage());
            response.setSuccess(false);
            e.printStackTrace();
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleGetTeacherData(Request request) {
        try {
            int teacherId = Integer.parseInt(request.getMessage());
            Teacher teacher = teacherService.findById(teacherId);

            if (teacher != null && teacher.getProfile() != null) {
                Map<String, String> teacherData = new HashMap<>();
                teacherData.put("name", teacher.getProfile().getName());
                teacherData.put("lastname", teacher.getProfile().getLastname());
                teacherData.put("phone", teacher.getProfile().getPhonenumber());

                response.setMessage(gson.toJson(teacherData));
                response.setSuccess(true);
            } else {
                response.setMessage("Преподаватель не найден");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage("Ошибка получения данных: " + e.getMessage());
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleStudentNames(Request request) {
        try {
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> studentIds = gson.fromJson(request.getMessage(), listType);

            Map<Integer, String> studentNamesMap = new HashMap<>();
            for (Integer id : studentIds) {
                // Здесь должна быть ваша логика получения имени студента по ID из БД
                // Например:
                Student student = studentService.findById(id); // Предполагается наличие такого сервиса
                if (student != null && student.getProfile() != null) {
                    // Формируем полное имя: "Фамилия Имя"
                    String fullName = student.getProfile().getLastname() + " " + student.getProfile().getName();
                    studentNamesMap.put(id, fullName);
                }
            }

            response.setMessage(gson.toJson(studentNamesMap));
            response.setSuccess(true);
        } catch (Exception e) {
            response.setMessage("Ошибка получения имен студентов: " + e.getMessage());
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void handleGetStudentData(Request request) {
        try {
            int studentId = Integer.parseInt(request.getMessage());
            Student student = studentService.findById(studentId); // Предполагается наличие такого сервиса

            if (student != null && student.getProfile() != null) {
                Map<String, String> studentData = new HashMap<>();
                studentData.put("name", student.getProfile().getName());
                studentData.put("lastname", student.getProfile().getLastname());
                studentData.put("phone", student.getProfile().getPhonenumber()); // Убедитесь, что поле называется "phonenumber" в StudentProfile

                response.setMessage(gson.toJson(studentData));
                response.setSuccess(true);
            } else {
                response.setMessage("Студент не найден");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage("Ошибка получения данных: " + e.getMessage());
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

}
