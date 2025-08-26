package org.HibernateExamples.Event;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Events")
public class Event {
    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "eventDate")
    private LocalDateTime eventDate;

    public Event() {
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;

        return getId().equals(event.getId()) && getTitle().equals(event.getTitle()) && getEventDate().equals(event.getEventDate());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getEventDate().hashCode();
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
