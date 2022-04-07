package com.vaadin.demo.servermodailty.data.service;

import com.vaadin.demo.servermodailty.data.entity.SamplePerson;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}