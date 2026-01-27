package com.chatapp.chat.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @ParameterizedTest
    @MethodSource("displayNameData")
    @DisplayName("getDisplayName() should compute display name correctly for multiple input combinations")
    void testGetDisplayName_Parameterized(
            String username,
            String firstName,
            String lastName,
            String expected
    ) {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .build();

        // when
        String displayName = user.getDisplayName();

        // then
        assertThat(displayName).startsWith(expected);
    }

    static Stream<Arguments> displayNameData() {
        return Stream.of(
                Arguments.of(null, null, null, "User-"),
                Arguments.of(null, null, "", "User-"),
                Arguments.of(null, null, "Smith", "User-"),
                Arguments.of(null, "", null, "User-"),
                Arguments.of(null, "", "", "User-"),
                Arguments.of(null, "", "Smith", "User-"),
                Arguments.of(null, "John", null, "John"),
                Arguments.of(null, "John", "", "John"),
                Arguments.of(null, "John", "Smith", "John Smith"),
                Arguments.of("", null, null, "User-"),
                Arguments.of("", null, "", "User-"),
                Arguments.of("", null, "Smith", "User-"),
                Arguments.of("", "", null, "User-"),
                Arguments.of("", "", "", "User-"),
                Arguments.of("", "", "Smith", "User-"),
                Arguments.of("", "John", null, "John"),
                Arguments.of("", "John", "", "John"),
                Arguments.of("", "John", "Smith", "John Smith"),
                Arguments.of("JS2026", null, null, "JS2026"),
                Arguments.of("JS2026", null, "", "JS2026"),
                Arguments.of("JS2026", null, "Smith", "JS2026"),
                Arguments.of("JS2026", "", null, "JS2026"),
                Arguments.of("JS2026", "", "", "JS2026"),
                Arguments.of("JS2026", "", "Smith", "JS2026"),
                Arguments.of("JS2026", "John", null, "JS2026"),
                Arguments.of("JS2026", "John", "", "JS2026"),
                Arguments.of("JS2026", "John", "Smith", "JS2026")
        );
    }

    @Test
    @DisplayName("getDisplayName() should fallback to User-<id> when no name is provided")
    void testGetDisplayName_FallbackToUserPrefix() {
        // given
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .build();
        user.setId(id);

        // when
        String result = user.getDisplayName();

        assertTrue(result.startsWith("User-"));
        assertEquals("User-" + id.toString().substring(0, 8), result);
    }

    @Test
    @DisplayName("@PrePersist should set createdAt and updatedAt to the same instant")
    void testOnCreateSetsCreatedAndUpdatedDates() {
        // given
        User user = new User();

        // when
        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(user.getCreatedAt(), user.getUpdatedAt());
    }

    @Test
    @DisplayName("@PreUpdate should update updatedAt to a later value")
    void testOnUpdateUpdatesUpdatedAt() throws NoSuchFieldException, IllegalAccessException {
        // given
        User user = new User();
        user.onCreate();

        Instant initial = user.getUpdatedAt();
        Instant future = initial.plusMillis(10);

        var updatedAtField = User.class.getDeclaredField("updatedAt");
        updatedAtField.setAccessible(true);
        updatedAtField.set(user, future.minusMillis(100));

        // when
        user.onUpdate();
        Instant updated = user.getUpdatedAt();

        // then
        assertThat(updated).isAfterOrEqualTo(initial);
    }
}
