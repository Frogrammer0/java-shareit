import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ShareItServerTest {

    private ConfigurableApplicationContext context;

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @DisplayName("Метод main запускает Spring приложение")
    void mainMethodStartsSpringApplication() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            System.setErr(new PrintStream(outputStream));

            String[] args = new String[]{
                    "--spring.profiles.active=test",
                    "--spring.datasource.url=jdbc:h2:mem:test",
                    "--spring.main.web-application-type=none"
            };

            assertDoesNotThrow(() -> ShareItServer.main(args),
                    "Метод main не должен бросать исключения");

        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    @DisplayName("SpringApplication.run может быть вызван для основного класса")
    void springApplicationRunWithMainClass() {
        assertDoesNotThrow(() -> {
            context = SpringApplication.run(ShareItServer.class,
                    "--spring.profiles.active=test",
                    "--spring.datasource.url=jdbc:h2:mem:test",
                    "--spring.main.web-application-type=none");

            assertTrue(context.isActive(), "Контекст должен быть активен");
        });
    }
}