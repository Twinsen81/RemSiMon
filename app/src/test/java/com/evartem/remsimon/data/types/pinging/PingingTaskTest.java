package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskResult;
import com.evartem.remsimon.data.types.base.TaskType;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class PingingTaskTest {

    @Mock
    Pinger pinger;

        private static final String TASK1_DESCRIPTION = "Test task description";
    private static final String TASK1_PING_ADDRESS = "8.8.8.8";  // google's dns
    private static final int TASK1_RUN_EVERY = 3;
    private static final int TASK1_PING_TIMEOUT = 300;

    private static final String TASK2_DESCRIPTION = "Test task - non-existent IP ";
    private static final String TASK2_PING_ADDRESS = "192.168.88.88";
    private static final int TASK2_RUN_EVERY = 2;
    private static final int TASK2_PING_TIMEOUT = 900;

    private static final String TASK3_DESCRIPTION = "Test task - not a proper URL";
    private static final String TASK3_PING_ADDRESS = "NotAURLatAll";
    private static final int TASK3_RUN_EVERY = 4;
    private static final int TASK3_PING_TIMEOUT = 999;

    PingingTask TASK1_Ok = PingingTask.create(TASK1_DESCRIPTION, TASK1_RUN_EVERY, TASK1_PING_ADDRESS, TASK1_PING_TIMEOUT);
    PingingTask TASK2_NoPing = PingingTask.create(TASK2_DESCRIPTION, TASK2_RUN_EVERY, TASK2_PING_ADDRESS, TASK2_PING_TIMEOUT);
    PingingTask TASK3_WrongUrl = PingingTask.create(TASK3_DESCRIPTION, TASK3_RUN_EVERY, TASK3_PING_ADDRESS, TASK3_PING_TIMEOUT);
    

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
        assertThat(TASK1_Ok.getMode(), is (MonitoringTask.MODE_STOPPED));
        assertFalse(TASK1_Ok.isWorking() || TASK1_Ok.isFinshed());
    }

    @Test
    public void workStageIsChanging() {

        // The work stage must change from STOPPED -> INPROGRESS -> FINISHED

        // Set our mocked pinger
        TASK1_Ok.setPinger(pinger);
        Answer pingAnswer = invocation -> { // Will be called when the task calls ping(...)
            assertTrue(TASK1_Ok.isWorking()); // Should be set to INPROGRESS while in the middle of the work
            assertFalse(TASK1_Ok.isFinshed());
            return new PingingTaskResult(false, 0);
        };
        Mockito.doAnswer(pingAnswer).when(pinger).ping(TASK1_Ok.settings);

        // Start the work
        TASK1_Ok.doTheWork();

        // When the work is done,  the stage should be FINISHED
        assertFalse(TASK1_Ok.isWorking());
        assertTrue(TASK1_Ok.isFinshed());
    }

    @Test
    public void getType_ShouldReturn_TaskTypePINGING() {
        assertThat(TASK1_Ok.getType(), is(TaskType.PINGING));
    }


    @Test
    public void invalidAddressReturnsError() {
        //Given invalid addresses
        TASK1_Ok.settings.setPingAddress("1.1.1");

        // When the work is executed
        TASK1_Ok.doTheWork();
        TASK3_WrongUrl.doTheWork();

        String result = TASK1_Ok.getLastResultJson();

        // Then the corresponding results with error messages are returned

    }

    //TODO: ping ERROR_INVALID_ADDRESS = 1 - Regex are not in the unit test lib - Robolectric?????







    //TODO: check different results



    //TODO: getStateChange

    //TODO: isTimeToExecute

    //TODO: ERROR_TIMEOUT = 2;


}