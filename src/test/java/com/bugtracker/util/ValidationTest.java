package com.bugtracker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;

class ValidationTest {
    @Test
    void acceptsValidEmailAndEnumValues() {
        assertEquals("qa@example.com", Validation.requireEmail(" qa@example.com "));
        assertEquals(Status.IN_PROGRESS, Validation.parseStatus("in_progress"));
        assertEquals(Severity.CRITICAL, Validation.parseSeverity("critical"));
        assertEquals(Status.CLOSED, Status.fromString("closed"));
        assertEquals(Severity.HIGH, Severity.fromString("high"));
    }

    @Test
    void rejectsEmptyFieldsInvalidEmailIdsAndEnumValues() {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText("  ", "Title"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requireEmail("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requirePositiveId(0, "Bug ID"));
        assertThrows(IllegalArgumentException.class, () -> Validation.parseStatus("WAITING"));
        assertThrows(IllegalArgumentException.class, () -> Validation.parseSeverity("URGENT"));
        assertThrows(IllegalArgumentException.class, () -> Status.fromString("WAITING"));
        assertThrows(IllegalArgumentException.class, () -> Severity.fromString("URGENT"));
    }
}