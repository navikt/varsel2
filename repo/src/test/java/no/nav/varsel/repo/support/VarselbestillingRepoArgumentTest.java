package no.nav.varsel.repo.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class})
public class VarselbestillingRepoArgumentTest {

	private static final LocalDateTime FOM_DATE = LocalDateTime.of(2016, Month.JUNE, 1, 13, 0);
	private static final LocalDateTime TOM_DATE = LocalDateTime.of(2016, Month.JULY, 1, 14, 0);

	@InjectMocks
	private VarselbestillingRepoImpl varselbestillingRepo;

	@Test
	public void throwsIllegalArgumentExceptionWhenBrukerParameterIsNull() {
		Executable executable = () -> varselbestillingRepo.findFerdigbehandletVarselbestillinger(null, FOM_DATE, TOM_DATE);
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "bruker is null or empty");

	}

	@Test
	public void throwsIllegalArgumentExceptionWhenBrukerParameterIsEmpty() {
		Executable executable = () -> varselbestillingRepo.findFerdigbehandletVarselbestillinger("", FOM_DATE, TOM_DATE);
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "bruker is null or empty");
	}
}
