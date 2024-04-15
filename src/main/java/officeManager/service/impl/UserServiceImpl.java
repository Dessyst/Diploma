package officeManager.service.impl;

import officeManager.dto.UserDto;
import officeManager.entity.Meeting;
import officeManager.entity.Role;
import officeManager.entity.User;
import officeManager.repository.RoleRepository;
import officeManager.repository.UserRepository;
import officeManager.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        for (String role : userDto.getRoles()) {
            Role roleEntity = roleRepository.findByName(role);
            if (roleEntity == null) {
                roleEntity = new Role();
                roleEntity.setName(role);
                roleRepository.save(roleEntity);
            }
            user.getRoles().add(roleEntity);
        }
        List<Meeting> meetings = userDto.getMeetings();
        user.setMeetings(meetings);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.getReferenceById(id);

        user.getRoles().clear();
        userRepository.deleteById(id);

    }

    @Override
    public void editUser(UserDto userDto, int userId) {
        User user = userRepository.getReferenceById(userId);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        List<Meeting> meetings = userDto.getMeetings();
        user.setMeetings(meetings);
        userRepository.save(user);
    }

    @Override
    public List<Meeting> findAllMeetingsForUser(int id) {
        List<Meeting> meetings = userRepository.getReferenceById(id).getMeetings();
        return meetings;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(int id){
        return userRepository.getReferenceById(id);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users;
    }
}
