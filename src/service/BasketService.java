package service;

import dao.BasketDAO;
import model.Student;

import java.sql.SQLException;

public class BasketService {
    private BasketDAO basketDAO;

    public BasketService(BasketDAO basketDAO) {
        this.basketDAO = basketDAO;
    }

    public void handleAddSectionToBasket(Student student, String sectionId) {
        long studentId = student.getStudentId();

        try {
            // 1) 장바구니 없으면 생성
            basketDAO.ensureBasketExists(studentId);

            // 2) 이미 해당 분반이 담겨 있는지 확인
            if (basketDAO.isSectionInBasket(studentId, sectionId)) {
                System.out.println("이미 이 분반은 수강꾸러미에 담겨 있습니다.");
                return;
            }

            boolean enrolled = basketDAO.addSectionToBasket(studentId, sectionId);

            if (enrolled) {
                System.out.println("분반을 수강꾸러미에 담았고, 정원 내라서 수강신청까지 완료되었습니다.");
            } else {
                System.out.println("정원 초과로 인해 이 분반은 경매 상태로 처리되었습니다.");
            }

        } catch (Exception e) {
            System.out.println("분반 담기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 2. 나의 수강꾸러미 조회
     */
    public void queryMyBasket(Student loggedInStudent) throws SQLException {
        try {
            long studentId = loggedInStudent.getStudentId();
            System.out.println("\n[나의 수강꾸러미 조회] - 학생 ID: " + studentId);
            basketDAO.printMyBasket(studentId);
        } catch (Exception e) {
            System.out.println("수강꾸러미 조회 중 오류: " + e.getMessage());
        }
    }
}