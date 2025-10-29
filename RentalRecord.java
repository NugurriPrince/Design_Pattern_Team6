// 파일 이름: RentalRecord.java

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 한 건의 대여 기록을 나타내는 데이터 클래스(Data Class)입니다.
 * 이 객체는 어떤 사용자가 어떤 물품을 언제 빌리고 반납했는지에 대한 정보를 담고 있습니다.
 * Serializable 인터페이스를 구현하여 파일에 객체 상태를 저장하고 불러올 수 있습니다.
 */
public class RentalRecord implements Serializable {

    // 직렬화/역직렬화 시 클래스 버전 관리를 위한 고유 ID
    private static final long serialVersionUID = 1L;

    // --- 필드(Fields) ---
    private final User user;                // 대여한 사용자
    private final String itemName;          // 대여한 물품의 이름
    private final LocalDateTime rentalTime; // 대여가 발생한 시간
    private LocalDateTime returnTime;       // 반납이 발생한 시간 (반납 전까지는 null)

    /**
     * 새로운 대여 기록을 생성하는 생성자입니다.
     * 객체가 생성되는 시점을 대여 시간으로 기록합니다.
     * @param user 대여하는 사용자 객체
     * @param itemName 대여하는 물품의 이름
     */
    public RentalRecord(User user, String itemName) {
        this.user = user;
        this.itemName = itemName;
        this.rentalTime = LocalDateTime.now(); // 현재 시간을 대여 시간으로 설정
        this.returnTime = null;                // 처음에는 반납 시간이 없으므로 null로 초기화
    }

    /**
     * 이 대여 기록을 '반납 완료' 상태로 변경합니다.
     * 메소드가 호출되는 시점을 반납 시간으로 기록합니다.
     */
    public void markAsReturned() {
        this.returnTime = LocalDateTime.now();
    }

    // --- Getter 메소드 ---

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