package integrated;

import com.jcraft.jsch.*;
import org.example.service.OperationService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Scanner;


public class SFTPTest {

    private ChannelSftp sftpChannel;
    private OperationService operationService;
    private final String testIpJsonFile = "test_ip_addresses.json";
    String host;
    String username;
    String password;
    int port = 22;

    @BeforeClass
    public void setUp() throws JSchException, SftpException {
        operationService = new OperationService();

        host = System.getProperty("host");
        username = System.getProperty("username");
        password = System.getProperty("password");

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "accept-new");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;

        sftpChannel.put(new ByteArrayInputStream(new byte[0]), testIpJsonFile);
    }


    @Test
    public void testWriteAndReadJson() throws SftpException, UnsupportedEncodingException {
        String domainToAdd = "test.domain 111.222.111.111";

        Map.Entry<String, String> expectedEntry = new AbstractMap.SimpleEntry<>("test.domain", "111.222.111.111");

        System.setIn(new ByteArrayInputStream(domainToAdd.getBytes()));
        Scanner scanner = new Scanner(System.in);
        operationService.addDomain(sftpChannel, scanner, testIpJsonFile);
        Map<String, String> fileContentMap = operationService.readMapOfIpAddresses(sftpChannel, testIpJsonFile);
        Map.Entry<String, String> firstEntry = fileContentMap.entrySet().iterator().next();

        Assert.assertFalse(fileContentMap.isEmpty());
        Assert.assertEquals(firstEntry.getKey(), expectedEntry.getKey());
        Assert.assertEquals(firstEntry.getValue(), expectedEntry.getValue());

        scanner.close();
        System.setIn(System.in);

    }

    @Test(dependsOnMethods = {"testWriteAndReadJson"})
    public void testGetIpByName() {
        String domainNameToFind = "test.domain";

        String expectedIp = "111.222.111.111";

        System.setIn(new ByteArrayInputStream(domainNameToFind.getBytes()));
        Scanner scanner = new Scanner(System.in);

        String actualIp = operationService.getIpByName(sftpChannel, scanner, testIpJsonFile);

        Assert.assertEquals(actualIp, expectedIp);

        scanner.close();
        System.setIn(System.in);
    }

    @Test(dependsOnMethods = {"testGetIpByName"})
    public void testNameByIp() {
        String iPToFind = "111.222.111.111";

        String expectedName = "test.domain";

        System.setIn(new ByteArrayInputStream(iPToFind.getBytes()));
        Scanner scanner = new Scanner(System.in);

        String actualName = operationService.getNameByIp(sftpChannel, scanner, testIpJsonFile);

        Assert.assertEquals(actualName, expectedName);

        scanner.close();
        System.setIn(System.in);
    }

    @Test(dependsOnMethods = {"testNameByIp"})
    public void deleteDomain() throws SftpException, UnsupportedEncodingException {
        String domainToDelete = "test.domain";

        System.setIn(new ByteArrayInputStream(domainToDelete.getBytes()));
        Scanner scanner = new Scanner(System.in);

        operationService.deleteDomain(sftpChannel, scanner, testIpJsonFile);

        Map<String, String> fileContentMap = operationService.readMapOfIpAddresses(sftpChannel, testIpJsonFile);
        Assert.assertTrue(fileContentMap.isEmpty());

        scanner.close();
        System.setIn(System.in);
    }


    @AfterClass
    public void closeAndClean() throws JSchException {
        try {
            sftpChannel.rm(testIpJsonFile);
            System.out.println("Файл " + testIpJsonFile + " успешно удален.");
        } catch (SftpException e) {
            System.err.println("Ошибка при удалении файла: " + e.getMessage());
        }
        if (sftpChannel != null && sftpChannel.isConnected()) {
            sftpChannel.exit();
        }
        try {
            sftpChannel.getSession().disconnect();
        } catch (JSchException | NullPointerException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
