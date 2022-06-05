package com.example;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.domain.BankAccountDetails;
import com.example.domain.FixedDepositDetails;
import com.example.service.BankAccountService;
import com.example.service.FixedDepositService;

public class BankApp {
    private static Logger logger = LogManager.getLogger(BankApp.class);

    public static void main(String args[]) throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:META-INF/spring/applicationContext.xml");

        BankAccountService bankAccountService = context.getBean(BankAccountService.class);
        BankAccountDetails bankAccountDetails = new BankAccountDetails();
        bankAccountDetails.setBalanceAmount(1000);
        bankAccountDetails.setLastTransactionTimestamp(new Date());

        int bankAccountId = bankAccountService.createBankAccount(bankAccountDetails);

        logger.info("Created bank account with id - " + bankAccountId);

        FixedDepositService fixedDepositService = context.getBean(FixedDepositService.class);

        FixedDepositDetails fdd = new FixedDepositDetails();
        fdd.setActive("Y");
        fdd.setBankAccountId(bankAccountId);
        fdd.setFdCreationDate(new Date());
        fdd.setFdAmount(500);
        fdd.setTenure(12);
        int fixedDepositId = fixedDepositService.createFixedDeposit(fdd);
        logger.info("Created fixed deposit with id - " + fixedDepositId);

        logger.info(fixedDepositService.getFixedDeposit(fixedDepositId));
        context.close();
    }
}