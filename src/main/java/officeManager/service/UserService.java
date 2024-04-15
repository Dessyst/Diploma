package officeManager.service;

import officeManager.dto.UserDto;
import officeManager.entity.Meeting;
import officeManager.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    void deleteUser(int id);

    User findByEmail(String email);

    User findById(int id);

    List<User> findAllUsers();

    List<Meeting> findAllMeetingsForUser(int id);

    void editUser(UserDto userDto, int userId);
}
