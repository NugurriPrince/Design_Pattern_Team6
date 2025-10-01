// 기능 : 할인 전략 인터페이스

public interface DiscountStrategy {
    double applyDiscount(double originalFee);
    String getStrategyName();
}