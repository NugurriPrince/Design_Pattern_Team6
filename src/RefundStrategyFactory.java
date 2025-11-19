// 파일 이름: RefundStrategyFactory.java
import java.time.LocalDateTime;

public class RefundStrategyFactory {

    // 정책에 따른 환불 비율 정의
    private static final double LATE_PENALTY_RATE = 0.5; // 연체 시 50%만 환불
    private static final double DEFAULT_FEE_RATE = 0.9;  // 일반 사용자 10% 수수료

    /**
     * 사용자와 대여 기록을 바탕으로 올바른 환불 정책 객체를 생성합니다.
     */
    public static RefundStrategy getStrategy(User user, RentalRecord record) {
        
        boolean isLate = LocalDateTime.now().isAfter(record.getDueDate());

        // 1. 연체된 경우 (가장 강력한 조건)
        if (isLate) {
            record.setStatus("반납 (연체)");
            return new PartialRefundStrategy(LATE_PENALTY_RATE, "연체료 50% 차감");
        }

        // 2. 정시 반납인 경우
        if ("Student".equals(user.getType())) {
            record.setStatus("반납 (정시)");
            return new FullRefundStrategy(); // 학생은 전액 환불
        } else {
            record.setStatus("반납 (정시)");
            return new PartialRefundStrategy(DEFAULT_FEE_RATE, "수수료 10% 차감"); // 그 외는 수수료
        }
    }
}