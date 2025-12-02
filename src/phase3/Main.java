package phase3;

import db.DBConnection;
import dao.*;
import service.*;
import model.Student;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // DB 연결
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("[FAILED] DB Connection Failed!");
            return;
        }
        System.out.println("[SUCCESS] DB Connected Successfully!");
        
        // Scanner 생성
        Scanner sc = new Scanner(System.in);
        
        // DAO 객체 생성
        StudentDAO studentDAO = new StudentDAO(conn);
        CourseDAO courseDAO = new CourseDAO(conn);
        BasketDAO basketDAO = new BasketDAO(conn);
        SectionDAO sectionDAO = new SectionDAO(conn); 
        BidDAO bidDAO = new BidDAO(conn);
        AuctionDAO auctionDAO = new AuctionDAO(conn);
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO(conn);
        
        // Service 객체 생성
        AuthService authService = new AuthService(studentDAO, sc);
        CourseService courseService = new CourseService(courseDAO, sc);
        SectionService sectionService = new SectionService(sectionDAO, courseDAO, sc);
        BasketService basketService = new BasketService(basketDAO, sc);
        AuctionService auctionService = new AuctionService(auctionDAO, bidDAO, studentDAO, sc);
        EnrollmentService enrollmentService = new EnrollmentService(enrollmentDAO, basketDAO, sc);
        
        Student loggedInStudent = null;
        boolean running = true;

        while (running) {
            if (loggedInStudent == null) {
                // 로그인 전 메뉴
                System.out.println("\n========================================");
                System.out.println("   경매 기반 수강신청 시스템");
                System.out.println("========================================");
                System.out.println("1. 로그인");
                System.out.println("2. 회원가입");
                System.out.println("0. 종료");
                System.out.println("========================================");
                System.out.print("선택: ");

                int choice = sc.nextInt();
                sc.nextLine(); // 엔터 제거

                switch (choice) {
                    case 1:
                        loggedInStudent = authService.handleLogin();
                        break;
                    case 2:
                        authService.handleSignUp();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } else {
                // 로그인 후 메뉴
                System.out.println("\n========================================");
                System.out.println("   경매 기반 수강신청 시스템");
                System.out.println("   [" + loggedInStudent.getName() + "님 로그인 중]");
                System.out.println("========================================");
                System.out.println("1. 강의 조회");
                System.out.println("2. 분반 조회");
                System.out.println("3. 수강꾸러미");
                System.out.println("4. 경매 참여");
                System.out.println("5. 등록조회");
                System.out.println("6. 종료 (로그아웃)");
                System.out.println("========================================");
                System.out.print("선택: ");
                
                int choice = sc.nextInt();
                sc.nextLine();
                
                switch (choice) {
                    case 1:
                        // TODO: 쿼리 실행 Service (다른 팀원)
                    	courseService.manageCourse(loggedInStudent);
                        break;
                    case 2:
                        sectionService.manageSectionQuery(loggedInStudent);;
                        break;
                    case 3:
                    	basketService.manageBasket(loggedInStudent);
                        break;
                    case 4:
                    	auctionService.manageAuction(loggedInStudent);
                        break;
                    case 5:
                    	enrollmentService.manageEnrollment(loggedInStudent);
                        break;
                    case 6:
                    	System.out.println("로그아웃 되었습니다.");
                        loggedInStudent = null;
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("잘못된 선택입니다.");
                }
            }
        }
        
        System.out.println("프로그램 종료!");
        
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sc.close();
    }
}