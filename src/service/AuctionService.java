package service;

import dao.AuctionDAO;
import dao.BidDAO;
import dao.StudentDAO;
import model.Auction;
import model.Bid;
import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AuctionService {
    private AuctionDAO auctionDAO;
    private BidDAO bidDAO;
    private StudentDAO studentDAO;

    public AuctionService(AuctionDAO auctionDAO, BidDAO bidDAO, StudentDAO studentDAO) {
        this.auctionDAO = auctionDAO;
        this.bidDAO = bidDAO;
        this.studentDAO = studentDAO;
    }
    
    /**
     * 1. 학과별 경매 조회
     */
    public void queryAuctionByDepartment(String department) throws SQLException {
        List<Auction> auctions = auctionDAO.selectByDepartment(department);
        
        if (auctions.isEmpty()) {
            System.out.println("해당 학과의 경매가 없습니다.");
            return;
        }
        
        System.out.println("\n========== 경매 목록 (" + department + ") ==========");
        System.out.printf("%-12s %-25s %-10s %-8s%n",
            "경매ID", "강의명", "분반", "정원");
        System.out.println("=".repeat(70));
        
        for (Auction a : auctions) {
            System.out.printf("%-12s %-25s %-10d %-8d%n",
                a.getAuctionId(),
                a.getCourseName(),
                a.getSectionNumber(),
                a.getAvailableSlots()
            );
        }
        System.out.println("=".repeat(70));
    }
    
    /**
     * 2. 나의 경매 조회 (참여 가능한 모든 경매 + 내 입찰 정보)
     */
    public void queryMyAuctions(Student loggedInStudent) throws SQLException {
        Map<Auction, Bid> auctionBidMap = auctionDAO.selectMyAuctions(loggedInStudent.getStudentId());
        
        if (auctionBidMap.isEmpty()) {
            System.out.println("참여 가능한 경매가 없습니다.");
            return;
        }
        
        System.out.println("\n========== 참여 가능한 경매 목록 ==========");
        System.out.printf("%-12s %-25s %-8s %-12s %-12s%n",
            "경매ID", "강의명", "분반", "참여인원", "내입찰");
        System.out.println("=".repeat(80));
        
        for (Map.Entry<Auction, Bid> entry : auctionBidMap.entrySet()) {
            Auction a = entry.getKey();
            Bid b = entry.getValue();
            
            // 참여 인원수 조회
            int participantCount = bidDAO.countBidsByAuctionId(a.getAuctionId());
            
            String participantStr = participantCount + "/" + a.getAvailableSlots();
            String bidAmountStr = b.getBidAmount() == 0 ? "-" : String.valueOf(b.getBidAmount());
            // String successStr; 성공 여부는 의도적으로 가림
            
            System.out.printf("%-12s %-25s %-8d %-12s %-12s%n",
                a.getAuctionId(),
                a.getCourseName(),
                a.getSectionNumber(),
                participantStr,
                bidAmountStr
            );
        }
        System.out.println("=".repeat(80));
    }
    
    /**
     * 3. 입찰하기
     */
    public void placeBid(Student loggedInStudent, String auctionId, int bidAmount) throws SQLException {
        if (!auctionDAO.existsById(auctionId)) {
            System.out.println("존재하지 않는 경매입니다.");
            return;
        }
        
        Auction auction = auctionDAO.selectById(auctionId);
        
        // ✅ 이미 입찰했는지 확인 (재입찰 금지)
        if (bidDAO.hasAlreadyBid(auctionId, loggedInStudent.getStudentId())) {
            System.out.println("이미 입찰한 경매입니다. 재입찰은 불가능합니다.");
            System.out.println("다른 경매에 입찰하거나 신중하게 포인트를 배분하세요.");
            return;
        }
        
        System.out.println("\n========== 경매 정보 ==========");
        System.out.println("강의명: " + auction.getCourseName());
        System.out.println("학과: " + auction.getDepartment());
        System.out.println("분반: " + auction.getSectionNumber());
        System.out.println("교수: " + auction.getProfessor());
        System.out.println("경매 정원: " + auction.getAvailableSlots() + "명");
        
        // 현재 입찰 통계만 표시
        int participantCount = bidDAO.countBidsByAuctionId(auctionId);
        if (participantCount > 0) {
            System.out.println("\n========== 현재 입찰 현황 ==========");
            System.out.println("총 입찰자 수: " + participantCount + "명");
            System.out.println("경매 정원: " + auction.getAvailableSlots() + "명");
        }
        
        Student student = studentDAO.selectById(loggedInStudent.getStudentId());
        int totalBidPoints = bidDAO.getTotalBidPoints(student.getStudentId());
        int availablePoints = student.getMaxPoint() - totalBidPoints;
        
        System.out.println("\n========== 내 포인트 정보 ==========");
        System.out.println("보유 포인트: " + student.getMaxPoint());
        System.out.println("사용한 포인트: " + totalBidPoints);
        System.out.println("남은 포인트: " + availablePoints);

        if (bidAmount > availablePoints) {
            System.out.println("포인트가 부족합니다. (필요: " + bidAmount + ", 보유: " + availablePoints + ")");
            return;
        }
        
        if (bidAmount <= 0) {
            System.out.println("0보다 큰 포인트를 입력해주세요.");
            return;
        }
        
        // 신규 입찰만 가능
        String bidSequence = bidDAO.generateBidSequence();
        Bid bid = new Bid();
        bid.setBidSequence(bidSequence);
        bid.setBidAmount(bidAmount);
        bid.setAuctionId(auctionId);
        bid.setStudentId(student.getStudentId());
        bid.setIsSuccessful("N");
        
        boolean isWinner = bidDAO.insertAndCheckWinner(bid, auction.getAvailableSlots(), auction.getSectionId());
        
        System.out.println("\n========================================");
        System.out.println("입찰이 완료되었습니다!");
        System.out.println("입찰 ID: " + bidSequence);
        System.out.println("========================================");
        System.out.println("입찰 금액: " + bidAmount + " 포인트");
        System.out.println("========================================");
        
        if (isWinner) {
            System.out.println("축하합니다! 낙찰되었습니다!");
            System.out.println("수강신청이 자동으로 완료되었습니다.");
            System.out.println("사용된 포인트: " + bidAmount);
        } else {
            System.out.println("아쉽게도 낙찰되지 못했습니다.");
        }
        System.out.println("========================================");
    }
}
