//package com.swe573.socialhub.assembler;
//
//import com.swe573.socialhub.controller.OrderController;
//import com.swe573.socialhub.model.Order;
//import com.swe573.socialhub.model.Status;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.server.RepresentationModelAssembler;
//import org.springframework.stereotype.Component;
//
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
//
//@Component
//public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
//
//    @Override
//    public EntityModel<Order> toModel(Order order) {
//
//        // Unconditional links to single-item resource and aggregate root
//
//        EntityModel<Order> orderModel = EntityModel.of(order,
//                linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
//                linkTo(methodOn(OrderController.class).all()).withRel("orders"));
//
//        // Conditional links based on state of the order
//
//        if (order.getStatus() == Status.IN_PROGRESS) {
//            orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
//            orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
//        }
//
//        return orderModel;
//    }
//}