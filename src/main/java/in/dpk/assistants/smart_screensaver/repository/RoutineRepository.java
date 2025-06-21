package in.dpk.assistants.smart_screensaver.repository;

import in.dpk.assistants.smart_screensaver.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByEnabledTrueOrderByPriorityDesc();
} 