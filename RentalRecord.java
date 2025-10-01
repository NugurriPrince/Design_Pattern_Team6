import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * [새로운 클래스] 대여 기록 한 건을 저장하는 모델
 */
class RentalRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final String itemName;
    private final LocalDateTime rentalTime;
    private LocalDateTime returnTime; // 반납 시 기록됨

    public RentalRecord(User user, String itemName) {
        this.user = user;
        this.itemName = itemName;
        this.rentalTime = LocalDateTime.now();
        this.returnTime = null; // 처음엔 null
    }

    public void markAsReturned() {
        this.returnTime = LocalDateTime.now();
    }

    // Getter 메서드들 (테이블에 표시할 때 사용)
    public User getUser() {
        return user;
    }

    public String getItemName() {
        return itemName;
    }

    public LocalDateTime getRentalTime() {
        return rentalTime;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }
}
