-- Type 1
SELECT student_id, name, department
FROM STUDENT
WHERE name LIKE '최%';

SELECT course_id, course_name
FROM COURSE
WHERE department = '물리학과';

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



-- Type 2
-- ‘일반물리’ 강의를 장바구니에 담은 학생의 ID와 이름을 구하기
SELECT DISTINCT s.student_id, s.name
FROM STUDENT s, BASKET b, BASKETITEM bi, SECTION sec, COURSE c
WHERE s.student_id = b.student_id
  AND b.basket_id   = bi.basket_id
  AND bi.section_id = sec.section_id
  AND sec.course_id = c.course_id
  AND c.course_name = '일반물리';

-- '박현우' 학생이 수강신청한 모든 강의의 이름과 교수명을 구하기
SELECT c.course_name, sec.professor
FROM STUDENT s, ENROLLMENT e, SECTION sec, COURSE c
WHERE s.student_id = e.student_id
  AND e.section_id = sec.section_id
  AND sec.course_id = c.course_id
  AND s.name = '박현우';

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


-- Type 3
-- 각 학과(department)별 개설된 강의의 총 학점(credits) 합계를 구하기
SELECT
  c.department,
  SUM(c.credits) AS total_credits
FROM COURSE c
GROUP BY c.department;

-- 각 교수(professor)별로 강의하는 학생 수를 구하기
SELECT
  sec.professor,
  COUNT(*) AS enroll_count
FROM ENROLLMENT e
JOIN SECTION sec ON sec.section_id = e.section_id
GROUP BY sec.professor;


-- Type 4
-- 전자공학부 강의를 실제 '수강신청(Enrollment)'한 학생의 id, name
SELECT DISTINCT s.student_id, s.name
FROM Student s
WHERE s.student_id IN (
    SELECT e.student_id
    FROM Enrollment e
    JOIN Section sec ON e.section_id = sec.section_id
    JOIN Course  c   ON sec.course_id = c.course_id
    WHERE c.department = '전자공학부'
);

-- '음악학과' 학생이 가진 장바구니 id, 학생 id
SELECT basket_id, student_id
FROM Basket
WHERE student_id IN (
    SELECT student_id
    FROM Student
    WHERE department = '음악학과'
);


-- Type 5
-- 경매(Auction) 를 통해서 등록(Enrollment) 한 분반이 있는 학생의 id, 이름
SELECT DISTINCT s.student_id, s.name
FROM Student s
WHERE EXISTS (
    SELECT 1
    FROM Enrollment e
    JOIN Auction a ON a.section_id = e.section_id
    WHERE e.student_id = s.student_id
      AND e.enrollment_source = 'FROM_AUCTION'
);

-- '컴퓨터네트워크' 강의의 경매에 입찰한 학생의 id, 이름
SELECT DISTINCT s.student_id, s.name
FROM Student s
WHERE EXISTS (
    SELECT 1
    FROM Bid b
    JOIN Auction a ON b.auction_id = a.auction_id
    JOIN Section sec ON a.section_id = sec.section_id
    JOIN Course c ON sec.course_id = c.course_id
    WHERE b.student_id = s.student_id
      AND c.course_name = '컴퓨터네트워크'
);


-- Type 6
-- 'BIO104002' 분반을 등록한 학생의 id, 이름
SELECT s.student_id, s.name
FROM Student s
WHERE s.student_id IN (
    SELECT e.student_id
    FROM Enrollment e
    WHERE e.section_id = 'BIO104002'
);

-- 강의실 'EDU-297'에서 열리는 모든 강의 id와 분반 id
SELECT section_id, course_id
FROM Section
WHERE course_id IN (
    SELECT course_id
    FROM Course
    WHERE classroom = 'EDU-297'
);


-- Type 7
-- 경영학과에서 개설된 모든 강의의 분반 ID와 강의명을 조회하라 
SELECT sub.section_id, c.course_name
FROM (
    SELECT section_id, course_id
    FROM Section
    WHERE course_id IN (
        SELECT course_id
        FROM Course
        WHERE department = '경영학과'
    )
) sub
JOIN Course c ON sub.course_id = c.course_id;

