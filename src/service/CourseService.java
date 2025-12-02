package service;

import dao.CourseDAO;
import model.Student;

import java.sql.SQLException;

public class CourseService {
    private CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }
    
    /**
     * 1. 전체 강의 조회
     */
    public void queryAllCourses() throws SQLException {
        courseDAO.printAllCourses();
    }
    
    /**
     * 2. 나의 강의 조회 (수강신청한 강의)
     */
    public void queryMyCourses(Student loggedInStudent) throws SQLException {
        try {
            long myId = loggedInStudent.getStudentId();   // 로그인된 학생의 ID
            System.out.println("\n[나의 강의 조회] - 학생 ID: " + myId);
            courseDAO.printMyCourses(myId);
        } catch (Exception e) {
            System.out.println("나의 강의 조회 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 3. 학과별 강의 조회
     */
    public void queryCoursesByDepartment(String dept) throws SQLException {
        try {
            System.out.println("\n[학과별 강의 조회] - " + dept);
            courseDAO.printCoursesByDepartment(dept);
        } catch (Exception e) {
            System.out.println("학과별 강의 조회 중 오류: " + e.getMessage());
        }
    }
}