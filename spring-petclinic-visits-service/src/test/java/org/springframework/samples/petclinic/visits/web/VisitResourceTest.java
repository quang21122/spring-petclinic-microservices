package org.springframework.samples.petclinic.visits.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitResourceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitResource visitResource;

    private Visit visit;
    // test

    @BeforeEach
    void setUp() {
        visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(1)
            .date(new Date())
            .description("Regular checkup")
            .build();
    }

    @Test
    void shouldCreateNewVisit() {
        // Given
        given(visitRepository.save(any(Visit.class))).willReturn(visit);

        // When
        Visit created = visitResource.create(visit, 1);

        // Then
        assertNotNull(created);
        assertEquals(visit.getId(), created.getId());
        assertEquals(visit.getPetId(), created.getPetId());
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void shouldGetVisitsByPetId() {
        // Given
        given(visitRepository.findByPetId(1)).willReturn(List.of(visit));

        // When
        List<Visit> visits = visitResource.read(1);

        // Then
        assertNotNull(visits);
        assertEquals(1, visits.size());
        assertEquals(visit.getId(), visits.get(0).getId());
        verify(visitRepository).findByPetId(1);
    }

    @Test
    void shouldGetVisitsByPetIds() {
        // Given
        List<Integer> petIds = Arrays.asList(1, 2);
        List<Visit> expectedVisits = Arrays.asList(visit);
        given(visitRepository.findByPetIdIn(petIds)).willReturn(expectedVisits);

        // When
        VisitResource.Visits visits = visitResource.read(petIds);

        // Then
        assertNotNull(visits);
        assertEquals(1, visits.items().size());
        assertEquals(visit.getId(), visits.items().get(0).getId());
        verify(visitRepository).findByPetIdIn(petIds);
    }

    @Test
    void shouldReturnEmptyVisitsWhenNoPetIdsProvided() {
        // Given
        List<Integer> petIds = List.of();
        given(visitRepository.findByPetIdIn(petIds)).willReturn(List.of());

        // When
        VisitResource.Visits visits = visitResource.read(petIds);

        // Then
        assertNotNull(visits);
        assertTrue(visits.items().isEmpty());
        verify(visitRepository).findByPetIdIn(petIds);
    }
}
