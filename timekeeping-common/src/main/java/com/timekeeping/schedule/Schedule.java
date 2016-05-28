package com.timekeeping.schedule;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.timekeeping.shop.Shop;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * JPA entity representing an individual scheduled work day.
 * 
 * @author Mikhail Romanenko
 *
 */

@Entity
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "SCHEDULE_DATE")
	private LocalDate date;
	@ManyToOne
	@JoinColumn(name = "SHOP_ID")
	private Shop shop;
	@Version
	private int version;
	@JsonManagedReference
	@OneToMany(mappedBy = "schedule", orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<ScheduleItem> items = new HashSet<>();

	protected Schedule(){
	}

	public Schedule(Shop shop, LocalDate date) {
		this.shop = shop;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	public Set<ScheduleItem> getItems() {
		return this.items;
	}

	public void setItems(Set<ScheduleItem> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	public void addItem(ScheduleItem item) {
		item.setSchedule(this);
		this.items.add(item);
	}
	
	public void removeItem(ScheduleItem item) {
		this.items.remove(item);
		item.setSchedule(null);
	}

	public boolean removeItemIf(Predicate<? super ScheduleItem> filter) {
		return this.items.removeIf(filter);
	}

	public boolean containsItem(ScheduleItem item) {
		return this.items.contains(item);
	}

	public void nullId() {
		this.id = null;
	}

	public void updateItems(Collection<? extends ScheduleItem> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Schedule [id: ").append(id).append(", date: ").append(date)
				.append(", shopId: ").append(shop != null ? shop.getId() : "null")
				.append(", itemsCount: ").append(items.size()).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, id, shop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (shop == null) {
			if (other.shop != null)
				return false;
		} else if (!shop.equals(other.shop))
			return false;
		return true;
	}
	
}
