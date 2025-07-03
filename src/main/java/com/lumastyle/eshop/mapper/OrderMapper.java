package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface
 * for converting between {@link OrderRequest}/{@link OrderResponse} DTOs and {@link OrderEntity} entity.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Converts an  {@link OrderRequest} DTO to an {@link OrderEntity} for persistence.
     * <p>
     * Maps request fields: {@code orderedItems}, {@code userAddress},
     * {@code phoneNumber}, {@code email} an {@code amount}.
     * Fields
     * ({@code id}, {@code userId}, {@code paymentStatus},
     * {@code gopayPaymentId}, {@code gopayTransactionId}, {@code orderStatus})
     * are ignored because they are managed by the business logic.
     *
     * @param request the {@link OrderRequest} DTO containing order placement data
     * @return a new {@link OrderEntity} populated from the request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "gopayPaymentId", ignore = true)
    @Mapping(target = "gopayTransactionId", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    OrderEntity toEntity(OrderRequest request);

    /**
     * Converts an {@link OrderEntity} into an {@link OrderResponse} DTO for client consumption.
     * <p>
     * Maps the internal GoPay payment ID to the external {@code paymentOrderId} field.
     * In addition, it remaps the GoPay payment ID to the {@code paymentOrderId} field.
     *
     * @param entity the {@link OrderEntity} retrieved from the database
     * @return an {@link OrderResponse} DTO containing order status and identifiers
     */
    @Mapping(source = "gopayPaymentId", target = "paymentOrderId")
    OrderResponse toResponse(OrderEntity entity);
}
