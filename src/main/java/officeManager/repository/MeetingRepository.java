package officeManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import officeManager.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Integer>  {
    Meeting findByName(String name);
}
