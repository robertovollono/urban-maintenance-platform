package it.unisa.diem.ticket;

import it.unisa.diem.ticket.config.multitenancy.TenantContext;
import it.unisa.diem.ticket.model.Ticket;
import it.unisa.diem.ticket.model.TicketStatus;
import it.unisa.diem.ticket.repository.TicketRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// RIMOSSO @Transactional: Vogliamo che ogni operazione apra una nuova connessione
// per permettere allo switch dello schema di funzionare.
@SpringBootTest
class MultiTenancyIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    // Pulizia manuale dopo ogni test (perché non abbiamo più il rollback automatico)
    @AfterEach
    public void cleanup() {
        TenantContext.setTenantId("schema_salerno");
        ticketRepository.deleteAll();
        
        TenantContext.setTenantId("schema_napoli");
        ticketRepository.deleteAll();
        
        TenantContext.clear();
    }

    @Test
    void testDataIsolationBetweenTenants() {
        System.out.println("--- INIZIO TEST MULTI-TENANCY ---");

        // 1. SALERNO
        TenantContext.setTenantId("schema_salerno");
        
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        Point locationSalerno = gf.createPoint(new Coordinate(14.768, 40.682));

        Ticket ticketSalerno = Ticket.builder()
                .creatorId("cittadino_salerno")
                .category("Roads")
                .description("Buca enorme via dei Principati")
                .status(TicketStatus.OPEN)
                .location(locationSalerno)
                .build();

        // Qui Spring apre una transazione, vede il TenantContext, e prende la connessione su schema_salerno
        ticketRepository.save(ticketSalerno);
        Assertions.assertEquals(1, ticketRepository.count());
        
        TenantContext.clear(); // Chiudo il contesto Salerno

        // ---------------------------------------------------

        // 2. NAPOLI
        TenantContext.setTenantId("schema_napoli");
        
        // Qui Spring apre una NUOVA transazione. Il Provider vede "schema_napoli" e cambia schema.
        long countNapoli = ticketRepository.count();
        System.out.println("Ticket trovati a Napoli: " + countNapoli);
        
        // ORA DEVE ESSERE 0
        Assertions.assertEquals(0, countNapoli, "Napoli NON deve vedere i ticket di Salerno!");

        // Creo un ticket a Napoli
        Point locationNapoli = gf.createPoint(new Coordinate(14.268, 40.851));
        Ticket ticketNapoli = Ticket.builder()
                .creatorId("cittadino_napoli")
                .category("Waste")
                .description("Rifiuti piazza del Plebiscito")
                .status(TicketStatus.OPEN)
                .location(locationNapoli)
                .build();
        
        ticketRepository.save(ticketNapoli);
        Assertions.assertEquals(1, ticketRepository.count());

        System.out.println("--- TEST SUPERATO CON SUCCESSO ---");
    }
}