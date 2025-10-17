package com.metamapa.repository;

import com.metamapa.domain.MensajeOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOutboxRepository extends JpaRepository<MensajeOutbox, Long> {
}

