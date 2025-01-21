package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradePolicyRepository extends JpaRepository<GradePolicy, Long> {

    boolean existsByName(String name);
    Optional<GradePolicy> findGradeByName(String name);
    Optional<GradePolicy> findByGrade(MemberGrade grade);

    GradePolicy findByMinLessThanEqualOrderByMinDesc(int spending);

    GradePolicy findByMinGreaterThanOrderByMinAsc(int spending);

    @Query("SELECT g FROM GradePolicy g WHERE g.min <= ?1 ORDER BY g.min DESC")
    GradePolicy findCurrentGrade(int spending);

    @Query("SELECT g FROM GradePolicy g WHERE g.min > ?1 ORDER BY g.min ASC")
    GradePolicy findNextGrade(int spending);

}

