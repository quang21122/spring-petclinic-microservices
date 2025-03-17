package org.springframework.samples.petclinic.customers.web;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PetRepository petRepository;

    @MockBean
    OwnerRepository ownerRepository;
    
    @Autowired
    ObjectMapper objectMapper;
    
    private Owner owner;
    private Pet pet;

    @BeforeEach
    void setup() {
        owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        
        pet = new Pet();
        pet.setName("Buddy");
        pet.setId(2);
        
        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);
        pet.setBirthDate(Date.valueOf(LocalDate.now().minusYears(1)));
        
        owner.addPet(pet);
    }

    @Test
    void shouldGetAPetInJsonFormat() throws Exception {
        given(petRepository.findById(2)).willReturn(Optional.of(pet));

        mvc.perform(get("/owners/1/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Buddy"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldReturnNotFoundForNonExistingPet() throws Exception {
        given(petRepository.findById(99)).willReturn(Optional.empty());

        mvc.perform(get("/owners/1/pets/99")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldCreateNewPet() throws Exception {
        PetType petType = new PetType();
        Pet newPet = new Pet();
        petType.setId(6);
        petType.setName("dog");
        
        newPet.setName("Leo");
        newPet.setId(3);
        newPet.setType(petType);
        newPet.setBirthDate(Date.valueOf(LocalDate.now().minusMonths(3)));
        
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));
        given(petRepository.save(any(Pet.class))).willReturn(newPet);
        
        mvc.perform(post("/owners/1/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Leo\",\"typeId\":6,\"birthDate\":\"" + LocalDate.now().minusMonths(3) + "\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
            
        verify(petRepository).save(any(Pet.class));
    }
    
    @Test
    void shouldUpdateExistingPet() throws Exception {
        given(petRepository.findById(2)).willReturn(Optional.of(pet));
        given(petRepository.save(any(Pet.class))).willReturn(pet);
        
        mvc.perform(put("/owners/1/pets/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Updated Buddy\",\"typeId\":6,\"birthDate\":\"" + LocalDate.now().minusYears(1) + "\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
            
        verify(petRepository).save(any(Pet.class));
    }
    
    @Test
    void shouldReturnPetTypes() throws Exception {
        PetType dogType = new PetType();
        dogType.setId(6);
        dogType.setName("dog");
        
        given(petRepository.findPetTypes()).willReturn(Collections.singletonList(dogType));
        
        mvc.perform(get("/petTypes")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(6))
            .andExpect(jsonPath("$[0].name").value("dog"));
    }
}