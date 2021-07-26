package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    public static List<Person> PERSON_DATA;

    static {
        PERSON_DATA = new ArrayList<>();
        PERSON_DATA.add(new Person("Mary", "Smith"));
        PERSON_DATA.add(new Person("Brian", "Archer"));
        PERSON_DATA.add( new Person("Collin", "Brown"));
    }

    public Optional<Person> findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
            .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                && p.getLastName().equalsIgnoreCase(lastName))
            .findFirst();
    }

    public List<Person> findAllByLastName(String lastName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public Person createPerson(Person person) {
        boolean isAlreadyExist = findPerson(person.getLastName(), person.getFirstName())
                .isPresent();
        if(isAlreadyExist)
            return null;
        Person newPerson = new Person(person.getFirstName(), person.getLastName());
        PERSON_DATA.add(newPerson);
        return newPerson;
    }

    public void removePerson(Person personObj) {
        PERSON_DATA.remove(personObj);
    }
}
