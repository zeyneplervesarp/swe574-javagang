package com.swe573.socialhub.controller;


import com.swe573.socialhub.assembler.TagModelAssembler;
import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.dto.TagDto;
import com.swe573.socialhub.exception.TagNotFoundException;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.service.TagService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public
class TagController {

    @Autowired
    private TagService tagService;

    private final TagRepository repository;
    private final TagModelAssembler assembler;

    TagController(TagRepository repository, TagModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/tags/{id}")
    public EntityModel<Tag> one(@PathVariable Long id) {

        Tag tag = repository.findById(id) //
                .orElseThrow(() -> new TagNotFoundException(id));

        return assembler.toModel(tag);
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/tags")
    public List<TagDto> all() {

        List<TagDto> tags = tagService.findAllTags();

        //        List<EntityModel<Tag>> tags = repository.findAll().stream() //
//                .map(assembler::toModel) //
//                .collect(Collectors.toList());

//        return CollectionModel.of(tags, linkTo(methodOn(TagController.class).all()).withSelfRel());
        return tags;

    }
    // end::get-aggregate-root[]

    @PostMapping("/tags")
    ResponseEntity<?> newTag(@RequestBody Tag newTag) {

        EntityModel<Tag> entityModel = assembler.toModel(repository.save(newTag));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @DeleteMapping("/tags/{id}")
    ResponseEntity<?> deleteTag(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tags/{id}")
    ResponseEntity<?> replaceTag(@RequestBody Tag newTag, @PathVariable Long id) {

        Tag updatedTag = repository.findById(id) //
                .map(tag -> {
                    tag.setName(newTag.getName());
                    return repository.save(tag);
                }) //
                .orElseGet(() -> {
                    newTag.setId(id);
                    return repository.save(newTag);
                });

        EntityModel<Tag> entityModel = assembler.toModel(updatedTag);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @GetMapping("/tags/info/{name}")
    public String getInfoFromApi(@PathVariable String name) {

        final String uri = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" + name;




        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject jsonObject =(JSONObject) JSONValue.parse(result);
        var i = 0;
        var returnString = "";

    var foo = result.substring(result.indexOf("extract") + 8 , result.length());
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(foo);
        while (m.find()) {

            System.out.println(m.group(1));
            if (i == 0)
            {
                returnString = m.group(1);
            }
            i++;
        }
        System.out.println(result);


        return  returnString;
    }
}