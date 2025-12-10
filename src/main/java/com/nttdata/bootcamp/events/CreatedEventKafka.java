package com.nttdata.bootcamp.events;

import com.nttdata.bootcamp.entity.dto.CustomerKafkaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatedEventKafka extends EventKafka<CustomerKafkaDto> {

}
