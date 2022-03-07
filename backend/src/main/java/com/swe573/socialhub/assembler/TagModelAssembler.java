package com.swe573.socialhub.assembler;

import com.swe573.socialhub.controller.TagController;
import com.swe573.socialhub.domain.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>> {

    @Override
    public EntityModel<Tag> toModel(Tag tag) {

        return EntityModel.of(tag, //
                linkTo(methodOn(TagController.class).one(tag.getId())).withSelfRel(),
                linkTo(methodOn(TagController.class).all()).withRel("tags"));
    }
}