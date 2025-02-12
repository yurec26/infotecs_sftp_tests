package unit;

import org.example.service.OperationService;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


public class OperationServiceTest {

    private final OperationService operationService = new OperationService();
    private Map<String, String> testMapAddresses;

    @BeforeTest
    public void setupTestData() {
        testMapAddresses = new HashMap<>();
        testMapAddresses.put("a.domain.ru", "177.69.255.166");
        testMapAddresses.put("b.domain.ru", "201.68.96.73");
        testMapAddresses.put("c.domain.ru", "178.125.28.164");
        testMapAddresses.put("d.domain.ru", "160.253.213.39");
        testMapAddresses.put("e.domain.ru", "136.81.21.85");
    }

    //
    @Test
    public void testValidIPv4() {
        Assert.assertTrue(operationService.isValidIPv4("192.168.1.1"), "Ожидается, что IP валидный");
    }

    @Test
    public void testInValidIPv4() {
        Assert.assertFalse(operationService.isValidIPv4("192.168.999.1"), "Ожидается, что IP невалидный");
    }

    @Test
    public void testEmptyIp() {
        Assert.assertFalse(operationService.isValidIPv4(""), "Ожидается, что IP невалидный");
    }

    //
    @Test
    public void testIPvIsUniqInCollection() {
        Assert.assertTrue(operationService.isIpUniq
                ("192.168.152.11", testMapAddresses), "Ожидается, что IP уникальный");
    }

    @Test
    public void testIPvIsNotUniqInCollection() {
        Assert.assertFalse(operationService.isIpUniq
                ("177.69.255.166", testMapAddresses), "Ожидается, что IP неуникальный");
    }
    //

}
