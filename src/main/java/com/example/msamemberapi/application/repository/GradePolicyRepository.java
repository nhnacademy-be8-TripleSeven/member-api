package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GradePolicyRepository extends JpaRepository<GradePolicy, Long> {

    Optional<GradePolicy> findByGrade(MemberGrade grade);
    @Query("SELECT gp FROM GradePolicy gp WHERE gp.min <= :spending AND (:spending < gp.max OR gp.max IS NULL)")
    GradePolicy findCurrentGrade(int spending);

    @Query("SELECT gp FROM GradePolicy gp WHERE gp.min > :spending ORDER BY gp.min ASC")
    GradePolicy findNextGrade(int spending);
}
