// 파일 이름: Item.java
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final int maxStock;
    private List<User> renters;
    private final double baseFee; // 기본 대여료 (현재 로직에선 사용되지 않음)
    
    // --- 수정/추가된 필드 ---
    private final int deposit; // 보증금
    private final int returnDeadlineDays; // 반납 기한 (일)
    // --------------------

    private transient List<Observer> observers = new ArrayList<>();

    // --- 생성자 수정 ---
    public Item(String name, int initialStock, double baseFee, int deposit, int returnDeadlineDays) {
        this.name = name;
        this.maxStock = initialStock;
        this.baseFee = baseFee;
        this.deposit = deposit;
        this.returnDeadlineDays = returnDeadlineDays;
        this.renters = new ArrayList<>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException { in.defaultReadObject(); this.observers = new ArrayList<>(); if (this.renters == null) { this.renters = new ArrayList<>(); } }
    private List<Observer> getObservers() { if (observers == null) { observers = new ArrayList<>(); } return observers; }
    
    // --- 새 Getter 추가 ---
    public int getDeposit() { return deposit; }
    public int getReturnDeadlineDays() { return returnDeadlineDays; }
    // -------------------

    public String getName() { return name; }
    public int getMaxStock() { return maxStock; }
    public int getCurrentStock() { return maxStock - renters.size(); }
    public double getBaseFee() { return baseFee; }
    public boolean rentTo(User user) { if (getCurrentStock() > 0) { renters.add(user); notifyObservers(); return true; } return false; }
    public boolean returnBy(User user) { if (renters.contains(user)) { renters.remove(user); notifyObservers(); return true; } return false; }
    public void addObserver(Observer observer) { getObservers().add(observer); }
    public void notifyObservers() { getObservers().forEach(observer -> observer.update(this)); }
    @Override public String toString() { return name; }
}