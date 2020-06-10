package net.sharksystem.eID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelfAuthWorkflowTest {

    private UserData normalUser;

    @BeforeEach
    void createNormalUser() {
        normalUser = new UserData();
        normalUser.setAddress("HAUPTSTR. 1");
        normalUser.setBirthName("");
        normalUser.setDateOfBirth("01.04.2020");
        normalUser.setDoctoralDegree("");
        normalUser.setDocumentType("ID");
        normalUser.setFamilyName("MUSTERMANN");
        normalUser.setGivenNames("MAX");
        normalUser.setIssuingCountry("D");
        normalUser.setNationality("D");
        normalUser.setPlaceOfBirth("BERLIN");
        normalUser.setArtisticName("");
    }

    @Test
    void GivenAuthSuccessfullyEnded_WhenDataFormattedAsExpected_ThenReturnUser() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<UserData> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            assertDoesNotThrow(() -> {
                UserData r = w.run(out, in);
                result.add(r);
            });
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"data\": {" +
                "    \"Address\": \"HAUPTSTR. 1\"," +
                "    \"Birth name\": \"\"," +
                "    \"Date of birth\": \"01.04.2020\"," +
                "    \"Doctoral degree\": \"\"," +
                "    \"Document type\": \"ID\"," +
                "    \"Family name\": \"MUSTERMANN\"," +
                "    \"Given name(s)\": \"MAX\"," +
                "    \"Issuing country\": \"D\"," +
                "    \"Nationality\": \"D\"," +
                "    \"Place of birth\": \"BERLIN\"," +
                "    \"Religious / artistic name\": \"\"" +
                "  }," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok\"" +
                "  }" +
                "}");

        UserData user = assertDoesNotThrow(result::take);
        assertEquals(normalUser, user);
        assertArrayEquals(normalUser.uuid(), user.uuid());
    }

    @Test
    void GivenAuthSuccessfullyEnded_WhenDataIsMixedCase_ThenReturnUser() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<UserData> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            assertDoesNotThrow(() -> {
                UserData r = w.run(out, in);
                result.add(r);
            });
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"data\": {" +
                "    \"Address\": \"Hauptstr.    1\"," +
                "    \"Birth name\": \"\"," +
                "    \"Date of birth\": \"01.04.2020\"," +
                "    \"Doctoral degree\": \"\"," +
                "    \"Document type\": \"id\"," +
                "    \"Family name\": \"Mustermann\"," +
                "    \"Given name(s)\": \"Max\"," +
                "    \"Issuing country\": \"D\"," +
                "    \"Nationality\": \"D\"," +
                "    \"Place of birth\": \"berlin\"," +
                "    \"Religious / artistic name\": \"\"" +
                "  }," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok\"" +
                "  }" +
                "}");

        UserData user = assertDoesNotThrow(result::take);
        assertEquals(normalUser, user);
        assertArrayEquals(normalUser.uuid(), user.uuid());
    }

    @Test
    void GivenAuthSuccessfullyEnded_WhenKeysAreDifferent_A_ThenReturnUser() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<UserData> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            assertDoesNotThrow(() -> {
                UserData r = w.run(out, in);
                result.add(r);
            });
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"data\": {" +
                "    \"Address\": \"Hauptstr. 1\"," +
                "    \"BirthName\": \"\"," +
                "    \"Date of birth\": \"01.04.2020\"," +
                "    \"Doctoral degree\": \"\"," +
                "    \"Document type\": \"id\"," +
                "    \"FamilyName\": \"Mustermann\"," +
                "    \"Given name\": \"Max\"," +
                "    \"Issuing country\": \"D\"," +
                "    \"Nationality\": \"D\"," +
                "    \"birthplace\": \"Berlin\"," +
                "    \"Artistic name\": \"\"" +
                "  }," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok\"" +
                "  }" +
                "}");

        UserData user = assertDoesNotThrow(result::take);
        assertEquals(normalUser, user);
        assertArrayEquals(normalUser.uuid(), user.uuid());
    }

    @Test
    void GivenAuthSuccessfullyEnded_WhenKeysAreDifferent_B_ThenReturnUser() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<UserData> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            assertDoesNotThrow(() -> {
                UserData r = w.run(out, in);
                result.add(r);
            });
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"data\": {" +
                "    \"Address\": \"Hauptstr. 1\"," +
                "    \"BirthName\": \"\"," +
                "    \"BirthDate\": \"01.04.2020\"," +
                "    \"DoctoralDegree\": \"\"," +
                "    \"DocumentType\": \"id\"," +
                "    \"FamilyName\": \"Mustermann\"," +
                "    \"GivenName\": \"Max\"," +
                "    \"IssuingCountry\": \"D\"," +
                "    \"Nationality\": \"D\"," +
                "    \"PlaceOfBirth\": \"Berlin\"," +
                "    \"Religious/ArtisticName\": \"\"" +
                "  }," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok\"" +
                "  }" +
                "}");

        UserData user = assertDoesNotThrow(result::take);
        assertEquals(normalUser, user);
        assertArrayEquals(normalUser.uuid(), user.uuid());
    }

    @Test
    void GivenPinEnterRequest_WhenPinIsAlreadySend_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<Object> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            try {
                w.run(out, in);
            } catch (Workflow.WorkflowException e) {
                result.add(new Object());
            } catch (InterruptedException e) {
                fail(e);
            }
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"ENTER_PIN\"" +
                "}");

        String s = assertDoesNotThrow(in::take);
        assertEquals("{\"cmd\": \"SET_PIN\", \"value\": \"123456\"}", s);

        out.add("{" +
                "  \"msg\": \"ENTER_PIN\"" +
                "}");

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertNotNull(result.take()));
        assertEquals(1, in.remainingCapacity());
    }

    @Test
    void GivenRunningWorkflow_WhenCanceled_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<Object> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            try {
                w.run(out, in);
            } catch (Workflow.WorkflowException e) {
                result.add(new Object());
            } catch (InterruptedException e) {
                fail(e);
            }
        });
        runner.start();

        w.cancel(in);

        String s = assertDoesNotThrow(in::take);
        assertEquals("{\"cmd\": \"CANCEL\"}", s);

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#error\"," +
                "    \"minor\": \"http://www.bsi.bund.de/ecard/api/1.1/resultminor/sal#cancellationByUser\"" +
                "  }" +
                "}");

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertNotNull(result.take()));
        assertEquals(1, in.remainingCapacity());
    }

    @Test
    void GivenRunningWorkflow_WhenAuthMessageWithMissingDataReceived_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123456");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        Chan<Object> result = new Chan<>(1);
        Thread runner = new Thread(() -> {
            try {
                w.run(out, in);
            } catch (Workflow.WorkflowException e) {
                result.add(new Object());
            } catch (InterruptedException e) {
                fail(e);
            }
        });
        runner.start();

        out.add("{" +
                "  \"msg\": \"AUTH\"," +
                "  \"result\": {" +
                "    \"major\": \"http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok\"" +
                "  }" +
                "}");

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertNotNull(result.take()));
        assertEquals(1, in.remainingCapacity());
    }

    @Test
    void GivenPinTooLong_WhenRun_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("1234567");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        assertThrows(Workflow.WorkflowException.class, () -> w.run(out, in));
    }

    @Test
    void GivenPinTooShort_WhenRun_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("123");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        assertThrows(Workflow.WorkflowException.class, () -> w.run(out, in));
    }

    @Test
    void GivenPinAlphanumeric_WhenRun_ThenWorkflowException() {
        SelfAuthWorkflow w = new SelfAuthWorkflow("12Abc");

        Chan<String> in = new Chan<>(1);
        Chan<String> out = new Chan<>(1);
        assertThrows(Workflow.WorkflowException.class, () -> w.run(out, in));
    }
}