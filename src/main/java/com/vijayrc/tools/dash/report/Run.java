package com.vijayrc.tools.dash.report;

import com.vijayrc.tools.dash.config.Category;
import com.vijayrc.tools.dash.config.Source;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
public class Run {
    private String name;
    private Set<Category> categories = new TreeSet<>();
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private State state;
    private Source source;

    public Run(String name, Source source) {
      this(name,source, State.Created);
    }

    public Run(String name, Source source, State state) {
        this.name = name;
        this.source = source;
        this.state = state;
    }

    public Run start() {
        this.state = State.Started;
        this.startTime = LocalDateTime.now();
        return this;
    }

    public Run stop() {
        this.state = State.Stopped;
        this.stopTime = LocalDateTime.now();
        return this;
    }

    public boolean isNew() {
        return this.state != State.Stopped;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public String getName() {
        return name;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return "Run{" +
                "name='" + name + '\'' +
                ", categories=" + categories +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", state=" + state +
                ", source=" + source +
                '}';
    }

    public void reset() {
        categories.clear();
    }

    public Source getSource() {
        return source;
    }

    public String getStartTime() {
        return startTime != null? startTime.toString(): "not-tracked";
    }

    public String getStopTime() {
        return stopTime != null? stopTime.toString(): "not-tracked";
    }

    public String getState() {
        return state.name();
    }

    public enum State {
        Created, Started, Stopped
    }
}
