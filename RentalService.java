// 기능 : 대여 및 반납 서비스 제공

import java.util.List;
import java.util.Optional;

public class RentalService {
    private final List<RentalRecord> rentalHistory;

    public RentalService(List<RentalRecord> rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    public String rentItem(User user, Item item, DiscountStrategy strategy) {
        if (item.rentTo(user)) {
            rentalHistory.add(new RentalRecord(user, item.getName()));
            double finalFee = strategy.applyDiscount(item.getBaseFee());
            // 요금 계산 로직은 현재 결과 문자열에 포함되지 않으므로 참고용으로 남겨둡니다.
            return String.format("[대여 성공] %s -> %s", user.getName(), item.getName());
        } else {
            return "[대여 실패] " + item.getName() + " 재고가 없습니다.";
        }
    }

    public String returnItem(User user, Item item) {
        Optional<RentalRecord> activeRecord = rentalHistory.stream()
                .filter(r -> r.getUser().equals(user) && r.getItemName().equals(item.getName()) && r.getReturnTime() == null)
                .findFirst();

        if (activeRecord.isPresent()) {
            if (item.returnBy(user)) {
                activeRecord.get().markAsReturned();
                return String.format("[반납 성공] %s <- %s", user.getName(), item.getName());
            }
        }
        return String.format("[반납 실패] %s님은 %s을(를) 대여하지 않았습니다.", user.getName(), item.getName());
    }
}