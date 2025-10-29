// 파일 이름: StudentDiscountStrategy.java

/**
 * 학생(Student)에게 적용되는 20% 할인 정책을 구현한 클래스입니다.
 * DiscountStrategy 인터페이스의 구체적인 구현(Concrete Strategy) 중 하나입니다.
 */
public class StudentDiscountStrategy implements DiscountStrategy {

    /**
     * 원본 요금에 20% 학생 할인을 적용합니다.
     * (원본 요금의 80%를 최종 요금으로 계산)
     * @param originalFee 할인이 적용되기 전의 원본 요금
     * @return 20% 할인이 적용된 최종 요금
     */
    @Override
    public double applyDiscount(double originalFee) {
        return originalFee * 0.8; // 20% 할인
    }

    /**
     * 현재 적용된 할인 정책의 이름을 반환합니다.
     * @return "학생 할인 (20%)" 문자열
     */
    @Override
    public String getStrategyName() {
        return "학생 할인 (20%)";
    }
}