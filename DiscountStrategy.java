// 파일 이름: DiscountStrategy.java

/**
 * 대여 요금에 대한 할인 정책을 정의하는 인터페이스입니다. 
 * 이 인터페이스는 '전략 패턴(Strategy Pattern)'을 구현하는 데 사용됩니다.
 * * 다양한 할인 방식(예: 학생 할인, 교직원 할인 등)을 각각의 '전략'으로 구현할 수 있으며,
 * 각 전략 클래스는 이 인터페이스를 반드시 구현해야 합니다.
 * 이를 통해 할인 정책을 동적으로 변경하거나 새로운 정책을 쉽게 추가할 수 있습니다.
 */
public interface DiscountStrategy {

    /**
     * 주어진 원본 요금에 할인율을 적용하여 최종 요금을 계산합니다.
     * * @param originalFee 할인이 적용되기 전의 원본 요금
     * @return 할인 정책이 적용된 후의 최종 요금
     */
    double applyDiscount(double originalFee);
    
    /**
     * 현재 적용된 할인 정책의 이름을 반환합니다.
     * (예: "학생 할인", "기본 요금")
     * * @return 할인 정책의 이름 (String)
     */
    String getStrategyName();
}