// 기능 : 학생 할인 전략 구현

public class StudentDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalFee) {
        return originalFee * 0.8;
    }
    @Override
    public String getStrategyName() {
        return "학생 할인 (20%)";
    }
}