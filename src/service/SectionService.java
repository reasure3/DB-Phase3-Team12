package service;

import dao.CourseDAO;
import dao.SectionDAO;
import model.Course;
import model.Section;
import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SectionService {
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;

    public SectionService(SectionDAO sectionDAO, CourseDAO courseDAO) {
        this.sectionDAO = sectionDAO;
        this.courseDAO = courseDAO;
    }
    
    /**
     * 1. 강의 코드로 분반 조회
     */
    public void querySectionByCourseId(String courseId) throws SQLException {
        courseId = courseId.toUpperCase();

        List<Section> sections = sectionDAO.selectByCourseId(courseId);
        Course course = courseDAO.selectById(courseId);
        
        if (sections.isEmpty()) {
            System.out.println("해당 강의의 분반이 없습니다.");
            return;
        }
        
        System.out.println("\n========== 분반 목록 ==========");
        System.out.printf("%-15s %-5s %-10s %-25s %-15s %-8s %-12s%n",
            "분반코드", "분반", "교수", "강의명", "학과", "학점", "정원");
        System.out.println("=".repeat(100));
        
        for (Section s : sections) {
            int currentEnrollment = sectionDAO.getCurrentEnrollment(s.getSectionId());
            
            System.out.printf("%-15s %-5d %-10s %-25s %-15s %-8d %d/%d%n",
                s.getSectionId(),
                s.getSectionNumber(),
                s.getProfessor(),
                course.getCourseName(),
                course.getDepartment(),
                course.getCredits(),
                currentEnrollment,
                s.getCapacity()
            );
        }
        System.out.println("=".repeat(100));
        System.out.println("총 " + sections.size() + "개의 분반이 있습니다.");
    }
    
    /**
     * 2. 나의 분반 조회 
     */
    public void queryMySection(Student loggedInStudent) throws SQLException {
        Map<Course, Section> courseSectionMap = sectionDAO.selectMySection(loggedInStudent.getStudentId());
        
        if (courseSectionMap.isEmpty()) {
            System.out.println("수강꾸러미나 수강신청한 분반이 없습니다.");
            return;
        }
        
        System.out.println("\n========== 내 분반 목록 ==========");
        System.out.printf("%-12s %-30s %-15s %-5s %-10s %-10s%n",
            "강의코드", "강의명", "학과", "학점", "분반", "교수");
        System.out.println("=".repeat(100));
        
        for (Map.Entry<Course, Section> entry : courseSectionMap.entrySet()) {
            Course course = entry.getKey();
            Section section = entry.getValue();
            
            System.out.printf("%-12s %-30s %-15s %-5d %-10d %-10s%n",
                course.getCourseId(),
                course.getCourseName(),
                course.getDepartment(),
                course.getCredits(),
                section.getSectionNumber(),
                section.getProfessor()
            );
        }
        System.out.println("=".repeat(100));
        System.out.println("총 " + courseSectionMap.size() + "개의 강의를 신청했습니다.");
        
        // 총 학점 계산
        int totalCredits = 0;
        for (Course course : courseSectionMap.keySet()) {
            totalCredits += course.getCredits();
        }
        System.out.println("총 학점: " + totalCredits + "학점");
    }
}
