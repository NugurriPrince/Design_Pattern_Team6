// 파일 이름: Item.java

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 대여 가능한 물품의 정보를 담는 데이터 모델 클래스
 * Serializable 인터페이스를 구현하여 객체 상태를 파일에 저장하고 불러올 수 있습니다. (직렬화)
 * 또한, 옵저버 패턴(Observer Pattern)을 사용하여 물품의 상태(예: 재고)가 변경될 때마다
 * 등록된 옵저버(주로 UI 컴포넌트)에게 변경 사실을 알려 UI를 자동으로 업데이트합니다.
 */

public class Item implements Serializable {
    
    // 직렬화/역직렬화 시 클래스 버전 관리를 위한 고유 ID
    private static final long serialVersionUID = 1L;

    // --- 필드(Fields) ---
    private final String name;          // 물품의 이름 (변경 불가)
    private final int maxStock;         // 최대 재고량 (변경 불가)
    private final double baseFee;       // 기본 대여 요금
    private List<User> renters;         // 현재 이 물품을 대여 중인 사용자 목록

    /**
     * 옵저버(Observer) 목록. 'transient' 키워드는 이 필드가 직렬화(파일 저장) 과정에서
     * 제외됨을 의미합니다. UI 컴포넌트와 같은 객체는 저장할 필요가 없기 때문입니다.
     */
    private transient List<Observer> observers = new ArrayList<>();

    /**
     * Item 객체 생성자
     * @param name 물품 이름
     * @param initialStock 초기 재고 (최대 재고량으로 설정됨)
     * @param baseFee 기본 대여 요금
     */
    public Item(String name, int initialStock, double baseFee) {
        this.name = name;
        this.maxStock = initialStock;
        this.baseFee = baseFee;
        this.renters = new ArrayList<>();
    }
    
    // --- Getter 메소드 ---
    public String getName() { return name; }
    public int getMaxStock() { return maxStock; }
    public double getBaseFee() { return baseFee; }

    /**
     * 현재 대여 가능한 재고량을 계산하여 반환합니다.
     * (최대 재고 - 현재 대여자 수)
     * @return 현재 재고량
     */
    public int getCurrentStock() {
        return maxStock - renters.size();
    }
    
    // --- 핵심 로직 메소드 (대여/반납) ---
    
    /**
     * 특정 사용자에게 물품을 대여합니다.
     * @param user 대여할 사용자
     * @return 대여 성공 시 true, 재고가 없어 실패 시 false
     */
    public boolean rentTo(User user) {
        if (getCurrentStock() > 0) {
            renters.add(user);
            notifyObservers(); // 상태 변경을 옵저버에게 알림
            return true;
        }
        return false;
    }
    
    /**
     * 사용자가 대여했던 물품을 반납합니다.
     * @param user 반납할 사용자
     * @return 반납 성공 시 true, 해당 사용자가 대여 중이 아닐 경우 false
     */
    public boolean returnBy(User user) {
        if (renters.contains(user)) {
            renters.remove(user);
            notifyObservers(); // 상태 변경을 옵저버에게 알림
            return true;
        }
        return false;
    }

    // --- 옵저버 패턴 관련 메소드 ---

    /**
     * 이 물품의 상태 변화를 감지할 옵저버를 추가합니다.
     * @param observer 추가할 옵저버 객체
     */
    public void addObserver(Observer observer) {
        getObservers().add(observer);
    }
    
    /**
     * 등록된 모든 옵저버에게 상태가 변경되었음을 알립니다.
     * 각 옵저버의 update() 메소드가 호출됩니다.
     */
    public void notifyObservers() {
        // null 체크 후 각 옵저버에게 업데이트 알림
        getObservers().forEach(observer -> observer.update(this));
    }

    // --- 유틸리티 및 재정의 메소드 ---

    /**
     * JComboBox나 리스트 등에서 객체를 문자열로 표현할 때 물품의 이름이 표시되도록 합니다.
     * @return 물품의 이름
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * (방어적 프로그래밍) observers 리스트가 null일 경우 새로 생성하여 NullPointerException을 방지합니다.
     */
    private List<Observer> getObservers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    /**
     * 역직렬화(파일에서 객체를 읽어올 때) 시 호출되는 특별 메소드입니다.
     * 'transient'로 선언되어 저장되지 않았던 observers 리스트를 다시 초기화해줍니다.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 기본 역직렬화 수행
        this.observers = new ArrayList<>(); // observers 리스트 초기화

        // 데이터 무결성을 위한 방어 코드
        if (this.renters == null) {
            this.renters = new ArrayList<>();
        }
    }
}