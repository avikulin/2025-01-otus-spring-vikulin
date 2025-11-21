package ru.otus.spring.hw15.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.spring.hw15.model.Payment;
import ru.otus.spring.hw15.msg.PaymentMsg;

@Mapper
public interface PaymentMapper {

    @Mapping(target = "accountId", source = "taxAccount.accountId")
    PaymentMsg toMsg(Payment payment);

    @Mapping(target = "taxAccount.accountId", source = "accountId")
    Payment toEntity(PaymentMsg paymentMsg);
}
