// 파일 이름: RentalRecord.java
import java.io.Serializable;
import java.time.LocalDateTime;

public class RentalRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final String itemName;
    private final LocalDateTime rentalTime;
    private LocalDateTime returnTime;

    // --- 수정/추가된 필드 ---
    private final LocalDateTime dueDate; // 반납 마감 기한
    private final double depositPaid; // 지불한 보증금
    private double refundAmount; // 최종 환불액
    private String status; // 현재 상태 (대여 중, 반납 (정시), 반납 (연체))
    // --------------------

    public RentalRecord(User user, Item item) {
        this.user = user;
        this.itemName = item.getName();
        this.rentalTime = LocalDateTime.now();
        this.depositPaid = item.getDeposit();
        this.dueDate = LocalDateTime.now().plusDays(item.getReturnDeadlineDays());
        this.status = "대여 중";
        this.returnTime = null;
        this.refundAmount = 0;
    }

    // --- 수정된 메소드 ---
    public void markAsReturned(double refundAmount) {
        this.returnTime = LocalDateTime.now();
        this.refundAmount = refundAmount;
        // status는 팩토리에서 미리 설정
    }
    
    // --- 새 Getter/Setter 추가 ---
    public LocalDateTime getDueDate() { return dueDate; }
    public double getDepositPaid() { return depositPaid; }
    public double getRefundAmount() { return refundAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    // -------------------------

    public User getUser() { return user; }
    public String getItemName() { return itemName; }
    public LocalDateTime getRentalTime() { return rentalTime; }
    public LocalDateTime getReturnTime() { return returnTime; }
}