package br.com.officyna.infrastructure.logging;

import br.com.officyna.infrastructure.converter.LocalDateTimeConverters;
import br.com.officyna.seed.DatabaseSeeder;
import br.com.officyna.seed.UserSeeder;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InfrastructureComponentsTest {

    @Test
    @DisplayName("CorrelationIdFilter deve propagar ou gerar correlationId nos headers de resposta")
    void correlationIdFilter_ShouldSetHeaders() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNotNull(response.getHeader(CorrelationConstants.CORRELATION_ID_HEADER));
        assertNotNull(response.getHeader(CorrelationConstants.LEGACY_CORRELATION_ID_HEADER));
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("LocalDateTimeConverters deve converter LocalDateTime para Date e vice-versa")
    void localDateTimeConverters_ShouldConvertBidirectionally() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 31, 22, 0, 0);

        Date date = LocalDateTimeConverters.LocalDateTimeToDateConverter.INSTANCE.convert(now);
        assertNotNull(date);

        LocalDateTime convertedBack = LocalDateTimeConverters.DateToLocalDateTimeConverter.INSTANCE.convert(date);
        assertEquals(now, convertedBack);
    }

    @Test
    @DisplayName("DatabaseSeeder deve acionar UserSeeder")
    void databaseSeeder_ShouldRunUserSeeder() {
        UserSeeder userSeeder = mock(UserSeeder.class);
        DatabaseSeeder databaseSeeder = new DatabaseSeeder(userSeeder);

        databaseSeeder.run();

        verify(userSeeder).seed();
    }
}

