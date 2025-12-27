package io.pillopl.library.lending;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration class for Lending module integration tests.
 * <p>
 * This class sets up the Spring application context required for testing the Lending module.
 * It imports the production configuration {@link LendingConfig} to ensure the test environment
 * mirrors the production setup closely.
 * </p>
 *
 * @see LendingConfig
 */
@Configuration
@Import({LendingConfig.class})
public class LendingTestContext {
}