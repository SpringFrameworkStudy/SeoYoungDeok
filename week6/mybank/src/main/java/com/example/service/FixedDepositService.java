package com.example.service;

import com.example.domain.FixedDepositDetails;

public interface FixedDepositService {
    int createFixedDeposit(FixedDepositDetails fdd) throws Exception;

    FixedDepositDetails getFixedDeposit(int fixedDepositId);
}
