package uk.co.huntersix.spring.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.of(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldNotReturnPersonFromService_personNotFound() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/person/smith/mary"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreatePersonFromService() throws Exception {
        when(personDataService.createPerson(any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(
                post("/person")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(new Person("Mary", "Smith")))
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldNotCreatePersonFromService() throws Exception {
        when(personDataService.createPerson(any())).thenReturn(null);
        this.mockMvc.perform(
                post("/person")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(new Person("Mary", "Smith")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void findAllPersonBySurName_whenMoreThanOneRecord() throws Exception {
        List<Person> listOfPersons = new ArrayList<>();
        listOfPersons.add(new Person("Mary", "Smith"));
        listOfPersons.add(new Person("George", "Smith"));
        when(personDataService.findAllByLastName(any())).thenReturn(listOfPersons);
        this.mockMvc.perform(
                get("/person/Smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));;
    }

    @Test
    public void findAllPersonBySurName_whenOneRecord() throws Exception {
        List<Person> listOfPersons = new ArrayList<>();
        listOfPersons.add(new Person("Mary", "Smith"));
        when(personDataService.findAllByLastName(any())).thenReturn(listOfPersons);
        this.mockMvc.perform(
                get("/person/Smith"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void findAllPersonBySurName_whenEmptyRecords() throws Exception {
        List<Person> listOfPersons = new ArrayList<>();
        when(personDataService.findAllByLastName(any())).thenReturn(listOfPersons);
        this.mockMvc.perform(
                get("/person/Smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}