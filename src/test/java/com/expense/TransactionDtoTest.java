package com.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class TransactionDtoTest {

    @Inject
    ObjectMapper mapper;

    private final TransactionDto costco = new TransactionDto(
            "abc", "Costco", "Groceries", false, 1234, LocalDate.of(2026, 9, 7)
    );

    @Test
    void fromCopiesEveryFieldExceptUserId() {
        Transaction entity = new Transaction(
                "abc", "Costco", "Groceries", false, 1234, LocalDate.of(2026, 9, 7), "secret-user-id"
        );

        assertEquals(costco, TransactionDto.from(entity));
    }

    @Test
    void serializesWithTheFieldNamesTheFrontendExpects() throws Exception {
        String json = mapper.writeValueAsString(costco);

        assertTrue(json.contains("\"id\":\"abc\""), json);
        assertTrue(json.contains("\"isIncome\":false"), json);
        assertTrue(json.contains("\"amountInCents\":1234"), json);
        assertTrue(json.contains("\"transactionDate\":\"2026-09-07\""), json);
        assertFalse(json.contains("userId"), json);
    }

    @Test
    void ignoresUserIdSentByTheClient() throws Exception {
        String json = """
            {"id":"abc","merchant":"Costco","category":"Groceries","isIncome":false,
             "amountInCents":1234,"transactionDate":"2026-09-07","userId":"attacker"}
            """;

        assertEquals(costco, mapper.readValue(json, TransactionDto.class));
    }
}