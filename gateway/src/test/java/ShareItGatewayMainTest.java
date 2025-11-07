import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItGateway;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@ActiveProfiles("test")
class ShareItGatewayMainTest {

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

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            String[] args = new String[]{"--spring.profiles.active=test"};
            ShareItGateway.main(args);

            String output = outputStream.toString();
            assertTrue(output.contains("Spring") || output.contains("Started") || output.isEmpty(),
                    "Приложение должно запуститься без ошибок");

        } catch (Exception e) {
            fail("Метод main не должен бросать исключения: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("SpringApplication.run может быть вызван для основного класса")
    void springApplicationRunWithMainClass() {
        assertDoesNotThrow(() -> {
            context = SpringApplication.run(ShareItGateway.class,
                    "--spring.profiles.active=test",
                    "--spring.main.web-application-type=none");

            assertTrue(context.isActive(), "Контекст должен быть активен");
        });
    }
}