package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<Person> person(@PathVariable(value="lastName") String lastName,
                         @PathVariable(value="firstName") String firstName) {
        Optional<Person> person = personDataService.findPerson(lastName, firstName);
        if(person.isPresent()) {
           return ResponseEntity.of(person);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/person/{lastName}")
    public ResponseEntity<List<Person>> findAllPersonBySurName(@PathVariable(value="lastName") String lastName) {
        return new ResponseEntity<List<Person>>(personDataService.findAllByLastName(lastName), HttpStatus.OK);
    }
    @PostMapping("/person")
    public ResponseEntity createPerson(@Valid @RequestBody Person person) {
        Person newPerson = personDataService.createPerson(person);
        if(newPerson == null) {
            // We can use hibernate validator framework to check this instead.
            // That would be better clearer implementation.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}