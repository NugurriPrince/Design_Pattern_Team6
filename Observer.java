// 파일 이름: Observer.java

/**
 * 옵저버 디자인 패턴(Observer Design Pattern)을 구현하기 위한 인터페이스입니다.
 * * 이 인터페이스를 구현하는 클래스(Observer, 관찰자)는 특정 객체(Subject, 관찰 대상)의
 * 상태 변화를 통보받을 수 있는 자격을 갖게 됩니다.
 * * 이 시스템에서는 'MainAppFrame' 클래스가 이 인터페이스를 구현하여,
 * 'Item' 객체(Subject)의 재고 변화를 실시간으로 감지하고 UI를 업데이트합니다.
 */
public interface Observer {
    
    /**
     * 관찰 대상(Subject)의 상태가 변경되었을 때 호출되는 메소드입니다.
     * Subject(이 시스템에서는 Item)는 자신의 상태가 바뀔 때, 등록된 모든 Observer의
     * 이 update 메소드를 호출하여 변경 사실을 알립니다.
     *
     * @param item 상태가 변경된 Item 객체. 옵저버는 이 객체를 통해 최신 정보를 얻을 수 있습니다.
     */
    void update(Item item);
}