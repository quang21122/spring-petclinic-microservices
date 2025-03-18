package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OwnerResourceUnitTest {

    private MockMvc mockMvc;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerResource ownerResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ownerResource).build();
    }

    @Test
    void shouldGetOwnerById() throws Exception {
        // Arrange
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

        // Act & Assert
        mockMvc.perform(get("/owners/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldReturnNotFoundForNonExistingOwner() throws Exception {
        // Arrange
        when(ownerRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/owners/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewOwner() throws Exception {
        // Arrange
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        // Act & Assert
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"city\": \"New York\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateExistingOwner() throws Exception {
        // Arrange
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        // Act & Assert
        mockMvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Johnny\", \"lastName\": \"Doe\", \"city\": \"Chicago\"}"))
                .andExpect(status().isNoContent());
        
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void shouldGetAllOwners() throws Exception {
        // Arrange
        List<Owner> owners = Arrays.asList(
            createOwner(1, "John", "Doe"),
            createOwner(2, "Jane", "Smith")
        );
        when(ownerRepository.findAll()).thenReturn(owners);

        // Act & Assert
        mockMvc.perform(get("/owners")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void shouldReturnBadRequestForInvalidOwnerData() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"\", \"lastName\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchOwnersByLastName() throws Exception {
        // Arrange
        List<Owner> owners = Arrays.asList(
            createOwner(1, "John", "Smith"),
            createOwner(2, "Jane", "Smith")
        );
        when(ownerRepository.findByLastName("Smith")).thenReturn(owners);

        // Act & Assert
        mockMvc.perform(get("/owners/search?lastName=Smith")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    void shouldReturnNotFoundForUpdateNonExistingOwner() throws Exception {
        // Arrange
        when(ownerRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/owners/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"city\": \"Chicago\"}"))
                .andExpect(status().isNotFound());
        
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoOwnersFound() throws Exception {
        // Arrange
        when(ownerRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/owners")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    
    private Owner createOwner(int id, String firstName, String lastName) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        return owner;
    }
}