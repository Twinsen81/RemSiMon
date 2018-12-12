package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.evartem.remsimon.data.types.pinging.PingingTaskResult.ERROR_TIMEOUT;
import static com.evartem.remsimon.data.types.pinging.PingingTaskResult.NO_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
// Using Robolectric to provide implementation for the Regex class (HybridPinger.isValidUrl)
public class PingingTaskTest {

    @Mock
    private
    Pinger pinger;

    private static final String TASK1_DESCRIPTION = "Test task - always available google's DNS";
    private static final String TASK1_PING_ADDRESS = "8.8.8.8";  // google's dns
    private static final int TASK1_RUN_EVERY_MS = 100;
    private static final int TASK1_PING_TIMEOUT = 500;

    private static final String TASK2_DESCRIPTION = "Test task - non-existent IP that always times out";
    private static final String TASK2_PING_ADDRESS = "192.168.88.88";
    private static final int TASK2_RUN_EVERY_MS = 100;
    private static final int TASK2_PING_TIMEOUT = 10;

    private static final String TASK3_DESCRIPTION = "Test task - invalid URL";
    private static final String TASK3_PING_ADDRESS = "NotAURLatAll";
    private static final int TASK3_RUN_EVERY_MS = 100;
    private static final int TASK3_PING_TIMEOUT = 999;

    private static final String jsonResultInvalidUrl = "{\"errorCode\":1,\"errorMessage\":\"Not a valid URL\",\"lastSuccessTime\":0,\"pingOK\":false,\"pingTimeMs\":0}";

    private PingingTask TASK1_Ok = PingingTask.create(TASK1_DESCRIPTION, TASK1_RUN_EVERY_MS, TASK1_PING_ADDRESS, TASK1_PING_TIMEOUT);
    private PingingTask TASK2_NoPing = PingingTask.create(TASK2_DESCRIPTION, TASK2_RUN_EVERY_MS, TASK2_PING_ADDRESS, TASK2_PING_TIMEOUT);
    private PingingTask TASK3_WrongUrl = PingingTask.create(TASK3_DESCRIPTION, TASK3_RUN_EVERY_MS, TASK3_PING_ADDRESS, TASK3_PING_TIMEOUT);


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void anInstanceIsProperlyInitialized() {
        // Given a task, make sure all the fields are initialized properly
        assertNotNull(TASK1_Ok.jsonAdapter);
        assertNotNull(TASK1_Ok.settings);
        assertNotNull(TASK1_Ok.pinger);
        assertNotNull(UUID.fromString(TASK1_Ok.getTaskId()));
        assertThat(TASK1_Ok.getDescription(), not(isEmptyString()));
        assertNotNull(TASK1_Ok.getLastResultJson());
        assertThat(TASK1_Ok.getMode(), is(MonitoringTask.MODE_STOPPED));
        assertFalse(TASK1_Ok.isWorking() || TASK1_Ok.isFinished());
    }

    @Test
    public void workStageIsChanging() {

        // The work stage must change from STOPPED -> IN_PROGRESS -> FINISHED_AWAITING_NEXT_EXECUTION

        // Set our mocked pinger
        TASK1_Ok.setPinger(pinger);
        Answer pingAnswer = invocation -> { // Will be called when the task calls ping(...)
            assertTrue(TASK1_Ok.isWorking()); // Should be set to IN_PROGRESS while in the middle of the work
            assertFalse(TASK1_Ok.isFinished());
            return new PingingTaskResult(false, 0);
        };
        Mockito.doAnswer(pingAnswer).when(pinger).ping(TASK1_Ok.settings);

        // When the work is executed
        TASK1_Ok.doTheWork();

        // Then, when the work is done,  the stage should be FINISHED_AWAITING_NEXT_EXECUTION
        assertFalse(TASK1_Ok.isWorking());
        assertTrue(TASK1_Ok.isFinished());
    }

    @Test
    public void getType_ShouldReturn_TaskTypePINGING() {
        assertThat(TASK1_Ok.getType(), is(TaskType.PINGING));
    }


    @Test
    public void invalidUrlAddressReturnsErrorJsonResult() {
        //Given invalid addresses
        TASK1_Ok.settings.setPingAddress("1.1.1");

        // When the work is executed
        TASK1_Ok.doTheWork();
        TASK3_WrongUrl.doTheWork();

        // Then the corresponding results with error messages are returned
        assertThat(TASK1_Ok.getLastResultJson(), is(jsonResultInvalidUrl));
        assertThat(TASK3_WrongUrl.getLastResultJson(), is(jsonResultInvalidUrl));
    }

    @Test
    public void googleUrlAlwaysPingsSuccessfully() {

        // Given an always available URL in the task
        TASK1_Ok.setPinger(new JavaPinger()); // Replace native pinger with JavaPinger for the test

        // When the work is executed
        TASK1_Ok.doTheWork();

        // Then the result is positive
        PingingTaskResult result = TASK1_Ok.getLastResult();
        assertTrue(result.pingOK);
        assertTrue(result.pingTimeMs > 0 && result.pingTimeMs < TASK1_PING_TIMEOUT);
        assertThat(result.errorMessage, isEmptyString());
        assertThat(result.errorCode, is(NO_ERROR));
    }

    @Test
    public void ifUrlIsNotReachableThenReturnTimeout() {
        // Given a URL that is surely not available
        TASK2_NoPing.setPinger(new JavaPinger()); // Replace native pinger with JavaPinger for the test

        // When the work is executed
        TASK2_NoPing.doTheWork();

        // Then the result is - timeout
        PingingTaskResult result = TASK2_NoPing.getLastResult();
        assertFalse(result.pingOK);
        assertTrue(result.pingTimeMs > 0);
        assertTrue(result.errorMessage.length() > 0);
        assertThat(result.errorCode, is(ERROR_TIMEOUT));
    }

    @Test
    public void stateChangedFlagIsRaisedAfterExecution() {
        // Given a task
        TASK1_Ok.setPinger(pinger); // Set our mocked pinger
        Answer pingAnswer = invocation -> { // Will be called when the task calls ping(...)
            return new PingingTaskResult(false, 0);
        };
        Mockito.doAnswer(pingAnswer).when(pinger).ping(TASK1_Ok.settings);

        // No changes before the task is executed
        assertFalse(TASK1_Ok.gotNewResult());

        // When the work is executed
        TASK1_Ok.doTheWork();

        // Then, when the work is done,  the task signals about a new result
        assertTrue(TASK1_Ok.gotNewResult());
        // The second call to gotNewResult should return false (the first call clears the flag)
        assertFalse(TASK1_Ok.gotNewResult());
    }

    @Test
    public void isTimeToExecute() throws InterruptedException {
        // Given a task
        TASK1_Ok.setPinger(pinger); // Set our mocked pinger
        Answer pingAnswer = invocation -> { // Will be called when the task calls ping(...)
            return new PingingTaskResult(false, 0);
        };
        Mockito.doAnswer(pingAnswer).when(pinger).ping(TASK1_Ok.settings);

        // Before the first execution the task signals that it's time to execute it
        assertTrue(TASK1_Ok.isTimeToExecute());

        // When the work is executed
        TASK1_Ok.doTheWork();

        // Then, when the work is done,  the task signals that it's not time to to execute yet again
        assertFalse(TASK1_Ok.isTimeToExecute());
        // But after the TASK1_RUN_EVERY_MS seconds elapse...
        TimeUnit.MILLISECONDS.sleep(TASK1_RUN_EVERY_MS + 10);
        // It's time to execute the task again
        assertTrue(TASK1_Ok.isTimeToExecute());
    }


}