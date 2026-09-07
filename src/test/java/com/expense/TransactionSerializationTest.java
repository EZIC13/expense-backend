package com.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class TransactionSerializationTest {

    @Inject
    ObjectMapper mapper;

    @Test
    void userIdIsNeverSerialized() throws Exception {
        Transaction t = new Transaction();
        t.id = "abc";
        t.merchant = "Costco";
        t.category = "Groceries";
        t.isIncome = false;
        t.amountInCents = 1234;
        t.transactionDate = LocalDate.of(2026, 9, 7);
        t.userId = "secret-user-id";

        String json = mapper.writeValueAsString(t);

        assertFalse(json.contains("userId"), json);
        assertFalse(json.contains("secret-user-id"), json);
        assertTrue(json.contains("\"isIncome\""), json);
        assertTrue(json.contains("\"id\":\"abc\""), json);
        assertTrue(json.contains("\"transactionDate\":\"2026-09-07\""), json);
    }

    @Test
    void userIdIsNeverDeserialized() throws Exception {
        String json = """
            {"id":"abc","merchant":"Costco","category":"Groceries","isIncome":true,
             "amountInCents":500,"transactionDate":"2026-09-07","userId":"attacker"}
            """;

        Transaction t = mapper.readValue(json, Transaction.class);

        assertNull(t.userId);
        assertEquals("abc", t.id);
        assertTrue(t.isIncome);
        assertEquals(LocalDate.of(2026, 9, 7), t.transactionDate);
    }
}
