package service;

import dao.StudentDAO;
import model.Student;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthService {
    private StudentDAO studentDAO;
    private Scanner sc;
    
    public AuthService(StudentDAO studentDAO, Scanner sc) {
        this.studentDAO = studentDAO;
        this.sc = sc;
    }
    
    /**
     * 로그인 처리
     * @return 로그인 성공한 Student 객체, 실패 시 null
     */
    public Student handleLogin() {
        try {
            System.out.println("\n==== 로그인 ====");
            System.out.print("학번 입력: ");
            int studentId = sc.nextInt();
            sc.nextLine(); // 엔터 제거

            System.out.print("비밀번호 입력: ");
            String password = sc.nextLine();

            Student student = studentDAO.login(studentId, password);

            if (student != null) {
                System.out.println("로그인 성공! " + student.getName() + "님 환영합니다.");
                return student;
            } else {
                System.out.println("로그인 실패! 학번 또는 비밀번호가 올바르지 않습니다.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("로그인 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 회원가입 처리
     * @return 성공 여부
     */
    public boolean handleSignUp() {
        try {
            System.out.println("\n==== 회원가입 ====");

            System.out.print("학번: ");
            int studentId = sc.nextInt();
            sc.nextLine(); // 엔터 제거

            // 중복 학번 체크
            if (studentDAO.existsById(studentId)) {
                System.out.println("이미 존재하는 학번입니다.");
                return false;
            }

            System.out.print("이름: ");
            String name = sc.nextLine();

            System.out.print("학과: ");
            String department = sc.nextLine();

            System.out.print("학년 (1-4): ");
            int grade = sc.nextInt();
            sc.nextLine(); // 엔터 제거

            System.out.print("비밀번호: ");
            String password = sc.nextLine();
            
            System.out.print("최대 학점 (18/21/22): ");
            int maxCredits = sc.nextInt();
            sc.nextLine();
            
            int maxPoint = maxCredits * 5;  // 자동 계산

            // Student 객체 생성
            Student student = new Student();
            student.setStudentId(studentId);
            student.setName(name);
            student.setDepartment(department);
            student.setGrade(grade);
            student.setPassword(password);
            student.setMaxCredits(maxCredits);
            student.setMaxPoint(maxPoint);

            int result = studentDAO.signUp(student);

            if (result > 0) {
                System.out.println("회원가입 성공! 이제 로그인 해주세요.");
                return true;
            } else {
                System.out.println("회원가입 실패.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("회원가입 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
}