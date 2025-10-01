// 기능 : 대여 기록을 담는 클래스

import java.io.Serializable;
import java.time.LocalDateTime;

public class RentalRecord implements Serializable {
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

    public User getUser() { return user; }
    public String getItemName() { return itemName; }
    public LocalDateTime getRentalTime() { return rentalTime; }
    public LocalDateTime getReturnTime() { return returnTime; }
}