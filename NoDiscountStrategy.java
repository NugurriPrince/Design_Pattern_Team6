// 기능 : 할인 전략 중 할인 없음 구현

public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalFee) {
        return originalFee;
    }
    @Override
    public String getStrategyName() {
        return "일반 (할인 없음)";
    }
}