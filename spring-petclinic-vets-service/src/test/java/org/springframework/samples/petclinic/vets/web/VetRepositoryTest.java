package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.samples.petclinic.vets.model.Specialty;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VetRepository vetRepository;

    @Test
    void shouldFindAllVets() {
        // given
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        entityManager.persist(vet);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        entityManager.persist(vet2);

        entityManager.flush();

        // when
        List<Vet> vets = vetRepository.findAll();

        // then
        assertThat(vets).hasSize(2);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
        assertThat(vets.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void shouldFindVetWithSpecialties() {
        // given
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        entityManager.persist(specialty);

        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.addSpecialty(specialty);
        entityManager.persist(vet);
        entityManager.flush();

        // when
        List<Vet> vets = vetRepository.findAll();

        // then
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getSpecialties()).hasSize(1);
        assertThat(vets.get(0).getSpecialties().get(0).getName()).isEqualTo("radiology");
    }
} 