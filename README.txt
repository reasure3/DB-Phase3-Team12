================================================================================
               COMP322 Team Project - Phase 3
              경매 기반 수강신청 시스템 README
================================================================================

Team12


1. 프로젝트 개요
================================================================================
본 프로젝트는 경매 시스템을 활용한 대학 수강신청 시스템입니다.
학생들은 일반 수강꾸러미 방식과 경매 입찰 방식을 통해 수강신청을 할 수 있으며,
JDBC를 사용하여 Oracle Database와 연동되는 콘솔 기반 애플리케이션입니다.


2. 개발 환경
================================================================================
- JDK 버전: Java 11 이상
- IDE: Eclipse IDE
- 데이터베이스: Oracle Database 19c 이상
- JDBC 드라이버: ojdbc10.jar (또는 ojdbc11.jar)

3. 파일 목록
=================================================================================
team12-phase3.zip: jdbc 프로젝트
Team12-Phase3-2.sql: 수정된 데이터 생성 sql
Team12-Phase3-3.sql: 수정된 쿼리 반영된 sql


4. 실행 방법
================================================================================

[5-1. 사전 준비]
1) Oracle Database가 설치되어 있어야 합니다
2) 데이터베이스 사용자 계정이 생성되어 있어야 합니다
    계정명: course_registration, 비밀번호: oracle
    * db.DBConnection.java 참고

[5-2. 데이터베이스 설정]
1) 기존 데이터 지운 후, SQL*Plus 또는 SQL Developer에서 Team12-Phase3-2.sql 실행

[5-3. Eclipse에서 실행]
1) Eclipse에서 File > Import > Existing Projects into Workspace 선택
2) 압축 해제한 프로젝트 폴더 선택
3) Build Path에 ojdbc10/11.jar 추가:
   - 프로젝트 우클릭 > Build Path > Configure Build Path
   - Libraries 탭 > Add External JARs
   - lib/ojdbc10/11.jar 선택
4) phase3/Main.java 실행 (Run As > Java Application)


5. 주요 기능 설명
================================================================================

[6-1. 회원 관리]
- 회원가입: 학번, 이름, 학과, 학년, 비밀번호, 최대학점 입력
- 로그인: 학번과 비밀번호로 인증
- 자동 포인트 계산: 최대학점 × 5 = 최대포인트

[6-2. 강의 조회]
- 전체 강의 조회: 시스템에 등록된 모든 강의 정보
- 나의 강의 조회: 현재 수강신청한 강의 목록
- 학과별 강의 조회: 특정 학과의 강의 검색

[6-3. 분반 조회]
- 강의 코드로 분반 조회: 특정 강의의 모든 분반 정보 및 현재 등록 인원
- 나의 분반 조회: 수강신청한 분반 상세 정보 및 총 학점

[6-4. 수강꾸러미]
- 분반 담기: 원하는 분반을 수강꾸러미에 추가
- 자동 등록 처리: 정원 내인 경우 즉시 수강신청 완료
- 정원 초과 처리: 정원 초과 시 FAILED 상태로 표시
- 나의 수강꾸러미 조회: 담은 분반 목록 확인

[6-5. 경매 참여]
- 학과별 경매 조회: 특정 학과의 진행 중인 경매 목록
- 나의 경매 조회: 참여 가능한 경매 및 내 입찰 정보
- 입찰하기:
  * 보유 포인트 확인
  * 입찰 금액 입력 (남은 포인트 범위 내)
  * 실시간 순위 계산 및 낙찰/탈락 판정
  * 낙찰 시 자동 수강신청 처리

[6-6. 등록 조회 및 관리]
- 나의 등록 조회: 수강신청 완료된 분반 목록
- 등록 취소: 특정 분반의 수강신청 취소
- 학과별 수강인원 조회: 학과별 각 분반의 등록 현황


6. Phase2에서 수정된 쿼리
================================================================================
-- 수정된 query 1
-- 학생의 id 와 pw 를 입력받아 해당 학생의 모든 정보를 조회하기
SELECT student_id, name, department, grade, password, max_credits, max_point
FROM Student 
WHERE student_id = 20000001 AND password = 'pw20000001';

-- 수정된 query 2
-- 학과명을 입력 받아 해당 학과에서 개설된 모든 강의의 정보를 강의 id 순서대로 정렬하여 출력
SELECT 
    course_id,
    course_name,
    department,
    credits,
    capacity,
    semester,
    year
FROM Course
WHERE department = '컴퓨터학부'
ORDER BY course_id;

-- 수정된 query 3
-- 분반 id 를 입력 받아 해당 분반을 수강신청한 학생 수를 출력
SELECT 
    COUNT(*) AS cnt
FROM ENROLLMENT
WHERE section_id = ?;

-- 수정된 query 4
-- 학생의 id 를 입력 받아 해당 학생이 수강신청한 모든 강의의 정보를 강의 id 순서대로 정렬하여 출력
SELECT DISTINCT
    c.course_id,
    c.course_name,
    c.department,
    c.credits,
    c.capacity,
    c.semester,
    c.year
