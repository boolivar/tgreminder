package org.bool;

import org.bool.tgreminder.core.Reminder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReminderTest {
    
    private final Reminder reminder = new Reminder();
    
    @Test
    void testRemind() {
        assertTrue(reminder.remind());
    }
}
