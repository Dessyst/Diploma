package officeManager.controller;

import officeManager.dto.UserDto;
import officeManager.entity.User;
import officeManager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String listRegisteredUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("{userId}")
    public String showUserDetails(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "userDetails";
    }

    @GetMapping("edit/{userId}")
    public String showEditUserForm(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("edit/{userId}")
    public String editUser(@ModelAttribute UserDto userDto, @PathVariable int userId) {
        userService.editUser(userDto, userId);
        return "redirect:/user/{userId}";
    }

    @GetMapping("myaccount")
    public String showMyAccount(Model model) {
        model.addAttribute("currentId", currentUserId());

        User user = userService.findById(currentUserId());
        model.addAttribute("user", user);

        return "myAccount";
    }

    @GetMapping("currentuser")
    public String getCurrentUsername(Model model) {
        model.addAttribute("currentId", currentUserId());

        return "currentuser";
    }

    @GetMapping("delete/{userId}")
    public String showDeleteUserForm(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "deleteUser";
    }

    @PostMapping("delete/{userId}")
    public String deleteUser(@PathVariable int userId){
        userService.deleteUser(userId);
        return "redirect:/user";
    }

    int currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByEmail(authentication.getName()).getId();
        }
        return 0;
    }
}
