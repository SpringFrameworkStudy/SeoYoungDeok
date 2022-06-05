package com.example.dao;

import com.example.domain.BankAccountDetails;

public interface BankAccountDao {
    int createBankAccount(BankAccountDetails bankAccountDetails);

    void subtractFromAccount(int bankAccountId, int amount);
}
