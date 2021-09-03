package edu.pure.server.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "favorites")
public class FavoriteItem extends EntityRecord {
}
