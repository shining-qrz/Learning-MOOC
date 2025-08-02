package edu.wust.qrz;

import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.service.CourseBaseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseTest {

    @Resource
    CourseBaseService courseBaseService;

    @Test
    public void getCourseByIdTest(){
        CourseBase courseBase = courseBaseService.getById(18);

        System.out.println(courseBase);
    }

    @Test
    public void getEnv(){
        System.out.println(System.getenv("DB_PASSWORD"));
        System.out.println(System.getenv("DB_PORT"));
    }
}
