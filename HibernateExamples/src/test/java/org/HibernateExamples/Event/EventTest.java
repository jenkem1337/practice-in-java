package org.HibernateExamples.Event;

import Shared.Rollback;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EventTest {
    static SessionFactory sessionFactory;
    Transaction tx;
    Session session;
    @BeforeAll
    static void setUpSessionFactory(){
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .build();

        try {
            sessionFactory =
                    new MetadataSources(registry)
                            .addAnnotatedClass(Event.class)
                            .buildMetadata()
                            .buildSessionFactory();

        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
    @BeforeEach
    void setUpSessionAndTransaction(){
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }

    @AfterAll
    static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        boolean rollbackEnabled = testInfo.getTestMethod()
                .map(m -> m.isAnnotationPresent(Rollback.class))
                .orElse(false);

        if (rollbackEnabled && tx != null && tx.isActive()) {
            tx.rollback();
        } else if (tx != null && tx.isActive()) {
            tx.commit();
        }

        if (session != null) {
            session.close();
        }
    }

    @Test
    @Rollback
    void insertEvent() {
        var event = new Event();
        event.setTitle("Project X Finike");
        event.setEventDate(LocalDateTime.now());
        session.persist(event);
        Event eventFromDb = session.find(Event.class, 1L);
        assertThat(eventFromDb).isNotNull();
        assertThat(eventFromDb.getTitle()).isEqualTo("Project X Finike");
    }

    @Test
    void findNotExistEvent() {
        Event eventFromDb = (session.find(Event.class, 2L));
        assertThat(eventFromDb).isNull();
    }

}