FROM Enrollment e
JOIN Section s ON e.section_id = s.section_id
JOIN Course c ON s.course_id = c.course_id
WHERE e.student_id = 20000001
ORDER BY c.course_id;

-- 수정된 query 5
-- 학생의 id 를 입력 받아 해당 학생이 수강신청한 모든 분반의 정보를 분반 id 순서대로 정렬하여 출력
SELECT s.section_id, s.section_number, s.professor, s.capacity, s.classroom, s.course_id, c.course_name, e.enrollment_source, e.points_used 
FROM Enrollment e 
JOIN Section s ON e.section_id = s.section_id 
JOIN Course c ON s.course_id = c.course_id 
WHERE e.student_id = 20000001 
ORDER BY s.section_id;

-- 수정된 query 6
-- 학생의 id 를 입력 받아 해당 학생이 장바구니에 담은 모든 분반의 정보를 분반 id 의 내림차순으로 정렬하여 출력
SELECT 
    s.section_id,
    s.section_number,
    s.professor,
    s.capacity,
    s.classroom,
    s.course_id,
    c.course_name
FROM Basket b
JOIN BasketItem bi ON b.basket_id = bi.basket_id
JOIN Section s ON bi.section_id = s.section_id
JOIN Course c ON s.course_id = c.course_id
WHERE b.student_id = ?
ORDER BY s.section_id DESC;

-- 수정된 query 7 
-- 학과명을 입력 받아 해당 학과에서 개설된 모든 경매의 정보를 경매 시작 시간의 내림차순으로 정렬하여 출력
SELECT
    a.auction_id,
    a.start_time,
    a.end_time,
    a.status,
    a.available_slots,
    a.created_at,
    a.section_id,
    s.section_number,
    s.professor,
    c.course_id,
    c.course_name,
    c.department,
    c.credits
FROM AUCTION a
JOIN SECTION s ON a.section_id = s.section_id
JOIN COURSE c ON s.course_id = c.course_id
WHERE c.department = ?
  AND a.status IN ('ACTIVE', 'COMPLETED')
ORDER BY a.start_time DESC;

-- 수정된 query 8
-- 강의 id 를 입력 받아 해당 강의의 모든 분반의 정보를 분반 번호 순서대로 정렬하여 출력
SELECT 
    s.section_id,
    s.section_number,
    s.professor,
    s.capacity,
    s.classroom
FROM SECTION s
JOIN COURSE c ON s.course_id = c.course_id
WHERE s.course_id = ?
ORDER BY s.section_number;

-- 수정된 query 9
-- 경매 id 를 입력 받아 해당 경매에 입찰한 모든 입찰 내역을 입찰 금액의 내림차순, 입찰 시간의 오름차순으로 정렬하여 출력
SELECT 
    b.bid_sequence,
    b.bid_amount,
    b.bid_time,
    b.is_successful,
    b.auction_id,
    b.student_id,
    s.name
FROM BID b
JOIN STUDENT s ON b.student_id = s.student_id
WHERE b.auction_id = ?
ORDER BY 
    b.bid_amount DESC,
    b.bid_time ASC;

--수정된 query 10
-- 컴퓨터학부에서 개설된 모든 강의의 분반 id, 강의명, 분반 정원, 그리고 현재 수강신청 인원수를 강의명과 분반 id 순서대로 정렬하여 출력
SELECT 
    s.section_id,
    c.course_name,
    s.capacity AS section_capacity,
    COUNT(e.student_id) AS enrolled_count
FROM Section s
JOIN Course c ON s.course_id = c.course_id
LEFT JOIN Enrollment e ON e.section_id = s.section_id
WHERE c.department = '컴퓨터학부'
GROUP BY 
    s.section_id,
    c.course_name,
    s.capacity
ORDER BY 
    c.course_name,
    s.section_id;


7. 사용 시나리오 예시
================================================================================

[시나리오 1: 일반 수강신청]
1) 로그인 → id: 20000001 pw: pw20000001
2) 강의 조회 → 학과별 강의 조회 (예: 컴퓨터공학과)
3) 분반 조회 → 강의 코드로 분반 조회 (예: CS301)
4) 수강꾸러미 → 분반 담기 (예: CS301001)
5) 정원 내면 즉시 수강신청 완료
6) 등록조회 → 나의 등록 조회로 확인

[시나리오 2: 경매 입찰]
1) 로그인
2) 경매 참여 → 학과별 경매 조회
3) 경매 참여 → 입찰하기
4) 경매 ID 입력 → 입찰 포인트 입력
5) 낙찰 여부 확인
6) 낙찰 시 자동 수강신청 완료

[시나리오 3: 수강신청 취소]
1) 로그인
2) 등록조회 → 나의 등록 조회
3) 등록조회 → 등록 취소
4) 취소할 분반 ID 입력