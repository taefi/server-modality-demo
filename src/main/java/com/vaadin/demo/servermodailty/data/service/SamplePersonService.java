package com.vaadin.demo.servermodailty.data.service;

import com.vaadin.demo.servermodailty.data.entity.SamplePerson;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SamplePersonService {

    private static final Logger logger = LoggerFactory.getLogger(SamplePersonService.class);

    private final SamplePersonRepository repository;

    @Autowired
    public SamplePersonService(SamplePersonRepository repository) {
        this.repository = repository;
    }

    public Optional<SamplePerson> get(UUID id) {
        return repository.findById(id);
    }

    public SamplePerson update(SamplePerson entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<SamplePerson> list(Pageable pageable) {
        if (count() == 0) {
            generateData();
        }
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    private void generateData() {
        int numberOfData = 10000;
        logger.info("Generating demo data...");
        logger.info(String.format("... generating %d Sample Person entities...", numberOfData));

        ExampleDataGenerator<SamplePerson> samplePersonRepositoryGenerator = new ExampleDataGenerator<>(
                SamplePerson.class, LocalDateTime.of(2022, 4, 4, 0, 0, 0));
        samplePersonRepositoryGenerator.setData(SamplePerson::setFirstName, DataType.FIRST_NAME);
        samplePersonRepositoryGenerator.setData(SamplePerson::setLastName, DataType.LAST_NAME);
        samplePersonRepositoryGenerator.setData(SamplePerson::setEmail, DataType.EMAIL);
        samplePersonRepositoryGenerator.setData(SamplePerson::setPhone, DataType.PHONE_NUMBER);
        samplePersonRepositoryGenerator.setData(SamplePerson::setDateOfBirth, DataType.DATE_OF_BIRTH);
        samplePersonRepositoryGenerator.setData(SamplePerson::setOccupation, DataType.OCCUPATION);
        samplePersonRepositoryGenerator.setData(SamplePerson::setImportant, DataType.BOOLEAN_10_90);

        int seed = 123;
        repository.saveAll(samplePersonRepositoryGenerator.create(numberOfData, seed));

        logger.info(String.format("Generated %d demo data", numberOfData));
    }

}
