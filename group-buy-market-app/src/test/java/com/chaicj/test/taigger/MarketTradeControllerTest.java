package com.chaicj.test.taigger;

import com.alibaba.fastjson.JSON;
import com.chaicj.api.IMarketTradeService;
import com.chaicj.api.dto.LockMarketPayOrderRequestDTO;
import com.chaicj.api.dto.LockMarketPayOrderResponseDTO;
import com.chaicj.api.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MarketTradeControllerTest {

    @Resource
    private IMarketTradeService marketTradeService;

    @Test
    public void test_lockMarketPayOrder_mq() throws Exception {
        LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO = new LockMarketPayOrderRequestDTO();
        lockMarketPayOrderRequestDTO.setUserId("chaicj");
        lockMarketPayOrderRequestDTO.setTeamId(null);
        lockMarketPayOrderRequestDTO.setActivityId(100123L);
        lockMarketPayOrderRequestDTO.setGoodsId("9890001");
        lockMarketPayOrderRequestDTO.setSource("s01");
        lockMarketPayOrderRequestDTO.setChannel("c01");
        lockMarketPayOrderRequestDTO.setNotifyMQ();
        lockMarketPayOrderRequestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));

        Response<LockMarketPayOrderResponseDTO> lockMarketPayOrderResponseDTOResponse = marketTradeService.lockMarketPayOrder(lockMarketPayOrderRequestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(lockMarketPayOrderRequestDTO), JSON.toJSONString(lockMarketPayOrderResponseDTOResponse));
    }

    @Test
    public void testLockMarketPayOrder() throws Exception {
        LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
        requestDTO.setUserId("lisi");
        requestDTO.setTeamId("05476313");
        requestDTO.setActivityId(100123L);
        requestDTO.setGoodsId("9890001");
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));
        requestDTO.setNotifyUrl("http://127.0.0.1:8091/api/v1/test/group_buy_notify");
        Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockMarketPayOrder(requestDTO);
        log.info("request: {}", JSON.toJSONString(requestDTO));
        log.info("response: {}", JSON.toJSONString(response));
    }

    @Test
    public void testLockMarketPayOrder1() throws Exception {
        LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
        requestDTO.setUserId("zhangsan");
        requestDTO.setTeamId("85646795");
        requestDTO.setActivityId(100123L);
        requestDTO.setGoodsId("9890001");
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));
        Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockMarketPayOrder(requestDTO);
        log.info("request: {}", JSON.toJSONString(requestDTO));
        log.info("response: {}", JSON.toJSONString(response));
    }
}
