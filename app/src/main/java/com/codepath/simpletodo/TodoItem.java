package com.codepath.simpletodo;

import java.util.Date;

/**
 * Created by briasullivan on 9/11/16.
 */
public class TodoItem {
    public long id;
    public String itemText;
    public Date dueDate;
    public Urgency urgency;
    public Status status;
    public Repeat repeat;

    public enum Urgency {
        HIGH(0),
        MEDIUM(1),
        LOW(2);

        private int urgency;

        Urgency(int urgency) {
            this.urgency = urgency;
        }

        public int getUrgency() {
            return urgency;
        }
    }
    public enum Status {
        TODO(0),
        DONE(1);

        private int status;

        Status(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
    public enum Repeat {
        NO_REPEAT(0),
        ONCE_A_DAY(1),
        ONCE_A_WEEK(2),
        ONCE_A_MONTH(3);

        private int repeat;

        Repeat(int repeat) {
            this.repeat = repeat;
        }

        public int getRepeat() {
            return repeat;
        }
    }
    public TodoItem(){}
    public TodoItem(String itemText, Date dueDate, Urgency urgency, Status status, Repeat repeat) {
        this.itemText = itemText;
        this.dueDate = dueDate;
        this.urgency = urgency;
        this.status = status;
        this.repeat = repeat;
    }
}
