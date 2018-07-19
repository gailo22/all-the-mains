package com.gailo22;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Main71 {

    public static void main(String[] args) {

        CaseInfo caseInfo = CaseInfo.builder()
            .caseId("case-1")
            .productInfo(
                Arrays.asList(ProductInfo.builder()
                    .productId("prod-1")
                    .creditCards(
                        Arrays.asList(CreditCard.builder()
                            .creditCardId("creditcard-1").build()))
                    .build()))
            .build();

        CaseInfo caseInfo2 = CaseInfo.builder()
            .caseId("case-1")
            .productInfo(
                Arrays.asList(ProductInfo.builder()
                    .productId("prod-1")
                    .creditCards(null)
                    .build()))
            .build();

        CaseInfo caseInfo3 = CaseInfo.builder()
            .caseId("case-1")
            .productInfo(null)
            .build();

        caseInfo.getProductInfo()
            .stream()
            .findFirst()
            .flatMap(it -> Optional.of(it.getCreditCards()))
            .flatMap(it -> it.stream().findFirst());

        CreditCard creditCard = caseInfo.getProductInfo()
            .stream()
            .findFirst()
            .map(it -> it.getCreditCards())
            .flatMap(it -> it.stream().findFirst())
            .orElse(null);

        CreditCard creditCard3 = Optional.ofNullable(caseInfo.getProductInfo())
            .flatMap(it -> it.stream().findFirst())
            .map(it -> it.getCreditCards())
            .flatMap(it -> it.stream().findFirst())
            .orElse(null);

        CreditCard creditCard4 = resolve(() -> caseInfo2.getProductInfo().get(0)
            .getCreditCards().get(0))
            .orElse(null);

        System.out.println(creditCard4);

    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

}

@Data
@Builder
class CaseInfo {
    private String caseId;
    private List<ProductInfo> productInfo;
}

@Data
@Builder
class ProductInfo {
    private String productId;
    private List<CreditCard> creditCards;
}

@Data
@Builder
class CreditCard {
    private String creditCardId;
}
