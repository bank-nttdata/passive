package com.nttdata.bootcamp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "passive")
public class Passive {
    @Id
    private String id;
    private String dni;
    private String typeCustomer;
    private Boolean flagVip;
    private Boolean flagPyme;
    private String accountNumber;
    private Boolean saving;
    private Boolean currentAccount;
    private Boolean fixedTerm;
    private Boolean freeCommission; //: libre de comisión
    private Number commissionMaintenance; // posee comisión de mantenimiento
    private Boolean movementsMonthly;
    private Number limitMovementsMonthly; //límite máximo de movimientos mensuales
    private Number commissionTransaction;
    private Boolean dailyAverage;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private Date creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @LastModifiedDate
    private Date modificationDate;
}
