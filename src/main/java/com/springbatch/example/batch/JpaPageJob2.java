package com.springbatch.example.batch;

import com.springbatch.example.domain.Dept;
import com.springbatch.example.domain.Dept2;
import com.springbatch.example.domain.Dept2Repository;
import com.springbatch.example.domain.DeptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPageJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final Dept2Repository dept2Repository;

    private int chunkSize = 10;

    @Bean
    public Job JpaPageJob2_batchBuild(){
        return jobBuilderFactory.get("JpaPageJob2")
                .start(JpaPageJob2_step2())
                .next(JpaPageJob2_step1()).build();
    }

    @Bean
    public Step JpaPageJob2_step1(){
        return stepBuilderFactory.get("JpaPageJob2_step1")
                .<Dept, Dept2>chunk(chunkSize)
                .reader(jpaPageJob2_dbItemReader())
                .processor(jpaPageJob2_processor())
                .writer(jpaPageJob2_dbItemWriter())
                .build();
    }


    @Bean
    public Step JpaPageJob2_step2(){
        return stepBuilderFactory.get("JpaPageJob2_step2")
                .tasklet((a, b)->{
                    dept2Repository.deleteAll();
                    return RepeatStatus.FINISHED;
                }).build();
    }


    private ItemProcessor<Dept,Dept2> jpaPageJob2_processor() {
        return dept -> {
            return new Dept2(dept.getDeptNo(),"NEW_"+dept.getDName(),"NEW_"+dept.getLoc());
        };
    }

    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob2_dbItemReader(){
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob1_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT d FROM Dept d order by dept_no desc")
                .build();
    }

    @Bean
    public JpaItemWriter<Dept2> jpaPageJob2_dbItemWriter(){
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}