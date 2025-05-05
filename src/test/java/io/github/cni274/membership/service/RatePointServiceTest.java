package io.github.cni274.membership.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RatePointServiceTest {

    @InjectMocks
    RatePointService ratePointService;

    @ParameterizedTest
    @MethodSource("calculateAmountParameter")
    @DisplayName("가격의 1%를 적립한다")
    void successfulCalculateAmount(int price, int amount) {
        int result = ratePointService.calculateAmount(price);

        assertThat(result).isEqualTo(amount);
    }

    private static Stream<Arguments> calculateAmountParameter() {
        return Stream.of(
                Arguments.of(10000, 100),
                Arguments.of(20000, 200),
                Arguments.of(30000, 300)
        );
    }
}
