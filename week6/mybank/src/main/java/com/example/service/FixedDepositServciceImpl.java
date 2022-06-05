package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.BankAccountDao;
import com.example.dao.FixedDepositDao;
import com.example.domain.FixedDepositDetails;

@Service(value = "fixedDepositService")
public class FixedDepositServciceImpl implements FixedDepositService {

    @Autowired
    @Qualifier(value = "fixedDepositDao")
    private FixedDepositDao myFixedDepositDao;

    @Autowired
    private BankAccountDao bankAccountDao;

    @Override
    @Transactional
    public int createFixedDeposit(FixedDepositDetails fdd) throws Exception {
        bankAccountDao.subtractFromAccount(fdd.getBankAccountId(), fdd.getFdAmount());
        return myFixedDepositDao.createFixedDeposit(fdd);
    }

    @Override
    public FixedDepositDetails getFixedDeposit(int fixedDepositId) {
        return myFixedDepositDao.getFixedDeposit(fixedDepositId);
    }

}
