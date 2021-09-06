package edu.pure.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "browsing_history")
public class BrowsingHistoryItem extends EntityRecord {
    private int count;

    public BrowsingHistoryItem(final String name, final CourseName course, final User user) {
        super(name, course, user);
    }

    public void incCount(final int inc) {
        this.count += inc;
    }
}
