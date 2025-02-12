package unit;

import org.example.listener.OperationListener;
import org.example.server.ServerUtil;
import org.example.service.OperationService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OperationListenerTest {


    private OperationListener operationListener;

    @BeforeClass
    public void setup() {
        OperationService operationService = new OperationService();
        ServerUtil serverUtil = new ServerUtil(operationService);
        operationListener = new OperationListener(serverUtil);
    }

    @Test
    public void testValidInput() {
        String[] args = {"param1", "22", "param3", "param4"};

        int result = operationListener.throwIfIllegalInput(args);

        Assert.assertEquals(result, 22, "Ожидается, что значение будет 22 (порт)");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidNumberOfArguments() {
        String[] args = {"param1", "123", "param3"};

        operationListener.throwIfIllegalInput(args);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidNumberFormat() {

        String[] args = {"param1", "abc", "param3", "param4"};
        operationListener.throwIfIllegalInput(args);
    }
}
