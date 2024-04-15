package officeManager.controller;

import officeManager.dto.UserDto;
import officeManager.entity.Meeting;
import officeManager.entity.User;
import officeManager.service.RoomService;
import officeManager.service.UserService;
import officeManager.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;


@Controller
public class MainController {

    private UserService userService;
    private MeetingService meetingService;
    private RoomService roomService;

    public MainController(UserService userService, MeetingService meetingService, RoomService roomService) {
        this.userService = userService;
        this.meetingService = meetingService;
        this.roomService = roomService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("user") UserDto user, BindingResult result, Model model) {

        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/passing")
    public String Passing() {
        int meetingCount = meetingService.getLastMeeting();

        return "redirect:/meeting/addusersorlater/" + meetingCount;
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {
        List<Meeting> meetings = meetingService.findAllMeetings();

        List<Map<String, Object>> events = new ArrayList<>();
        for (Meeting meeting : meetings) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", meeting.getName());
            event.put("start", meeting.getStartTime().toString());
            event.put("end", meeting.getEndTime().toString());
            events.add(event);
        }

        return events;
    }

    @GetMapping("/myevents")
    @ResponseBody
    public List<Map<String, Object>> getMyEvents() {
        int currentId = currentUserId();
        List<Meeting> meetings = userService.findAllMeetingsForUser(currentId);

        List<Map<String, Object>> events = new ArrayList<>();
        for (Meeting meeting : meetings) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", meeting.getName());
            event.put("start", meeting.getStartTime().toString());
            event.put("end", meeting.getEndTime().toString());
            events.add(event);
        }

        return events;
    }

    @GetMapping("currentuser")
    public String getCurrentUsername(Model model) {
        model.addAttribute("currentId", currentUserId());

        return "currentuser";
    }

    int currentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByEmail(authentication.getName()).getId();
        }
        return 0;
    }
}
