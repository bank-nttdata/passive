package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccountDto {
    private String ruc;
    private String dni;
    private String accountNumber;
    private Number commissionMaintenance;
    private Number commissionTransaction;
    private Boolean firmante;
}
