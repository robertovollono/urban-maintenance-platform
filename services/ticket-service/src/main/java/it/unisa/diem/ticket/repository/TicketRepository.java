package it.unisa.diem.ticket.repository;

import it.unisa.diem.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Grazie a Spring Data JPA, non serve implementare i metodi base (save, findById, etc.)
    // Li abbiamo gratis.
    
    // In futuro aggiungeremo qui le query spaziali (es. findWithinRadius)
}