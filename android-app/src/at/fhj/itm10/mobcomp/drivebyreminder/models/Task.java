package at.fhj.itm10.mobcomp.drivebyreminder.models;

import java.util.Calendar;

/**
 * Represents a task stored in this application.
 * 
 * @author Wolfgang Gaar
 */
public class Task {

	private int id;
	
	private String title;
	
	/**
	 * Zero indicates usage of the default proximitry.
	 */
	private int customProximitry = 0;
	
	private Calendar startDate;
	
	private Calendar endDate;
	
	private boolean noDate;
	
	private boolean done;
	
	private int sorting;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCustomProximitry() {
		return customProximitry;
	}

	public void setCustomProximitry(int customProximitry) {
		this.customProximitry = customProximitry;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
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

	public void setNoDate(boolean noDate) {
		this.noDate = noDate;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getSorting() {
		return sorting;
	}

	public void setSorting(int sorting) {
		this.sorting = sorting;
	}
}