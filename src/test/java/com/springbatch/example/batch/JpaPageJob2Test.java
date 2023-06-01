package com.springbatch.example.batch;

import com.springbatch.example.batch.JpaPageJob2;
import com.springbatch.example.domain.Dept;
import com.springbatch.example.domain.Dept2;
import com.springbatch.example.domain.Dept2Repository;
import com.springbatch.example.domain.DeptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = JpaPageJob2.class)
@EnableBatchProcessing
public class JpaPageJob2Test {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private Dept2Repository dept2Repository;

    @Test
    public void testJpaPageJob2() throws Exception {
        // Given
        // 데이터베이스에 테스트를 위한 Dept 엔티티를 저장합니다.
        Dept dept1 = new Dept(1, "Dept1", "Location1");
        Dept2 dept2 = new Dept2(2, "Dept2", "Location2");
        deptRepository.save(dept1);
        dept2Repository.save(dept2);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // When
        // JpaPageJob2를 실행합니다.
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        // 배치 작업이 성공적으로 완료되었는지 확인합니다.
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        // 변환된 데이터가 정상적으로 저장되었는지 확인합니다.
        Iterable<Dept2> savedDept2 = dept2Repository.findAll();
        assertThat(savedDept2).hasSize(2);
        assertThat(savedDept2).contains(
                new Dept2(dept1.getDeptNo(), "NEW_" + dept1.getDName(), "NEW_" + dept1.getLoc()),
                new Dept2(dept2.getDeptNo(), "NEW_" + dept2.getDName(), "NEW_" + dept2.getLoc())
        );
    }
}
