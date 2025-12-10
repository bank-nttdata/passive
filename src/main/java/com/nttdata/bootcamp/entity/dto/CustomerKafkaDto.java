package com.nttdata.bootcamp.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CustomerKafkaDto {

    @Id
    private String id;

    private String dni;
    private String typeCustomer;
    private Boolean flagVip;
    private Boolean flagPyme;

}
