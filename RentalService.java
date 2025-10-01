import java.util.List;
import java.util.Optional;

// =================================================================================
// 2. 비즈니스 로직 - RentalRecord를 사용하도록 수정
// =================================================================================
class RentalService {
    // 이제 서비스가 전체 대여 기록을 관리
    private final List<RentalRecord> rentalHistory;

    public RentalService(List<RentalRecord> rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    public String rentItem(User user, Item item, DiscountStrategy strategy) {
        if (item.rentTo(user)) {
            // 대여 성공 시, 새로운 대여 기록 생성 및 추가
            rentalHistory.add(new RentalRecord(user, item.getName()));

            double finalFee = strategy.applyDiscount(item.getBaseFee());
            return String.format("[대여 성공] %s -> %s", user.getName(), item.getName());
        } else {
            return "[대여 실패] " + item.getName() + " 재고가 없습니다.";
        }
    }

    public String returnItem(User user, Item item) {
        // 현재 사용자가 빌린 해당 물품의 미반납 기록 찾기
        Optional<RentalRecord> activeRecord = rentalHistory.stream()
                .filter(r -> r.getUser().equals(user) && r.getItemName().equals(item.getName()) && r.getReturnTime() == null)
                .findFirst();

        if (activeRecord.isPresent()) {
            if (item.returnBy(user)) {
                // 반납 성공 시, 찾은 기록에 반납 시간 기록
                activeRecord.get().markAsReturned();
                return String.format("[반납 성공] %s <- %s", user.getName(), item.getName());
            }
        }
        return String.format("[반납 실패] %s님은 %s을(를) 대여하지 않았습니다.", user.getName(), item.getName());
    }
}
