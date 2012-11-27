package at.fhj.itm10.mobcomp.drivebyreminder.models;

import java.util.Calendar;

/**
 * Represents a task stored in this application.
 *
 * @author Wolfgang Gaar
 */
public class Task {

	private long id;

	private String title;

	/**
	 * Zero indicates usage of the default proximitry.
	 */
	private int customProximitry = 0;

	private Calendar startDate;

	private Calendar endDate;

	private boolean noDate;

	private boolean done;

	private String description;

	private long sorting;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public int getCustomProximitry() {
		return customProximitry;
	}

	public void setCustomProximitry(final int customProximitry) {
		this.customProximitry = customProximitry;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(final Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public boolean isNoDate() {
		return noDate;
	}

	public void setNoDate(final boolean noDate) {
		this.noDate = noDate;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(final boolean done) {
		this.done = done;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public long getSorting() {
		return sorting;
	}

	public void setSorting(final long sorting) {
		this.sorting = sorting;
	}
}
