package org.HibernateExamples.Event;

import Shared.HibernateTestBase;
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

class EventTest extends HibernateTestBase {

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
    @Rollback
    void findNotExistEvent() {
        Event eventFromDb = (session.find(Event.class, 2L));
        assertThat(eventFromDb).isNull();
    }

}