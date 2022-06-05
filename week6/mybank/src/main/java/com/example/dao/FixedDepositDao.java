package com.example.dao;

import com.example.domain.FixedDepositDetails;

public interface FixedDepositDao {
    int createFixedDeposit(FixedDepositDetails fdd);

    FixedDepositDetails getFixedDeposit(int fixedDepositId);
}
