package com.mozen.springboothibernatesearch.service;

import com.mozen.springboothibernatesearch.model.Plant;
import com.mozen.springboothibernatesearch.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class PlantService {

	private PlantRepository plantRepository;

	private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "scientificName", "family");

	public PlantService(PlantRepository plantRepository) {
		this.plantRepository = plantRepository;
	}

	public List<Plant> saveAll(List<Plant> plants) {
		return plantRepository.saveAll(plants);
	}

	public List<Plant> searchPlants(String text, List<String> fields, int limit) {

		List<String> fieldsToSearchBy = fields.isEmpty() ? SEARCHABLE_FIELDS : fields;

		boolean containsInvalidField = fieldsToSearchBy.stream().anyMatch(f -> !SEARCHABLE_FIELDS.contains(f));

		if (containsInvalidField) {
			throw new IllegalArgumentException();
		}

		return plantRepository.searchBy(text, limit, fieldsToSearchBy.toArray(new String[0]));
	}

	public List<Plant> updatePlants() {
		List<Plant> plants = plantRepository.findAll();
		Instant now = Instant.now();
		for (Plant plant : plants) {
			plant.setUpdatedAt(now);
		}
		return plantRepository.saveAll(plants);
	}
}
