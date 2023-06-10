package com.springbatch.example.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestDeptRepository {

    @Autowired
    DeptRepository deptRepository;

    private int testCount = 100;

    @Test
    @Commit
    public void dept01() {
        for (int i = 1; i < testCount+1; i++) {
            deptRepository.save(new Dept(i, "dname_" + String.valueOf(i), "loc_" + String.valueOf(i)));
        }
    }


    @Test
    @Commit
    public void dept01_read() {
        List<Dept> deptList = (List<Dept>) deptRepository.findAll();
        assertEquals(deptList.size(),testCount);
    }
}
