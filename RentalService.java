// 파일 이름: RentalService.java

import java.util.List;
import java.util.Optional;

/**
 * 물품의 대여 및 반납과 관련된 핵심 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 실제 데이터(Item, User)를 조작하고, 그 결과를 기록(RentalRecord)하며,
 * 처리 결과를 문자열 메시지로 반환하는 역할을 담당합니다.
 */
public class RentalService {

    // 애플리케이션의 전체 대여/반납 기록을 저장하는 리스트
    private final List<RentalRecord> rentalHistory;

    /**
     * RentalService 생성자
     * @param rentalHistory 대여 기록을 저장하고 공유하기 위한 List<RentalRecord> 객체
     */
    public RentalService(List<RentalRecord> rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    /**
     * 특정 사용자가 물품을 대여하는 로직을 처리합니다.
     * @param user 대여를 시도하는 사용자
     * @param item 대여할 물품
     * @param strategy 적용할 할인 정책 (현재 버전에서는 요금 계산이 없어 직접 사용되진 않음)
     * @return 처리 결과에 대한 성공 또는 실패 메시지 문자열
     */
    public String rentItem(User user, Item item, DiscountStrategy strategy) {
        // 1. Item 객체에 대여를 요청하고, 성공 여부를 반환받음 (재고 확인 포함)
        if (item.rentTo(user)) {
            // 2. 대여에 성공하면, 새로운 대여 기록(RentalRecord)을 생성하여 history에 추가
            rentalHistory.add(new RentalRecord(user, item.getName()));
            return String.format("[대여 성공] %s -> %s", user.getName(), item.getName());
        } else {
            // 3. 재고가 없어 대여에 실패하면, 실패 메시지를 반환
            return "[대여 실패] " + item.getName() + " 재고가 없습니다.";
        }
    }

    /**
     * 특정 사용자가 물품을 반납하는 로직을 처리합니다.
     * @param user 반납을 시도하는 사용자
     * @param item 반납할 물품
     * @return 처리 결과에 대한 성공 또는 실패 메시지 문자열
     */
    public String returnItem(User user, Item item) {
        // 1. rentalHistory에서 '해당 사용자'가 '해당 물품'을 빌리고 '아직 반납하지 않은' 기록을 찾음
        Optional<RentalRecord> activeRecord = rentalHistory.stream()
                .filter(r -> r.getUser().equals(user) &&
                             r.getItemName().equals(item.getName()) &&
                             r.getReturnTime() == null) // 반납 시간이 null인 기록 = 대여 중인 기록
                .findFirst();

        // 2. 대여 중인 기록이 존재하는 경우
        if (activeRecord.isPresent()) {
            // 3. Item 객체에 반납 처리를 요청
            if (item.returnBy(user)) {
                // 4. 반납에 성공하면, 찾아둔 대여 기록(activeRecord)에 반납 시간을 기록
                activeRecord.get().markAsReturned();
                return String.format("[반납 성공] %s <- %s", user.getName(), item.getName());
            }
        }
        
        // 5. 대여 기록이 없거나, 기타 이유로 반납에 실패한 경우 실패 메시지를 반환
        return String.format("[반납 실패] %s님은 %s을(를) 대여하지 않았습니다.", user.getName(), item.getName());
    }
}