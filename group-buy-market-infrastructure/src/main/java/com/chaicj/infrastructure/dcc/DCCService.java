package com.chaicj.infrastructure.dcc;

import com.chaicj.types.annotations.DCCValue;
import com.chaicj.types.common.Constants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DCCService {

    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @DCCValue("cutRange:100")
    private String cutRange;

    @DCCValue("scBlacklist:s02c02")
    private String scBlacklist;

    public boolean isDowngradeSwitch() {
        return "1".equals(downgradeSwitch);
    }

    public boolean isCutRange(String userId) {
        int hashCode = Math.abs(userId.hashCode());
        int lastTwo = hashCode % 100;
        if (lastTwo <= Integer.valueOf(cutRange)) {
            return true;
        }
        return false;
    }

    public Boolean isSCBlackIntercept(String source, String channel) {
        List<String> list = Arrays.asList(scBlacklist.split(Constants.SPLIT));
        return list.contains(source + channel);
    }
}
