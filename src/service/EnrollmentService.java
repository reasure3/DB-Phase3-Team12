package service;

import dao.EnrollmentDAO;
import dao.BasketDAO;
import model.Student;

import java.sql.SQLException;
import java.util.Scanner;

public class EnrollmentService {
	private EnrollmentDAO enrollmentDAO;
	private BasketDAO basketDAO;
	private Scanner sc;

	public EnrollmentService(EnrollmentDAO enrollmentDAO, BasketDAO basketDAO, Scanner sc) {
		this.enrollmentDAO = enrollmentDAO;
		this.basketDAO = basketDAO;
		this.sc = sc;
	}

	/**
	 * 등록 메뉴
	 */
	public void manageEnrollment(Student loggedInStudent) {
		System.out.println("\n==== 등록 메뉴 ====");
		System.out.println("1. 나의 등록 조회");
		System.out.println("2. 등록 취소");
		System.out.println("3. 학과별 수강인원 조회");
		System.out.println("0. 뒤로 가기");
		System.out.print("선택: ");

		int choice = sc.nextInt();
		sc.nextLine();

		try {
			switch (choice) {
			case 1:
				queryMyEnrollment(loggedInStudent);
				break;
			case 2:
				cancelEnrollment(loggedInStudent);
				break;
			case 3:
				queryEnrollmentByDepartment();
				break;
			case 0:
				return;
			default:
				System.out.println("잘못된 입력입니다. 다시 선택하세요.");
			}
		} catch (SQLException e) {
			System.out.println("에러 발생: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 1. 나의 등록 조회
	 */
	private void queryMyEnrollment(Student loggedInStudent) throws SQLException {
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
	private void cancelEnrollment(Student loggedInStudent) throws SQLException {
		System.out.print("취소할 분반의 SECTION_ID 입력: ");
		String cancelSectionId = sc.nextLine();

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
	private void queryEnrollmentByDepartment() throws SQLException {
		System.out.print("조회할 학과 입력: ");
		String dept = sc.nextLine();

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