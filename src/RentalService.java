// 파일 이름: RentalService.java
import java.util.List;
import java.util.Optional;

public class RentalService {
    private final List<RentalRecord> rentalHistory;
    public RentalService(List<RentalRecord> rentalHistory) { this.rentalHistory = rentalHistory; }

    /**
     * rentItem 수정: 대여 성공 시 String 대신 RentalRecord 객체를 반환합니다.
     * @return 대여 성공 시 RentalRecord, 실패 시 null
     */
    public RentalRecord rentItem(User user, Item item) { 
        if (item.rentTo(user)) {
            // 새 RentalRecord 생성 시 Item 객체 통째로 전달
            RentalRecord record = new RentalRecord(user, item);
            rentalHistory.add(record); 
            return record; // 성공 시 생성된 기록 객체를 반환
        } else {
            return null; // 재고 없음 등으로 실패 시 null 반환
        }
    }
    
    public String returnItem(User user, Item item) {
        Optional<RentalRecord> activeRecordOpt = rentalHistory.stream()
                .filter(r -> r.getUser().equals(user) && 
                             r.getItemName().equals(item.getName()) && 
                             r.getReturnTime() == null)
                .findFirst();

        if (activeRecordOpt.isEmpty()) {
            return String.format("[반납 실패] %s님은 %s을(를) 대여 중이지 않습니다.", user.getName(), item.getName());
        }

        RentalRecord record = activeRecordOpt.get();

        if (item.returnBy(user)) {
            // 환불 정책 공장을 통해 어떤 환불 전략을 쓸지 결정
            RefundStrategy strategy = RefundStrategyFactory.getStrategy(user, record);
            
            // 전략에 따라 환불액 계산
            double refund = strategy.calculateRefund(record.getDepositPaid());

            // 대여 기록에 반납 시간과 환불액, 상태를 최종 기록
            record.markAsReturned(refund);
            
            return String.format("[반납 성공] %s <- %s | %s | 환불액: %.0f원", 
                user.getName(), item.getName(), strategy.getPolicyName(), refund);
        } else {
            return "[반납 실패] 알 수 없는 오류입니다.";
        }
    }
}