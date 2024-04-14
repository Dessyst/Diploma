package officeManager.controller;

import officeManager.dto.MeetingDto;
import officeManager.entity.Meeting;
import officeManager.entity.Room;
import officeManager.entity.User;
import officeManager.service.MeetingService;
import officeManager.service.UserService;
import officeManager.service.RoomService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("meeting")
public class MeetingController {
    private MeetingService meetingService;
    private UserService userService;
    private RoomService roomService;

    public MeetingController(MeetingService meetingService, UserService userService, RoomService roomService) {
        this.meetingService = meetingService;
        this.userService = userService;
        this.roomService = roomService;

    }

    @GetMapping("")
    public String listMeetings(Model model) {
        List<Meeting> meetings = meetingService.findAllMeetings();
        model.addAttribute("meetings", meetings);
        return "meetingsall";
    }

    @GetMapping("adduser/{meetingId}")
    public String showAddUserToMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("meeting", meeting);
        return "addUserToMeet";
    }

    @PostMapping("adduser/{meetingId}")
    public String addUserToMeeting(@RequestParam("userId") String userId, @PathVariable int meetingId) {
        User user1 = userService.findById(Integer.parseInt(String.valueOf(userId)));
        Meeting meeting1 = meetingService.findById(meetingId);

        meetingService.addUser(user1, meeting1);

        return "redirect:/meeting/adduser/{meetingId}";
    }

    @GetMapping("edit/{meetingId}")
    public String showEditMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "editMeeting";
    }

    @PostMapping("edit/{meetingId}")
    public String editMeeting(@ModelAttribute MeetingDto meetingDto, @PathVariable int meetingId) {
        meetingService.editMeeting(meetingDto, meetingId);
        return "redirect:/meeting/{meetingId}";
    }

    @GetMapping("{meetingId}")
    public String showMeetingDetails(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "meetingDetails";
    }

    @GetMapping("passing")
    public String Passing() {
        int meetingCount = meetingService.getLastMeeting();

        return "redirect:/addusersorlater/" + meetingCount;
    }

    @GetMapping("addusersorlater/{meetingId}")
    public String addUsersOrLater(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);

        return "addUsersOrLater";
    }

    @GetMapping("mymeetings")
    public String showMyMeetings(Model model) {
        List<Meeting> meetings = meetingService.findAllMeetings();
        List<Meeting> myMeetings = new ArrayList<>();
        for(Meeting meeting: meetings){
            if(meeting.getCreatorId() == currentUserId()){
                myMeetings.add(meeting);
            }
        }
        model.addAttribute("meetings", myMeetings);
        return "myMeetings";
    }

    @GetMapping("delete/{meetingId}")
    public String showDeleteMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "deleteMeeting";
    }

    @PostMapping("delete/{meetingId}")
    public String deleteMeeting(@PathVariable int meetingId){
        meetingService.deleteMeeting(meetingId);
        return "redirect:/meeting";
    }

    @GetMapping("/agendaDay")
    public String showAgendaForDate(Model model, @RequestParam("date") String date) {
        List<Meeting> meetings = meetingService.findMeetingsForDate(LocalDate.parse(date).atStartOfDay());
        model.addAttribute("events", meetings);
        return "agenda";
    }

    int currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByEmail(authentication.getName()).getId();
        }
        return 0;
    }
}
