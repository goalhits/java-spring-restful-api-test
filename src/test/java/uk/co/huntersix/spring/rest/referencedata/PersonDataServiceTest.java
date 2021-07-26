package uk.co.huntersix.spring.rest.referencedata;


import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
public class PersonDataServiceTest {
    @InjectMocks
    private PersonDataService personDataService;

    @After
    public void cleanup() {
        personDataService.removePerson(new Person("John", "Smith"));
    }

    @Test
    public void shouldCreatePersonFirstTimeWhenNoDuplicate() {
        Person personObj = new Person("John", "Smith");
        Person newPerson = personDataService.createPerson(personObj);
        assertNotNull(newPerson);
        assertEquals(personObj.getFirstName(), newPerson.getFirstName());
        assertEquals(personObj.getLastName(), newPerson.getLastName());
    }

    @Test
    public void shouldNotCreatePersonSecondTimeWhenDuplicate() {
        Person personObj = new Person("John", "Smith");
        Person newPerson = personDataService.createPerson(personObj);
        newPerson = personDataService.createPerson(newPerson);
        assertNull(newPerson);
    }

    @Test
    public void shouldFindAllByLastName() {
        Person personObj = new Person("John", "Smith");
        personDataService.createPerson(personObj);
        List<Person> allPersonsByLastName = personDataService.findAllByLastName("Smith");
        assertEquals(allPersonsByLastName.size(), 2);
    }

    @Test
    public void shouldFindPerson() {
        Person personObj = new Person("John", "Smith");
        personDataService.createPerson(personObj);
        Optional<Person> person = personDataService.findPerson("Smith", "John");
        assertTrue(person.isPresent());
    }

    @Test
    public void shouldNotFindPerson() {
        Optional<Person> person = personDataService.findPerson("Smith", "John");
        assertFalse(person.isPresent());
    }
}
