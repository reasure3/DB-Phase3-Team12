package service;

import dao.CourseDAO;
import model.Student;

import java.sql.SQLException;
import java.util.Scanner;

public class CourseService {
    private CourseDAO courseDAO;
    private Scanner sc;
    
    public CourseService(CourseDAO courseDAO, Scanner sc) {
        this.courseDAO = courseDAO;
        this.sc = sc;
    }
    
    /**
     * 강의 조회 메뉴
     */
    public void manageCourse(Student loggedInStudent) {
        System.out.println("\n========== 강의 조회 ==========");
        System.out.println("1. 전체 강의 조회");
        System.out.println("2. 나의 강의 조회");
        System.out.println("3. 학과별 강의 조회");
        System.out.println("0. 뒤로가기");
        System.out.println("==============================");
        System.out.print("선택: ");
        
        int choice = sc.nextInt();
        sc.nextLine();
        
        try {
            switch (choice) {
                case 1:
                    queryAllCourses();
                    break;
                case 2:
                    queryMyCourses(loggedInStudent);
                    break;
                case 3:
                    queryCoursesByDepartment();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        } catch (SQLException e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 1. 전체 강의 조회
     */
    private void queryAllCourses() throws SQLException {
        courseDAO.printAllCourses();
    }
    
    /**
     * 2. 나의 강의 조회 (수강신청한 강의)
     */
    private void queryMyCourses(Student loggedInStudent) throws SQLException {
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
    private void queryCoursesByDepartment() throws SQLException {
    	System.out.print("조회할 학과 입력: ");
        String dept = sc.nextLine();
        try {
            System.out.println("\n[학과별 강의 조회] - " + dept);
            courseDAO.printCoursesByDepartment(dept);
        } catch (Exception e) {
            System.out.println("학과별 강의 조회 중 오류: " + e.getMessage());
        }
    }
}