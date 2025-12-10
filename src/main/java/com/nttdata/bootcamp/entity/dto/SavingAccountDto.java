package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccountDto {
    private String dni;
    private String typeCustomer;
    private String accountNumber;
    private Number limitMovementsMonthly;
    private Number commissionTransaction;
}
