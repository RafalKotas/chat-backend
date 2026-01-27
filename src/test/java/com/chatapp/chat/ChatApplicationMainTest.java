package com.chatapp.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class ChatApplicationMainTest {

    @Test
    @DisplayName("main() should invoke SpringApplication.run without errors")
    void mainMethodRuns() {

        // given
        try (MockedStatic<SpringApplication> springMock = Mockito.mockStatic(SpringApplication.class)) {

            springMock.when(() -> SpringApplication.run(ChatApplication.class, new String[]{}))
                    .thenReturn(null);

            // when
            ChatApplication.main(new String[]{});

            // then
            springMock.verify(() -> SpringApplication.run(ChatApplication.class, new String[]{}));
        }
    }
}