-- 2025년 7월 31일에 수강신청(enrollment)을 한 학생들의 id와 이름을 조회하라.
SELECT s.student_id, s.name
FROM (
    SELECT DISTINCT student_id
    FROM Enrollment
    WHERE TRUNC(enrollment_time) = TO_DATE('2025-07-31', 'YYYY-MM-DD')
) sub
JOIN Student s ON sub.student_id = s.student_id;



-- Type 8
-- 로그인한 학생들의 ID, 이름, 로그인 시각(분 단위)을 로그인 시각 순서대로 정렬하여 출력
SELECT L.student_id, S.name, TO_CHAR(L.timestamp, 'YYYY-MM-DD HH24:MI') AS login_time
FROM Log L
JOIN Student S ON L.student_id = S.student_id
WHERE L.action_type = 'LOGIN'
ORDER BY L.timestamp ASC;

-- 요일이 금요일(FRI) 인 수업의 분반 ID, 강의 이름, 그리고 수업 종료 시간 을 출력하되,가장 늦게 끝나는 수업부터 순서대로 정렬하라.
SELECT T.section_id, C.course_name, end_time
FROM TimeSlot T
JOIN Section S ON T.section_id = S.section_id
JOIN Course C ON S.course_id = C.course_id
WHERE T.day = 'FRI'
ORDER BY T.end_time DESC;

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


-- Type 9

-- 월요일에 열리는 각 분반의 강의이름과 분반 id와 수강신청 인원 수를 수강신청 인원수의 내림차순 정렬하라.
SELECT
    C.course_name,
    S.section_id,
    COUNT(E.enrollment_id) AS enrolled_cnt
FROM Section   S
JOIN Course    C ON S.course_id  = C.course_id
JOIN TimeSlot  T ON T.section_id = S.section_id
LEFT JOIN Enrollment E ON E.section_id = S.section_id
WHERE T.day = 'MON'
GROUP BY C.course_name, S.section_id
ORDER BY enrolled_cnt DESC, C.course_name;

-- 모든 학생의 입찰한 포인트의 합계와 학생 id와 학생이름을 입찰한 포인트의 내림차순으로 정렬하라
SELECT 
    S.student_id,
    S.name,
    SUM(B.bid_amount) AS total_bid_points
FROM Bid B
JOIN Student S ON B.student_id = S.student_id
GROUP BY S.student_id, S.name
ORDER BY total_bid_points DESC;

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



-- Type 10
-- ‘확률과 통계’ 강의를 수강꾸러미 또는 수강신청한 학생 id 와 이름
SELECT S.student_id, S.name
FROM Student S
JOIN Basket B ON S.student_id = B.student_id
JOIN BasketItem BI ON B.basket_id = BI.basket_id
JOIN Section SC ON BI.section_id = SC.section_id
JOIN Course C ON SC.course_id = C.course_id
WHERE C.course_name = '확률과통계'
UNION
SELECT S.student_id, S.name
FROM Student S
JOIN Enrollment E ON S.student_id = E.student_id
JOIN Section SC ON E.section_id = SC.section_id
JOIN Course C ON SC.course_id = C.course_id
WHERE C.course_name = '확률과통계'
ORDER BY student_id;


-- ‘확률과 통계’ 과목을 수강꾸러미에는 담았지만 수강신청은 하지 않은 학생 id 와 이름
SELECT S.student_id, S.name
FROM Student S
JOIN Basket B ON S.student_id = B.student_id
JOIN BasketItem BI ON B.basket_id = BI.basket_id
JOIN Section SC ON BI.section_id = SC.section_id
JOIN Course C ON SC.course_id = C.course_id
WHERE C.course_name = '확률과통계'
MINUS
SELECT S.student_id, S.name
FROM Student S
JOIN Enrollment E ON S.student_id = E.student_id
JOIN Section SC ON E.section_id = SC.section_id
JOIN Course C ON SC.course_id = C.course_id
WHERE C.course_name = '확률과통계'
ORDER BY student_id;




