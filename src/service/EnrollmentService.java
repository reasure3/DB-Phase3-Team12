package service;

import dao.EnrollmentDAO;
import dao.BasketDAO;
import model.Student;

import java.sql.SQLException;

public class EnrollmentService {
        private EnrollmentDAO enrollmentDAO;
        private BasketDAO basketDAO;

        public EnrollmentService(EnrollmentDAO enrollmentDAO, BasketDAO basketDAO) {
                this.enrollmentDAO = enrollmentDAO;
                this.basketDAO = basketDAO;
        }

        /**
         * 1. 나의 등록 조회
         */
        public void queryMyEnrollment(Student loggedInStudent) throws SQLException {
                try {
                        long studentId = loggedInStudent.getStudentId();
                        System.out.println("\n[나의 등록 조회] - 학생 ID: " + studentId);
                        enrollmentDAO.printMyEnrollment(studentId);
                } catch (Exception e) {
			System.out.println("나의 등록 조회 중 오류: " + e.getMessage());
		}
	}

	/**
         * 2. 등록 취소
         */
        public void cancelEnrollment(Student loggedInStudent, String cancelSectionId) throws SQLException {
                try {
                        long studentId = loggedInStudent.getStudentId();

                        // 1) ENROLLMENT 삭제
			int deletedEnr = enrollmentDAO.deleteEnrollment(studentId, cancelSectionId);

			// 2) BASKET → basketId 조회
			String basketId = basketDAO.getBasketId(studentId);

			// 3) basketId + sectionId 로 BasketItem 삭제
			int deletedBasket = basketDAO.deleteSectionFromBasket(basketId, cancelSectionId);

			if (deletedEnr == 0 && deletedBasket == 0) {
				System.out.println("해당 분반의 등록 또는 장바구니 내역이 없습니다.");
			} else {
				System.out.println("등록 취소가 완료되었습니다.");
			}

		} catch (Exception e) {
			System.out.println("등록 취소 중 오류 발생: " + e.getMessage());
		}
	}

	/**
         * 3. 학과별 수강인원 조회
         */
        public void queryEnrollmentByDepartment(String dept) throws SQLException {
                try {
                        enrollmentDAO.printEnrollmentByDepartment(dept);
                } catch (Exception e) {
                        System.out.println("학과별 수강인원 조회 중 오류: " + e.getMessage());
                }
	}

	/**
	 * 학과별 수강인원 통계를 담는 내부 클래스
	 */
	public static class EnrollmentStat {
		private String courseId;
		private String courseName;
		private int sectionCount;
		private int totalEnrollments;

		public EnrollmentStat(String courseId, String courseName, int sectionCount, int totalEnrollments) {
			this.courseId = courseId;
			this.courseName = courseName;
			this.sectionCount = sectionCount;
			this.totalEnrollments = totalEnrollments;
		}

		public String getCourseId() {
			return courseId;
		}

		public String getCourseName() {
			return courseName;
		}

		public int getSectionCount() {
			return sectionCount;
		}

		public int getTotalEnrollments() {
			return totalEnrollments;
		}
	}
}