package officeManager.controller;

import officeManager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("calendar")
public class CalendarController {

    private UserService userService;

    public CalendarController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String getCalendar(Model model) {
        return "calendar";
    }

    @GetMapping("mycalendar")
    public String getMyCalendar(Model model) {
        return "myCalendar";
    }
}
