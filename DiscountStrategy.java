interface DiscountStrategy {
    double applyDiscount(double originalFee);

    String getStrategyName();
}
