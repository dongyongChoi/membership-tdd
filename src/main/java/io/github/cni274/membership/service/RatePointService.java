package io.github.cni274.membership.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatePointService implements PointService{

    private static final double POINT_RATE = 0.01;

    @Override
    public int calculateAmount(int price) {
        return (int) (price * POINT_RATE);
    }
}
