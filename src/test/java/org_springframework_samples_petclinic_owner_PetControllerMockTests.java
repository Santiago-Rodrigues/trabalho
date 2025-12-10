package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

class PetControllerMockTests {

	private OwnerRepository owners;
	private PetTypeRepository types;
	private PetController controller;

	@BeforeEach
	void setup() {
		this.owners = Mockito.mock(OwnerRepository.class);
		this.types = Mockito.mock(PetTypeRepository.class);
		this.controller = new PetController(owners, types);

		PetType hamster = new PetType();
		hamster.setId(3);
		hamster.setName("hamster");
		given(this.types.findPetTypes()).willReturn(List.of(hamster));

		Owner owner = new Owner();
		owner.setId(1);
		Pet existing = new Pet();
		existing.setId(2);
		existing.setName("petty");
		owner.addPet(existing);
		given(this.owners.findById(1)).willReturn(Optional.of(owner));
	}

	@Test
	void processCreationFormSuccess() {
		ModelMap model = new ModelMap();
		Pet pet = new Pet();
		pet.setName("Betty");
		pet.setType(this.types.findPetTypes().get(0));
		BindingResult result = new BeanPropertyBindingResult(pet, "pet");

		String view = controller.processCreationForm(pet, result, 1, model);
		assertThat(view).startsWith("redirect:/owners/");
	}

	@Test
	void processCreationFormDuplicateName() {
		ModelMap model = new ModelMap();
		Pet pet = new Pet();
		pet.setName("petty"); // duplicado
		BindingResult result = new BeanPropertyBindingResult(pet, "pet");

		String view = controller.processCreationForm(pet, result, 1, model);
		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
	}
}