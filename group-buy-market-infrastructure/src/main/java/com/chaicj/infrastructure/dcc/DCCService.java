package com.chaicj.infrastructure.dcc;

import com.chaicj.types.annotations.DCCValue;
import org.springframework.stereotype.Service;

@Service
public class DCCService {

    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @DCCValue("cutRange:100")
    private String cutRange;

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
}
