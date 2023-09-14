package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.compilations.model.Compilations;

public interface CompilationsRepository extends JpaRepository<Compilations, Long>, JpaSpecificationExecutor<Compilations> {

}