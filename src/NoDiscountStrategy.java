// 파일 이름: NoDiscountStrategy.java

/**
 * 할인을 적용하지 않는 기본 할인 정책을 구현한 클래스입니다.
 * DiscountStrategy 인터페이스의 구체적인 구현(Concrete Strategy) 중 하나로,
 * 할인 조건에 해당하지 않는 사용자(예: 교직원, 일반인)에게 적용됩니다.
 */
public class NoDiscountStrategy implements DiscountStrategy {

    /**
     * 할인을 적용하지 않으므로, 원본 요금을 그대로 반환합니다.
     * @param originalFee 할인이 적용되기 전의 원본 요금
     * @return 원본 요금과 동일한 값
     */
    @Override
    public double applyDiscount(double originalFee) {
        return originalFee;
    }

    /**
     * 현재 적용된 할인 정책의 이름을 반환합니다.
     * @return "일반 (할인 없음)" 문자열
     */
    @Override
    public String getStrategyName() {
        return "일반 (할인 없음)";
    }
}